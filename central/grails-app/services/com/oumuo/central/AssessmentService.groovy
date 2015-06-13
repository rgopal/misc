package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class AssessmentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Assessment assessment, String username, int permission) {
        addPermission assessment, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#assessment, admin)")
    @Transactional
    void addPermission(Assessment assessment, String username,
        Permission permission) {
        aclUtilService.addPermission assessment, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Assessment getNew(Map params) {
        def assessment = new Assessment()
        
    
        
        // this has to be non null (since comment belongsTo Person
        if (!params.organization) {
            assessment.errors.allErrors.each {
                log.warning ("create: error while getting new assessment ${assessment}: ${it}")
            }
        } else {
            log.trace "getNew: creating new assessment for $params.organization"
         
            Organization.findById(params.organization?.id).addToAssessments(assessment)
        }
        
        log.trace "getNew: new assessment $assessment instance created"
        assessment
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Assessment create(Map params) {
        Assessment assessment = new Assessment(params)
        if (!assessment.save(flush:true)) {
            assessment.errors.allErrors.each {
                log.warning ("create: error while saving assessment ${assessment}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission assessment, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission assessment, 'admin',
        BasePermission.READ
        
        assessment
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Assessment', read) or hasPermission(#id, 'com.oumuo.central.Assessment', admin) or hasRole('ROLE_USER')")
    Assessment get(long id) {
        Assessment.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Assessment> list(Map params) {
        Assessment.list()
    }

    int count() {
        Assessment.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessment, write) or hasPermission(#assessment, admin)")
    void update(assessment, Map params) {
        
        log.trace "udpate: before binding ${assessment}"    
        assessment.properties = params
        if (!assessment.save(flush:true)) {         
            assessment.errors.allErrors.each {
                log.warning ("create: error while saving assessment ${assessment}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessment, delete) or hasPermission(#assessment, admin)")
    void delete(Assessment assessment) {
        assessment.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl assessment
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessment, admin)")
    void deletePermission(Assessment assessment, String username, Permission permission) {
        def acl = aclUtilService.readAcl(assessment)

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