package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ObjectiveService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Objective objective, String username, int permission) {
        addPermission objective, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#objective, admin)")
    @Transactional
    void addPermission(Objective objective, String username,
        Permission permission) {
        aclUtilService.addPermission objective, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Objective getNew(Map params) {
        def objective = new Objective()
    
        
        // this has to be non null (since CourseRequirement belongsTo Course
        if (params.course) 
        Course.findById(params.course.id).addToObjectives(course)
        else if (params.assessment)
        Assessment.findById(params.assessment.id).addToObjectives(assessment)
   
        log.trace "getNew: new objective $objective instance created for $params.course or $params.assessment"
        objective
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Objective create(Map params) {
        Objective objective = new Objective(params)
        if (!objective.save(flush:true)) {
            objective.errors.allErrors.each {
                log.warning ("create: error while saving objective ${objective}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission objective, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission objective, 'admin',
        BasePermission.READ
        
        objective
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Objective', read) or hasPermission(#id, 'com.oumuo.central.Objective', admin) or hasRole('ROLE_USER')")
    Objective get(long id) {
        Objective.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Objective> list(Map params) {
        Objective.list()
    }

    int count() {
        Objective.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#objective, write) or hasPermission(#objective, admin)")
    void update(objective, Map params) {
        
        log.trace "udpate: before binding ${objective}"    
        objective.properties = params
        if (!objective.save(flush:true)) {         
            objective.errors.allErrors.each {
                log.warning ("create: error while saving objective ${objective}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#objective, delete) or hasPermission(#objective, admin)")
    void delete(Objective objective) {
        objective.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl objective
    }

    @Transactional
    @PreAuthorize("hasPermission(#objective, admin)")
    void deletePermission(Objective objective, String username, Permission permission) {
        def acl = aclUtilService.readAcl(objective)

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