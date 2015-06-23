package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class LocationUseService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(LocationUse locationUse, String username, int permission) {
        addPermission locationUse, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#locationUse, admin)")
    @Transactional
    void addPermission(LocationUse locationUse, String username,
        Permission permission) {
        aclUtilService.addPermission locationUse, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    LocationUse getNew(Map params) {
        def locationUse = new LocationUse()
        
     
        // first find the person who is authoring the comment. Eventually this
        // will be done when some one creates a course (then related LocationUse
        // also gets created.
        
        if (params.location)
        Location.findById(params.location?.id).addToLocationUses(locationUse)
        else if (params.clazs)
        Clazs.findById(params.clazs?.id).addToLocationUses(locationUse)
        else if (params.classSession)
        ClassSession.findById(params.classSession?.id).addToLocationUses(locationUse)
          else if (params.person)
        Person.findById(params.person?.id).addToLocationUses(locationUse)
          log.warn "getNew: at least one of location, person, clazs, or classSession should be non null"
        
        log.trace "getNew: new locationUse $locationUse instance created"
        locationUse
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    LocationUse create(Map params) {
        LocationUse locationUse = new LocationUse(params)
        if (!locationUse.save(flush:true)) {
            locationUse.errors.allErrors.each {
                log.warning ("create: error while saving locationUse ${locationUse}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission locationUse, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission locationUse, 'admin',
        BasePermission.READ
        
        locationUse
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.LocationUse', read) or hasPermission(#id, 'com.oumuo.central.LocationUse', admin) or hasRole('ROLE_USER')")
    LocationUse get(long id) {
        LocationUse.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<LocationUse> list(Map params) {
        LocationUse.list()
    }

    int count() {
        LocationUse.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#locationUse, write) or hasPermission(#locationUse, admin)")
    void update(locationUse, Map params) {
        
        log.trace "udpate: before binding ${locationUse}"    
        locationUse.properties = params
        if (!locationUse.save(flush:true)) {         
            locationUse.errors.allErrors.each {
                log.warning ("create: error while saving locationUse ${locationUse}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#locationUse, delete) or hasPermission(#locationUse, admin)")
    void delete(LocationUse locationUse) {
        locationUse.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl locationUse
    }

    @Transactional
    @PreAuthorize("hasPermission(#locationUse, admin)")
    void deletePermission(LocationUse locationUse, String username, Permission permission) {
        def acl = aclUtilService.readAcl(locationUse)

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