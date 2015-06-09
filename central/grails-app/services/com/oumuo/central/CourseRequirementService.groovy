package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CourseRequirementService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(CourseRequirement courseRequirement, String username, int permission) {
        addPermission courseRequirement, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#courseRequirement, admin)")
    @Transactional
    void addPermission(CourseRequirement courseRequirement, String username,
        Permission permission) {
        aclUtilService.addPermission courseRequirement, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseRequirement getNew(Map params) {
        def courseRequirement = new CourseRequirement()
        
        
        if (!params.course) {
            courseRequirement.errors.allErrors.each {
                log.warning ("create: error while getting new courseRequirement ${courseRequirement}: ${it}")
            }
        } else {
            courseRequirement.course = Course.findById(params.course.id)
            log.trace "getNew: creating new courseRequirement for $courseRequirement"
        }
        
        
        log.trace "getNew: new courseRequirement $courseRequirement instance created"
        courseRequirement
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseRequirement create(Map params) {
        CourseRequirement courseRequirement = new CourseRequirement(params)
        if (!courseRequirement.save(flush:true)) {
            courseRequirement.errors.allErrors.each {
                log.warning ("create: error while saving courseRequirement ${courseRequirement}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission courseRequirement, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission courseRequirement, 'admin',
        BasePermission.READ
        
        courseRequirement
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.CourseRequirement', read) or hasPermission(#id, 'com.oumuo.central.CourseRequirement', admin) or hasRole('ROLE_USER')")
    CourseRequirement get(long id) {
        CourseRequirement.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<CourseRequirement> list(Map params) {
        CourseRequirement.list()
    }

    int count() {
        CourseRequirement.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRequirement, write) or hasPermission(#courseRequirement, admin)")
    void update(courseRequirement, Map params) {
        
        log.trace "udpate: before binding ${courseRequirement}"    
        courseRequirement.properties = params
        if (!courseRequirement.save(flush:true)) {         
            courseRequirement.errors.allErrors.each {
                log.warning ("create: error while saving courseRequirement ${courseRequirement}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRequirement, delete) or hasPermission(#courseRequirement, admin)")
    void delete(CourseRequirement courseRequirement) {
        courseRequirement.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl courseRequirement
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRequirement, admin)")
    void deletePermission(CourseRequirement courseRequirement, String username, Permission permission) {
        def acl = aclUtilService.readAcl(courseRequirement)

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