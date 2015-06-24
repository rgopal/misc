package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class StudentInstructionService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(StudentInstruction studentInstruction, String username, int permission) {
        addPermission studentInstruction, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#studentInstruction, admin)")
    @Transactional
    void addPermission(StudentInstruction studentInstruction, String username,
        Permission permission) {
        aclUtilService.addPermission studentInstruction, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    StudentInstruction getNew(Map params) {
        def studentInstruction = new StudentInstruction()
        
     
        // use addTo for person so that hibernate works fine.  Find  the logged
        // user
        
        def person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new  ${studentInstruction}: ${error}")
            }
        } else
        person.addToStudentInstructions(studentInstruction)
        
        log.trace "getNew: creating new studentInstruction for $person"
      
        
        if (params.instruction)
        Instruction.findById(params.instruction?.id).addToStudentInstructions(studentInstruction)
        else
        log.warn "getNew: instruction should be non null"
    
        log.trace "getNew: new studentInstruction $studentInstruction instance created"
        studentInstruction
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    StudentInstruction create(Map params) {
        StudentInstruction studentInstruction = new StudentInstruction(params)
        if (!studentInstruction.save(flush:true)) {
            studentInstruction.errors.allErrors.each {
                log.warning ("create: error while saving studentInstruction ${studentInstruction}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission studentInstruction, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission studentInstruction, 'admin',
        BasePermission.READ
        
        studentInstruction
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.StudentInstruction', read) or hasPermission(#id, 'com.oumuo.central.StudentInstruction', admin) or hasRole('ROLE_USER')")
    StudentInstruction get(long id) {
        StudentInstruction.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<StudentInstruction> list(Map params) {
        StudentInstruction.list()
    }

    int count() {
        StudentInstruction.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentInstruction, write) or hasPermission(#studentInstruction, admin)")
    void update(studentInstruction, Map params) {
        
        log.trace "udpate: before binding ${studentInstruction}"    
        studentInstruction.properties = params
        if (!studentInstruction.save(flush:true)) {         
            studentInstruction.errors.allErrors.each {
                log.warning ("create: error while saving studentInstruction ${studentInstruction}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentInstruction, delete) or hasPermission(#studentInstruction, admin)")
    void delete(StudentInstruction studentInstruction) {
        studentInstruction.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl studentInstruction
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentInstruction, admin)")
    void deletePermission(StudentInstruction studentInstruction, String username, Permission permission) {
        def acl = aclUtilService.readAcl(studentInstruction)

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