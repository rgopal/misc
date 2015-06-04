package com.oumuo.central

import com.oumuo.central.Ranking

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class RankingInstanceService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Ranking ranking, String username, int permission) {
        addPermission ranking, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#ranking, admin)")
    @Transactional
    void addPermission(Ranking ranking, String username,
        Permission permission) {
        aclUtilService.addPermission ranking, username, permission
    }
    
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    Ranking getNew(Map params) {
     
        // this serves the purpose to drive a form but save again gets a new
        // after the form returns
        def ranking = new Ranking()
        
        // first find the person who is authoring the ranking (has to be admin)
        ranking.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (not using belongsTo for flexibility)
        if (!ranking.person) {
            ranking.errors.allErrors.each {
                log.warning ("getNew: error while getting new ranking ${ranking}: ${it}")
            }
        } else
            log.trace "getNew: creating new ranking for person $ranking.person"
        
        ranking.organization = Organization.findById(params.organization?.id)
        ranking.program = Program.findById(params.program?.id)
 
  
        // check for other domains which would have ranking (only one would be
        // with non null value in params
        ranking
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    Ranking create(Map params) {
        Ranking ranking = new Ranking(params)
        if (!ranking.save(flush:true)) {
            ranking.errors.allErrors.each {
                log.warning ("create: error while saving ranking ${ranking}: ${it}")
            }
        }
      
        // Grant the current principal administrative permission
        addPermission ranking, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission ranking, 'admin',
        BasePermission.READ
        
        ranking
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Ranking', read) or hasPermission(#id, 'com.oumuo.central.Ranking', admin) or hasRole('ROLE_USER')")
    Ranking get(long id) {
        Ranking.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)  or hasRole('ROLE_READ_ALL')")
    List<Ranking> list(Map params) {
        Ranking.list()
    }

    int count() {
        Ranking.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#ranking, write) or hasPermission(#ranking, admin)")
    void update(ranking, Map params) {
        
        log.trace "udpate: before binding ${ranking}"    
        ranking.properties = params
        if (!ranking.save(flush:true)) {         
            ranking.errors.allErrors.each {
                log.warning ("create: error while saving ranking ${ranking}: ${it}")
            }
        }
            
    }

    @Transactional
    @PreAuthorize("hasPermission(#ranking, delete) or hasPermission(#ranking, admin)")
    void delete(Ranking ranking) {
        ranking.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl ranking
    }

    @Transactional
    @PreAuthorize("hasPermission(#ranking, admin)")
    void deletePermission(Ranking ranking, String username, Permission permission) {
        def acl = aclUtilService.readAcl(ranking)

        // Remove all permissions associated with this particular
        // recipient (string equality to KISS)
        acl.entries.eachWithIndex { entry, i ->
            if (entry.sid.equals(recipient) &&
                entry.permission.equals(permission)) {
                acl.deleteAce i
            }
        }

        aclService.updateAcl acl
    }
}