package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ProgramService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Program program, String username, int permission) {
        addPermission program, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#program, admin)")
    @Transactional
    void addPermission(Program program, String username,
        Permission permission) {
        aclUtilService.addPermission program, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Program getNew(Map params) {
        def program = new Program()
        
        // first find the person who is authoring the comment
        program.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!program.person) {
            program.errors.allErrors.each {
                log.warning ("create: error while getting new program ${program}: ${error}")
            }
        } else
        log.trace "getNew: creating new program for $program.person"
         
        program.organization = Organization.findById(params.organization?.id)
       
        
        log.trace "getNew: new program $program instance created"
        program
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Program create(Map params) {
        Program program = new Program(params)
        if (!program.save(flush:true)) {
            program.errors.allErrors.each {
                log.warning ("create: error while saving program ${program}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission program, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission program, 'admin',
        BasePermission.READ
        
        program
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Program', read) or hasPermission(#id, 'com.oumuo.central.Program', admin) or hasRole('ROLE_USER')")
    Program get(long id) {
        Program.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Program> list(Map params) {
        Program.list()
    }

    int count() {
        Program.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#program, write) or hasPermission(#program, admin)")
    void update(program, Map params) {
        
        log.trace "udpate: before binding ${program}"    
        program.properties = params
        if (!program.save(flush:true)) {         
            program.errors.allErrors.each {
                log.warning ("create: error while saving program ${program}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#program, delete) or hasPermission(#program, admin)")
    void delete(Program program) {
        program.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl program
    }

    @Transactional
    @PreAuthorize("hasPermission(#program, admin)")
    void deletePermission(Program program, String username, Permission permission) {
        def acl = aclUtilService.readAcl(program)

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