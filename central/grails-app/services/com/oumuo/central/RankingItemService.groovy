package com.oumuo.central

import com.oumuo.central.RankingItem

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class RankingItemService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(RankingItem rankingItem, String username, int permission) {
        addPermission rankingItem, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#rankingItem, admin)")
    @Transactional
    void addPermission(RankingItem rankingItem, String username,
        Permission permission) {
        aclUtilService.addPermission rankingItem, username, permission
    }
    
    @PreAuthorize("hasRole('ROLE_USER')")
    RankingItem getNew(Map params) {
     
        // this serves the purpose to drive a form but save again gets a new
        // after the form returns
        def rankingItem = new RankingItem()
        
        // first find the person who is authoring the rankingItem (has to be admin)
        rankingItem.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (using belongsTo for flexibility)
        if (!rankingItem.person) {
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while getting new rankingItem ${rankingItem}: ${it}")
            }
        } else
        log.trace "getNew: creating new rankingItem for person $rankingItem.person"
        println params.collect{it}.join('\n')
        
        // find the parent Ranking object for this item (don't directly create it from person)
        //TODO: remove from Person association??
        def ranking = Ranking.findById(params.ranking.id)
        println ranking.properties.collect{it}
          
        // ALl PROPS println r.domainClass.persistentProperties.collect{it}.join('\n')
        
        // find the parent for this item
        if (ranking.program) {
            // check if this is for a program
            log.trace ("getNew: found organizatoin $ranking.program")
            rankingItem.programRanking = ranking
            
        } else if (ranking.organization) {
            // or for the parent Ranking has an organization (can't be both
            log.trace ("getNew: found organization $ranking.organization")
            rankingItem.organizationRanking = ranking   
        } else if (ranking.course) {
            // or for the parent Ranking has an organization (can't be both
            log.trace ("getNew: found course $ranking.course")
            rankingItem.courseRanking = ranking   
        }
        else
            log.warn "getNew: did not find either prgoram, course or organization in Ranking"
            
        rankingItem
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    RankingItem create(Map params) {
        RankingItem rankingItem = new RankingItem(params)
        if (!rankingItem.save(flush:true)) {
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while saving rankingItem ${rankingItem}: ${it}")
            }
        }
      
        // Grant the current principal administrative permission
        addPermission rankingItem, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission rankingItem, 'admin',
        BasePermission.READ
        
        rankingItem
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.RankingItem', read) or hasPermission(#id, 'com.oumuo.central.RankingItem', admin) or hasRole('ROLE_READ_ALL')")
    RankingItem get(long id) {
        RankingItem.get id
    }

    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)  or hasRole('ROLE_READ_ALL')")
    List<RankingItem> list(Map params) {
        RankingItem.list()
    }

    int count() {
        RankingItem.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, write) or hasPermission(#rankingItem, admin)")
    void update(rankingItem, Map params) {
        
        log.trace "udpate: before binding ${rankingItem}"    
        rankingItem.properties = params
        if (!rankingItem.save(flush:true)) {         
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while saving rankingItem ${rankingItem}: ${it}")
            }
        }
            
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, delete) or hasPermission(#rankingItem, admin)")
    void delete(RankingItem rankingItem) {
        rankingItem.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl rankingItem
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, admin)")
    void deletePermission(RankingItem rankingItem, String username, Permission permission) {
        def acl = aclUtilService.readAcl(rankingItem)

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