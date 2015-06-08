package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CourseObjectiveService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(CourseObjective courseObjective, String username, int permission) {
        addPermission courseObjective, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#courseObjective, admin)")
    @Transactional
    void addPermission(CourseObjective courseObjective, String username,
        Permission permission) {
        aclUtilService.addPermission courseObjective, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseObjective getNew(Map params) {
        def courseObjective = new CourseObjective()
        
        // first find the person who is authoring the comment
        courseObjective.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!courseObjective.person) {
            courseObjective.errors.allErrors.each {
                log.warning ("create: error while getting new courseObjective ${courseObjective}: ${it}")
            }
        } else
        log.trace "getNew: creating new courseObjective for $courseObjective.person"
         
        courseObjective.organization = Organization.findById(params.organization?.id)
       
        
        log.trace "getNew: new courseObjective $courseObjective instance created"
        courseObjective
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseObjective create(Map params) {
        CourseObjective courseObjective = new CourseObjective(params)
        if (!courseObjective.save(flush:true)) {
            courseObjective.errors.allErrors.each {
                log.warning ("create: error while saving courseObjective ${courseObjective}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission courseObjective, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission courseObjective, 'admin',
        BasePermission.READ
        
        courseObjective
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.CourseObjective', read) or hasPermission(#id, 'com.oumuo.central.CourseObjective', admin) or hasRole('ROLE_USER')")
    CourseObjective get(long id) {
        CourseObjective.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<CourseObjective> list(Map params) {
        CourseObjective.list()
    }

    int count() {
        CourseObjective.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseObjective, write) or hasPermission(#courseObjective, admin)")
    void update(courseObjective, Map params) {
        
        log.trace "udpate: before binding ${courseObjective}"    
        courseObjective.properties = params
        if (!courseObjective.save(flush:true)) {         
            courseObjective.errors.allErrors.each {
                log.warning ("create: error while saving courseObjective ${courseObjective}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseObjective, delete) or hasPermission(#courseObjective, admin)")
    void delete(CourseObjective courseObjective) {
        courseObjective.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl courseObjective
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseObjective, admin)")
    void deletePermission(CourseObjective courseObjective, String username, Permission permission) {
        def acl = aclUtilService.readAcl(courseObjective)

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