package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class EventService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Event event, String username, int permission) {
        addPermission event, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#event, admin)")
    @Transactional
    void addPermission(Event event, String username,
        Permission permission) {
        aclUtilService.addPermission event, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    Event getNew(Map params) {
        def event = new Event()
        
    
      if (params.classSession)
        ClassSession.findById(params.classSession?.id).addToLocationUses(locationUse)
          else if (params.clazs)
        Clazs.findById(params.clazs?.id).addToLocationUses(locationUse)
          log.warn "getNew: at least one of clazs, or classSession should be non null"
        
        
        log.trace "getNew: new event $event instance created"
        event
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Event create(Map params) {
        Event event = new Event(params)
        if (!event.save(flush:true)) {
            event.errors.allErrors.each {
                log.warning ("create: error while saving event ${event}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission event, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission event, 'admin',
        BasePermission.READ
        
        event
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Event', read) or hasPermission(#id, 'com.oumuo.central.Event', admin) or hasRole('ROLE_USER')")
    Event get(long id) {
        Event.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Event> list(Map params) {
        Event.list()
    }

    int count() {
        Event.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#event, write) or hasPermission(#event, admin)")
    void update(event, Map params) {
        
        log.trace "udpate: before binding ${event}"    
        event.properties = params
        if (!event.save(flush:true)) {         
            event.errors.allErrors.each {
                log.warning ("create: error while saving event ${event}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#event, delete) or hasPermission(#event, admin)")
    void delete(Event event) {
        event.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl event
    }

    @Transactional
    @PreAuthorize("hasPermission(#event, admin)")
    void deletePermission(Event event, String username, Permission permission) {
        def acl = aclUtilService.readAcl(event)

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