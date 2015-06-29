package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PositionProgramService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PositionProgram positionProgram, String username, int permission) {
        addPermission positionProgram, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#positionProgram, admin)")
    @Transactional
    void addPermission(PositionProgram positionProgram, String username,
        Permission permission) {
        aclUtilService.addPermission positionProgram, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    PositionProgram getNew(Map params) {
        def positionProgram = new PositionProgram()
        
     
        // could reach it by any one of the following
        
        if (params.program)
        Program.findById(params.program?.id).addToPositionPrograms(positionProgram)
        else if (params.position)
        Position.findById(params.position?.id).addToPositionPrograms(positionProgram)
  
        log.trace "getNew: new positionProgram $positionProgram instance created"
        positionProgram
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    PositionProgram create(Map params) {
        PositionProgram positionProgram = new PositionProgram(params)
        if (!positionProgram.save(flush:true)) {
            positionProgram.errors.allErrors.each {
                log.warning ("create: error while saving positionProgram ${positionProgram}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission positionProgram, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission positionProgram, 'admin',
        BasePermission.READ
        
        positionProgram
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PositionProgram', read) or hasPermission(#id, 'com.oumuo.central.PositionProgram', admin) or hasRole('ROLE_USER')")
    PositionProgram get(long id) {
        PositionProgram.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<PositionProgram> list(Map params) {
        PositionProgram.list()
    }

    int count() {
        PositionProgram.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#positionProgram, write) or hasPermission(#positionProgram, admin)")
    void update(positionProgram, Map params) {
        
        log.trace "udpate: before binding ${positionProgram}"    
        positionProgram.properties = params
        if (!positionProgram.save(flush:true)) {         
            positionProgram.errors.allErrors.each {
                log.warning ("create: error while saving positionProgram ${positionProgram}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#positionProgram, delete) or hasPermission(#positionProgram, admin)")
    void delete(PositionProgram positionProgram) {
        positionProgram.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl positionProgram
    }

    @Transactional
    @PreAuthorize("hasPermission(#positionProgram, admin)")
    void deletePermission(PositionProgram positionProgram, String username, Permission permission) {
        def acl = aclUtilService.readAcl(positionProgram)

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