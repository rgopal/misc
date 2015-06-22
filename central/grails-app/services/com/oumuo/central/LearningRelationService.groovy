package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class LearningRelationService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(LearningRelation learningRelation, String username, int permission) {
        addPermission learningRelation, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#learningRelation, admin)")
    @Transactional
    void addPermission(LearningRelation learningRelation, String username,
        Permission permission) {
        aclUtilService.addPermission learningRelation, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    LearningRelation getNew(Map params) {
        def learningRelation = new LearningRelation()
        
     
        // note no reference to first or second Learnings
         
        Program.findById(params.program?.id).addToLearningRelations(learningRelation)
       
        
        log.trace "getNew: new learningRelation $learningRelation instance created for $learningRelation.program"
        learningRelation
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    LearningRelation create(Map params) {
        LearningRelation learningRelation = new LearningRelation(params)
        if (!learningRelation.save(flush:true)) {
            learningRelation.errors.allErrors.each {
                log.warning ("create: error while saving learningRelation ${learningRelation}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission learningRelation, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission learningRelation, 'admin',
        BasePermission.READ
        
        learningRelation
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.LearningRelation', read) or hasPermission(#id, 'com.oumuo.central.LearningRelation', admin) or hasRole('ROLE_USER')")
    LearningRelation get(long id) {
        LearningRelation.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<LearningRelation> list(Map params) {
        LearningRelation.list()
    }

    int count() {
        LearningRelation.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningRelation, write) or hasPermission(#learningRelation, admin)")
    void update(learningRelation, Map params) {
        
        log.trace "udpate: before binding ${learningRelation}"    
        learningRelation.properties = params
        if (!learningRelation.save(flush:true)) {         
            learningRelation.errors.allErrors.each {
                log.warning ("create: error while saving learningRelation ${learningRelation}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningRelation, delete) or hasPermission(#learningRelation, admin)")
    void delete(LearningRelation learningRelation) {
        learningRelation.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl learningRelation
    }

    @Transactional
    @PreAuthorize("hasPermission(#learningRelation, admin)")
    void deletePermission(LearningRelation learningRelation, String username, Permission permission) {
        def acl = aclUtilService.readAcl(learningRelation)

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