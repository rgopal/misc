package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class LearningService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Learning learning, String username, int permission) {
        addPermission learning, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#learning, admin)")
    @Transactional
    void addPermission(Learning learning, String username,
        Permission permission) {
        aclUtilService.addPermission learning, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Learning getNew(Map params) {
        def learning = new Learning()
       
         
        learning.organization = Organization.findById(params.organization?.id)
       
        
        log.trace "getNew: new learning $learning instance created for organization $organization"
        learning
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Learning create(Map params) {
        Learning learning = new Learning(params)
        if (!learning.save(flush:true)) {
            learning.errors.allErrors.each {
                log.warning ("create: error while saving learning ${learning}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission learning, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission learning, 'admin',
        BasePermission.READ
        
        learning
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Learning', read) or hasPermission(#id, 'com.oumuo.central.Learning', admin) or hasRole('ROLE_USER')")
    Learning get(long id) {
        Learning.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Learning> list(Map params) {
        Learning.list()
    }

    int count() {
        Learning.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#learning, write) or hasPermission(#learning, admin)")
    void update(learning, Map params) {
        
        log.trace "udpate: before binding ${learning}"    
        learning.properties = params
        if (!learning.save(flush:true)) {         
            learning.errors.allErrors.each {
                log.warning ("create: error while saving learning ${learning}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#learning, delete) or hasPermission(#learning, admin)")
    void delete(Learning learning) {
        learning.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl learning
    }

    @Transactional
    @PreAuthorize("hasPermission(#learning, admin)")
    void deletePermission(Learning learning, String username, Permission permission) {
        def acl = aclUtilService.readAcl(learning)

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