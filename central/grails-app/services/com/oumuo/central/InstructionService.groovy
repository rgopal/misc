package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class InstructionService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Instruction instruction, String username, int permission) {
        addPermission instruction, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#instruction, admin)")
    @Transactional
    void addPermission(Instruction instruction, String username,
        Permission permission) {
        aclUtilService.addPermission instruction, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Instruction getNew(Map params) {
        def instruction = new Instruction()
        
     
        // could reach it by any one of the following
        
        if (params.classSession)
        ClassSession.findById(params.classSession?.id).addToInstructions(instruction)
        else if (params.learning)
        Learning.findById(params.learning?.id).addToInstructions(instruction)
        else
        log.warn "getNew: both classSession and learning are null for $instruction"
        
        log.trace "getNew: new instruction $instruction instance created"
        instruction
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Instruction create(Map params) {
        Instruction instruction = new Instruction(params)
        if (!instruction.save(flush:true)) {
            instruction.errors.allErrors.each {
                log.warning ("create: error while saving instruction ${instruction}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission instruction, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission instruction, 'admin',
        BasePermission.READ
        
        instruction
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Instruction', read) or hasPermission(#id, 'com.oumuo.central.Instruction', admin) or hasRole('ROLE_USER')")
    Instruction get(long id) {
        Instruction.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Instruction> list(Map params) {
        Instruction.list()
    }

    int count() {
        Instruction.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#instruction, write) or hasPermission(#instruction, admin)")
    void update(instruction, Map params) {
        
        log.trace "udpate: before binding ${instruction}"    
        instruction.properties = params
        if (!instruction.save(flush:true)) {         
            instruction.errors.allErrors.each {
                log.warning ("create: error while saving instruction ${instruction}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#instruction, delete) or hasPermission(#instruction, admin)")
    void delete(Instruction instruction) {
        instruction.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl instruction
    }

    @Transactional
    @PreAuthorize("hasPermission(#instruction, admin)")
    void deletePermission(Instruction instruction, String username, Permission permission) {
        def acl = aclUtilService.readAcl(instruction)

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