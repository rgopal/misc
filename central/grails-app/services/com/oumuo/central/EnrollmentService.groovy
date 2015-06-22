package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class EnrollmentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Enrollment enrollment, String username, int permission) {
        addPermission enrollment, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#enrollment, admin)")
    @Transactional
    void addPermission(Enrollment enrollment, String username,
        Permission permission) {
        aclUtilService.addPermission enrollment, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    Enrollment getNew(Map params) {
        def enrollment = new Enrollment()
        
     
        // first find the person who is authoring the comment. Eventually this
        // will be done when some one creates a course (then related Enrollment
        // also gets created.
        enrollment.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!enrollment.person) {
            enrollment.errors.allErrors.each {
                log.warning ("create: error while getting new enrollment ${enrollment}: ${error}")
            }
        } else
        log.trace "getNew: creating new enrollment for $enrollment.person"
        // could reach it by any one of the following
        
        if (params.program)
        Program.findById(params.program?.id).addToEnrollments(enrollment)
        else if (params.term)
        Term.findById(params.term?.id).addToEnrollments(enrollment)
        else if (params.clazs)
        Clazs.findById(params.clazs?.id).addToEnrollments(enrollment)
          else if (params.classSession)
        ClassSession.findById(params.classSession?.id).addToEnrollments(enrollment)
         else if (params.instruction)
        Instruction.findById(params.instruction?.id).addToEnrollments(enrollment)
        else
        log.warn "getNew: at least one item should be non null"
        
        log.trace "getNew: new enrollment $enrollment instance created"
        enrollment
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Enrollment create(Map params) {
        Enrollment enrollment = new Enrollment(params)
        if (!enrollment.save(flush:true)) {
            enrollment.errors.allErrors.each {
                log.warning ("create: error while saving enrollment ${enrollment}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission enrollment, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission enrollment, 'admin',
        BasePermission.READ
        
        enrollment
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Enrollment', read) or hasPermission(#id, 'com.oumuo.central.Enrollment', admin) or hasRole('ROLE_USER')")
    Enrollment get(long id) {
        Enrollment.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Enrollment> list(Map params) {
        Enrollment.list()
    }

    int count() {
        Enrollment.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#enrollment, write) or hasPermission(#enrollment, admin)")
    void update(enrollment, Map params) {
        
        log.trace "udpate: before binding ${enrollment}"    
        enrollment.properties = params
        if (!enrollment.save(flush:true)) {         
            enrollment.errors.allErrors.each {
                log.warning ("create: error while saving enrollment ${enrollment}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#enrollment, delete) or hasPermission(#enrollment, admin)")
    void delete(Enrollment enrollment) {
        enrollment.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl enrollment
    }

    @Transactional
    @PreAuthorize("hasPermission(#enrollment, admin)")
    void deletePermission(Enrollment enrollment, String username, Permission permission) {
        def acl = aclUtilService.readAcl(enrollment)

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