package com.oumuo.central

import com.oumuo.central.AssessmentItem

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class AssessmentItemService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(AssessmentItem assessmentItem, String username, int permission) {
        addPermission assessmentItem, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#assessmentItem, admin)")
    @Transactional
    void addPermission(AssessmentItem assessmentItem, String username,
        Permission permission) {
        aclUtilService.addPermission assessmentItem, username, permission
    }

    // A assessmentItem of assessments will be created for a program so that will be non
    // null.   A assessmentItem is a hiearchical structure with self references.  When
    // a new instance is created then it gets its sequence based on parent and
    // older siblings and the owner is the person creating it.  Possible that
    // root created by one person and children by others.
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    AssessmentItem getNew(Map params) {
     
        def assessmentItem = new AssessmentItem()
        
 if (params.assessmentItem) {
             
           
            def parent = AssessmentItem.findById(params.assessmentItem.id)
           
            // this is still now saved to add 1
          def location = parent.subAssessmentItems ? parent.subAssessmentItems.size() +1 : 1
            assessmentItem.sequence = parent.sequence + "." + location.toString()
                
        // add the current as a child to the parent found from table
            parent.addToSubAssessmentItems(assessmentItem)
            
                       if (assessmentItem.parentAssessmentItem.assessment)
            Assessment.findById(assessmentItem.parentAssessmentItem.assessment.id).addToAssessmentItems(assessmentItem)
            else
            log.warn "getNew: $assessmentItem parent $assessmentItem.parentAssessmentItem has no assessment"
            
            log.trace "getNew: child assessmentItem $assessmentItem is created for a assessment $assessmentItem.parentAssessmentItem with $assessmentItem.sequence "
 
        } 
      
        else {
            
             if (params.assessment)
            
            // this is the root so add 1 to all other root items findAll{!it.parentAssessmentItem}
           assessmentItem.sequence = 
           Assessment.findById(params.assessment.id).assessmentItems.findAll{!it.parentAssessmentItem}.size() + 1
   
            Assessment.findById(params.assessment?.id).addToAssessmentItems(assessmentItem)
            log.trace "getNew: root assessmentItem $assessmentItem is created for $assessmentItem.assessment with $assessmentItem.sequence"
        }
        
         // now see if need to copy assessment from its parent
         
        assessmentItem
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    AssessmentItem create(Map params) {
        AssessmentItem assessmentItem = new AssessmentItem(params)
        if (!assessmentItem.save(flush:true)) {
            assessmentItem.errors.allErrors.each {
                log.warning ("create: error while saving assessmentItem ${assessmentItem}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission assessmentItem, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission assessmentItem, 'admin',
        BasePermission.READ
        
        assessmentItem
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.AssessmentItem', read) or hasPermission(#id, 'com.oumuo.central.AssessmentItem', admin) or hasRole('ROLE_USER')")
    AssessmentItem get(long id) {
        AssessmentItem.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<AssessmentItem> list(Map params) {
        AssessmentItem.list()
    }

    int count() {
        AssessmentItem.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessmentItem, write) or hasPermission(#assessmentItem, admin)")
    void update(assessmentItem, Map params) {
        
        log.trace "udpate: before binding ${assessmentItem}"    
        assessmentItem.properties = params
        if (!assessmentItem.save(flush:true)) {         
            assessmentItem.errors.allErrors.each {
                log.warning ("create: error while saving assessmentItem ${assessmentItem}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessmentItem, delete) or hasPermission(#assessmentItem, admin)")
    void delete(AssessmentItem assessmentItem) {
        assessmentItem.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl assessmentItem
    }

    @Transactional
    @PreAuthorize("hasPermission(#assessmentItem, admin)")
    void deletePermission(AssessmentItem assessmentItem, String username, Permission permission) {
        def acl = aclUtilService.readAcl(assessmentItem)

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