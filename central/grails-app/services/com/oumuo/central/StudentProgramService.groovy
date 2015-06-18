package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class StudentProgramService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(StudentProgram studentProgram, String username, int permission) {
        addPermission studentProgram, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#studentProgram, admin)")
    @Transactional
    void addPermission(StudentProgram studentProgram, String username,
        Permission permission) {
        aclUtilService.addPermission studentProgram, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    StudentProgram getNew(Map params) {
        def studentProgram = new StudentProgram()
       
         
            
        if (params.program)
        Program.findById(params.program.id).addToStudentPrograms(studentProgram)
        else  if (params.person)
        Person.findById(params.person.id).addToStudentPrograms(studentProgram)
        else
        log.warn "getNew:  one of person or program should be non null"
        
        log.trace "getNew: new studentProgram $studentProgram instance created"
        studentProgram
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    StudentProgram create(Map params) {
        StudentProgram studentProgram = new StudentProgram(params)
        if (!studentProgram.save(flush:true)) {
            studentProgram.errors.allErrors.each {
                log.warning ("create: error while saving studentProgram ${studentProgram}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission studentProgram, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission studentProgram, 'admin',
        BasePermission.READ
        
        studentProgram
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.StudentProgram', read) or hasPermission(#id, 'com.oumuo.central.StudentProgram', admin) or hasRole('ROLE_USER')")
    StudentProgram get(long id) {
        StudentProgram.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<StudentProgram> list(Map params) {
        StudentProgram.list()
    }

    int count() {
        StudentProgram.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentProgram, write) or hasPermission(#studentProgram, admin)")
    void update(studentProgram, Map params) {
        
        log.trace "udpate: before binding ${studentProgram}"    
        studentProgram.properties = params
        if (!studentProgram.save(flush:true)) {         
            studentProgram.errors.allErrors.each {
                log.warning ("create: error while saving studentProgram ${studentProgram}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentProgram, delete) or hasPermission(#studentProgram, admin)")
    void delete(StudentProgram studentProgram) {
        studentProgram.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl studentProgram
    }

    @Transactional
    @PreAuthorize("hasPermission(#studentProgram, admin)")
    void deletePermission(StudentProgram studentProgram, String username, Permission permission) {
        def acl = aclUtilService.readAcl(studentProgram)

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