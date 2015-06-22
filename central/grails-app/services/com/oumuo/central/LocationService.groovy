package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class LocationService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Location location, String username, int permission) {
        addPermission location, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#location, admin)")
    @Transactional
    void addPermission(Location location, String username,
        Permission permission) {
        aclUtilService.addPermission location, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Location getNew(Map params) {
        def location = new Location()
        
       /* // first find the person who is authoring the comment
        location.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!location.person) {
            location.errors.allErrors.each {
                log.warning ("create: error while getting new location ${location}: ${error}")
            }
        } else
        log.trace "getNew: creating new location for $location.person"
         
        */
       
        // don't care if it is null
        location.organization = Organization.findById(params.organization?.id)
       
        
        log.trace "getNew: new location $location instance created"
        location
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Location create(Map params) {
        Location location = new Location(params)
        if (!location.save(flush:true)) {
            location.errors.allErrors.each {
                log.warning ("create: error while saving location ${location}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission location, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission location, 'admin',
        BasePermission.READ
        
        location
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Location', read) or hasPermission(#id, 'com.oumuo.central.Location', admin) or hasRole('ROLE_USER')")
    Location get(long id) {
        Location.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Location> list(Map params) {
        Location.list()
    }

    int count() {
        Location.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#location, write) or hasPermission(#location, admin)")
    void update(location, Map params) {
        
        log.trace "udpate: before binding ${location}"    
        location.properties = params
        if (!location.save(flush:true)) {         
            location.errors.allErrors.each {
                log.warning ("create: error while saving location ${location}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#location, delete) or hasPermission(#location, admin)")
    void delete(Location location) {
        location.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl location
    }

    @Transactional
    @PreAuthorize("hasPermission(#location, admin)")
    void deletePermission(Location location, String username, Permission permission) {
        def acl = aclUtilService.readAcl(location)

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