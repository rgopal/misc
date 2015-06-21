package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ClazsService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Clazs clazs, String username, int permission) {
        addPermission clazs, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#clazs, admin)")
    @Transactional
    void addPermission(Clazs clazs, String username,
        Permission permission) {
        aclUtilService.addPermission clazs, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Clazs getNew(Map params) {
        def clazs = new Clazs()
        
     
        // could reach it by any one of the following
        
        if (params.course)
        Course.findById(params.course?.id).addToClazss(clazs)
        else if (params.term)
        Term.findById(params.term?.id).addToClazss(clazs)
    //    else if (params.location)
    //    Location.findById(params.location?.id).addToClazss(clazs)
        
        log.trace "getNew: new clazs $clazs instance created"
        clazs
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Clazs create(Map params) {
        Clazs clazs = new Clazs(params)
        if (!clazs.save(flush:true)) {
            clazs.errors.allErrors.each {
                log.warning ("create: error while saving clazs ${clazs}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission clazs, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission clazs, 'admin',
        BasePermission.READ
        
        clazs
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Clazs', read) or hasPermission(#id, 'com.oumuo.central.Clazs', admin) or hasRole('ROLE_USER')")
    Clazs get(long id) {
        Clazs.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Clazs> list(Map params) {
        Clazs.list()
    }

    int count() {
        Clazs.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#clazs, write) or hasPermission(#clazs, admin)")
    void update(clazs, Map params) {
        
        log.trace "udpate: before binding ${clazs}"    
        clazs.properties = params
        if (!clazs.save(flush:true)) {         
            clazs.errors.allErrors.each {
                log.warning ("create: error while saving clazs ${clazs}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#clazs, delete) or hasPermission(#clazs, admin)")
    void delete(Clazs clazs) {
        clazs.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl clazs
    }

    @Transactional
    @PreAuthorize("hasPermission(#clazs, admin)")
    void deletePermission(Clazs clazs, String username, Permission permission) {
        def acl = aclUtilService.readAcl(clazs)

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