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

    // A assessmentItem of courses will be created for a program so that will be non
    // null.   A assessmentItem is a hiearchical structure with self references.  When
    // a new instance is created then it gets its sequence based on parent and
    // older siblings and the owner is the person creating it.  Possible that
    // root created by one person and children by others.
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    AssessmentItem getNew(Map params) {
     
        def assessmentItem = new AssessmentItem()
        
 
        
        // check if it was created against an existin assessmentItem (becomes parent)  
        assessmentItem.parentAssessmentItem = AssessmentItem.findById(params.assessmentItem?.id)
        // question and answer contents are added manually through the GUI or program
        
        if (!assessmentItem.parentAssessmentItem) {
                  assessmentItem.assessment = Assessment.findById(params.assessment?.id)
            // start a new root TODO find the current number for each program       
         
            if (!assessmentItem.assessment) {
                assessmentItem.sequence = "1"
            } else
            {
                assessmentItem.sequence = assessmentItem.assessment.assessmentItems.size() + 1
            }
            log.trace "getNew: root assessmentItem $assessmentItem is created for a assessment $assessmentItem.assessment "
 
            
        }else {
          
            // not a root so get the sequence from other siblings and program
            def location = assessmentItem.parentAssessmentItem.subAssessmentItems ? assessmentItem.parentAssessmentItem.subAssessmentItems.size()+ 1 : 1
            assessmentItem.sequence = assessmentItem.parentAssessmentItem.sequence + "." + location.toString()
            
            log.trace "getNew: assessmentItem sequence $assessmentItem.sequence is subAssessmentItem of $assessmentItem.parentAssessmentItem"
        }
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