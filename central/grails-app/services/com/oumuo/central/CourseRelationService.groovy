package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CourseRelationService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(CourseRelation courseRelation, String username, int permission) {
        addPermission courseRelation, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#courseRelation, admin)")
    @Transactional
    void addPermission(CourseRelation courseRelation, String username,
        Permission permission) {
        aclUtilService.addPermission courseRelation, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseRelation getNew(Map params) {
        def courseRelation = new CourseRelation()
        
     
         
        Program.findById(params.program?.id).addToCourseRelations(courseRelation)
       
        
        log.trace "getNew: new courseRelation $courseRelation instance created for $courseRelation.program"
        courseRelation
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    CourseRelation create(Map params) {
        CourseRelation courseRelation = new CourseRelation(params)
        if (!courseRelation.save(flush:true)) {
            courseRelation.errors.allErrors.each {
                log.warning ("create: error while saving courseRelation ${courseRelation}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission courseRelation, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission courseRelation, 'admin',
        BasePermission.READ
        
        courseRelation
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.CourseRelation', read) or hasPermission(#id, 'com.oumuo.central.CourseRelation', admin) or hasRole('ROLE_USER')")
    CourseRelation get(long id) {
        CourseRelation.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<CourseRelation> list(Map params) {
        CourseRelation.list()
    }

    int count() {
        CourseRelation.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRelation, write) or hasPermission(#courseRelation, admin)")
    void update(courseRelation, Map params) {
        
        log.trace "udpate: before binding ${courseRelation}"    
        courseRelation.properties = params
        if (!courseRelation.save(flush:true)) {         
            courseRelation.errors.allErrors.each {
                log.warning ("create: error while saving courseRelation ${courseRelation}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRelation, delete) or hasPermission(#courseRelation, admin)")
    void delete(CourseRelation courseRelation) {
        courseRelation.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl courseRelation
    }

    @Transactional
    @PreAuthorize("hasPermission(#courseRelation, admin)")
    void deletePermission(CourseRelation courseRelation, String username, Permission permission) {
        def acl = aclUtilService.readAcl(courseRelation)

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