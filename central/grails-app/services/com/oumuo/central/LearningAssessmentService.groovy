package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class LearningAssessmentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(LearningAssessment learningAssessment, String username, int permission) {
        addPermission learningAssessment, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#learningAssessment, admin)")
    @Transactional
    void addPermission(LearningAssessment learningAssessment, String username,
        Permission permission) {
        aclUtilService.addPermission learningAssessment, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    LearningAssessment getNew(Map params) {
        def learningAssessment = new LearningAssessment()
        
     
        // could reach it by any one of the following
        
        if (params.program)
        Program.findById(params.program?.id).addToLearningAssessments(learningAssessment)
        else if (params.learning)
        Learning.findById(params.learning?.id).addToLearningAssessments(learningAssessment)
        else if (params.assessment)
        Assessment.findById(params.assessment?.id).addToLearningAssessments(learningAssessment)
        
        log.trace "getNew: new learningAssessment $learningAssessment instance created for $learningAssessment.program"
        learningAssessment
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    LearningAssessment create(Map params) {
        LearningAssessment learningAssessment = new LearningAssessment(params)
        if (!learningAssessment.save(flush:true)) {
            learningAssessment.errors.allErrors.each {
                log.warning ("create: error while saving learningAssessment ${learningAssessment}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission learningAssessment, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission learningAssessment, 'admin',
        BasePermission.READ
        
        learningAssessment
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.LearningAssessment', read) or hasPermission(#id, 'com.oumuo.central.LearningAssessment', admin) or hasRole('ROLE_USER')")
    LearningAssessment get(long id) {
        LearningAssessment.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<LearningAssessment> list(Map params) {
        LearningAssessment.list()
    }

    int count() {
        LearningAssessment.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningAssessment, write) or hasPermission(#learningAssessment, admin)")
    void update(learningAssessment, Map params) {
        
        log.trace "udpate: before binding ${learningAssessment}"    
        learningAssessment.properties = params
        if (!learningAssessment.save(flush:true)) {         
            learningAssessment.errors.allErrors.each {
                log.warning ("create: error while saving learningAssessment ${learningAssessment}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningAssessment, delete) or hasPermission(#learningAssessment, admin)")
    void delete(LearningAssessment learningAssessment) {
        learningAssessment.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl learningAssessment
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningAssessment, admin)")
    void deletePermission(LearningAssessment learningAssessment, String username, Permission permission) {
        def acl = aclUtilService.readAcl(learningAssessment)

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