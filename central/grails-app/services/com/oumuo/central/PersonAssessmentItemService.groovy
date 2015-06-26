package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonAssessmentItemService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonAssessmentItem personAssessmentItem, String username, int permission) {
        addPermission personAssessmentItem, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personAssessmentItem, admin)")
    @Transactional
    void addPermission(PersonAssessmentItem personAssessmentItem, String username,
        Permission permission) {
        aclUtilService.addPermission personAssessmentItem, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonAssessmentItem getNew(Map params) {
        def personAssessmentItem = new PersonAssessmentItem()
        
     
        // use addTo for person so that hibernate works fine.  Find  the logged
        // user
        
    
        
        log.trace "getNew: creating new personAssessmentItem"
      
        
        if (params.personAssessment)
        PersonInstruction.findById(params.personAssessment?.id).addToPersonAssessmentItems(personAssessmentItem)
        else if (params.assessmentItem)
        AssessmentItem.findById(params.assessmentItem?.id).addToPersonAssessmentItems(personAssessmentItem)
        else
        log.warn "getNew: personAssessment or assessmentItem should be non null"
    
        log.trace "getNew: new personAssessmentItem $personAssessmentItem instance created"
        personAssessmentItem
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonAssessmentItem create(Map params) {
        PersonAssessmentItem personAssessmentItem = new PersonAssessmentItem(params)
        if (!personAssessmentItem.save(flush:true)) {
            personAssessmentItem.errors.allErrors.each {
                log.warning ("create: error while saving personAssessmentItem ${personAssessmentItem}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personAssessmentItem, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission personAssessmentItem, 'admin',
        BasePermission.READ
        
        personAssessmentItem
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonAssessmentItem', read) or hasPermission(#id, 'com.oumuo.central.PersonAssessmentItem', admin) or hasRole('ROLE_USER')")
    PersonAssessmentItem get(long id) {
        PersonAssessmentItem.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<PersonAssessmentItem> list(Map params) {
        PersonAssessmentItem.list()
    }

    int count() {
        PersonAssessmentItem.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessmentItem, write) or hasPermission(#personAssessmentItem, admin)")
    void update(personAssessmentItem, Map params) {
        
        log.trace "udpate: before binding ${personAssessmentItem}"    
        personAssessmentItem.properties = params
        if (!personAssessmentItem.save(flush:true)) {         
            personAssessmentItem.errors.allErrors.each {
                log.warning ("create: error while saving personAssessmentItem ${personAssessmentItem}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessmentItem, delete) or hasPermission(#personAssessmentItem, admin)")
    void delete(PersonAssessmentItem personAssessmentItem) {
        personAssessmentItem.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personAssessmentItem
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessmentItem, admin)")
    void deletePermission(PersonAssessmentItem personAssessmentItem, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personAssessmentItem)

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