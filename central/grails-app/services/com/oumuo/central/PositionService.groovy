package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PositionService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Position position, String username, int permission) {
        addPermission position, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#position, admin)")
    @Transactional
    void addPermission(Position position, String username,
        Permission permission) {
        aclUtilService.addPermission position, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Position getNew(Map params) {
        def position = new Position()
        
  
        
        // could reach it by any one of the following
        
        if (params.job)
        Job.findById(params.job?.id).addToPositions(position)
       
        
        log.trace "getNew: new position $position instance created"
        position
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Position create(Map params) {
        Position position = new Position(params)
       
        if (!position.save(flush:true)) {
            position.errors.allErrors.each {
                log.warning ("create: error while saving position ${position}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission position, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission position, 'admin',
        BasePermission.READ
        
        position
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Position', read) or hasPermission(#id, 'com.oumuo.central.Position', admin) or hasRole('ROLE_USER')")
    Position get(long id) {
        Position.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Position> list(Map params) {
        Position.list()
    }

    int count() {
        Position.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#position, write) or hasPermission(#position, admin)")
    void update(position, Map params) {
        
        log.trace "udpate: before binding ${position}"    
        position.properties = params
        if (!position.save(flush:true)) {         
            position.errors.allErrors.each {
                log.warning ("create: error while saving position ${position}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#position, delete) or hasPermission(#position, admin)")
    void delete(Position position) {
        position.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl position
    }

    @Transactional
    @PreAuthorize("hasPermission(#position, admin)")
    void deletePermission(Position position, String username, Permission permission) {
        def acl = aclUtilService.readAcl(position)

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