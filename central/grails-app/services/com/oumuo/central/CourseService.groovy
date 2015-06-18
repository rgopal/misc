package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CourseService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Course course, String username, int permission) {
        addPermission course, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#course, admin)")
    @Transactional
    void addPermission(Course course, String username,
        Permission permission) {
        aclUtilService.addPermission course, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Course getNew(Map params) {
        def course = new Course()
        
      /*  // first find the person who is authoring the comment
        course.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!course.person) {
            course.errors.allErrors.each {
                log.warning ("create: error while getting new course ${course}: ${it}")
            }
        } else
        log.trace "getNew: creating new course for $course.person"
        
        */
        Organization.findById(params.organization?.id).addToCourses(course)
       
        
        log.trace "getNew: new course $course instance created"
        course
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Course create(Map params) {
        Course course = new Course(params)
        if (!course.save(flush:true)) {
            course.errors.allErrors.each {
                log.warning ("create: error while saving course ${course}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission course, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission course, 'admin',
        BasePermission.READ
        
        course
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Course', read) or hasPermission(#id, 'com.oumuo.central.Course', admin) or hasRole('ROLE_USER')")
    Course get(long id) {
        Course.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Course> list(Map params) {
        Course.list()
    }

    int count() {
        Course.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#course, write) or hasPermission(#course, admin)")
    void update(course, Map params) {
        
        log.trace "udpate: before binding ${course}"    
        course.properties = params
        if (!course.save(flush:true)) {         
            course.errors.allErrors.each {
                log.warning ("create: error while saving course ${course}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#course, delete) or hasPermission(#course, admin)")
    void delete(Course course) {
        course.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl course
    }

    @Transactional
    @PreAuthorize("hasPermission(#course, admin)")
    void deletePermission(Course course, String username, Permission permission) {
        def acl = aclUtilService.readAcl(course)

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