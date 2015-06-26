package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonAssessmentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonAssessment personAssessment, String username, int permission) {
        addPermission personAssessment, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personAssessment, admin)")
    @Transactional
    void addPermission(PersonAssessment personAssessment, String username,
        Permission permission) {
        aclUtilService.addPermission personAssessment, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonAssessment getNew(Map params) {
        def personAssessment = new PersonAssessment()
        
     
        // use addTo for person so that hibernate works fine.  Find  the logged
        // user
        
    
        
        log.trace "getNew: creating new personAssessment"
      
        
        if (params.personInstruction)
        PersonInstruction.findById(params.personInstruction?.id).addToPersonAssessments(personAssessment)
        else if (params.assessment)
        Assessment.findById(params.assessment?.id).addToPersonAssessments(personAssessment)
        else
        log.warn "getNew: personInstruction or assessment should be non null"
    
        log.trace "getNew: new personAssessment $personAssessment instance created"
        personAssessment
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonAssessment create(Map params) {
        PersonAssessment personAssessment = new PersonAssessment(params)
        if (!personAssessment.save(flush:true)) {
            personAssessment.errors.allErrors.each {
                log.warning ("create: error while saving personAssessment ${personAssessment}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personAssessment, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission personAssessment, 'admin',
        BasePermission.READ
        
        personAssessment
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonAssessment', read) or hasPermission(#id, 'com.oumuo.central.PersonAssessment', admin) or hasRole('ROLE_USER')")
    PersonAssessment get(long id) {
        PersonAssessment.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<PersonAssessment> list(Map params) {
        PersonAssessment.list()
    }

    int count() {
        PersonAssessment.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessment, write) or hasPermission(#personAssessment, admin)")
    void update(personAssessment, Map params) {
        
        log.trace "udpate: before binding ${personAssessment}"    
        personAssessment.properties = params
        if (!personAssessment.save(flush:true)) {         
            personAssessment.errors.allErrors.each {
                log.warning ("create: error while saving personAssessment ${personAssessment}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessment, delete) or hasPermission(#personAssessment, admin)")
    void delete(PersonAssessment personAssessment) {
        personAssessment.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personAssessment
    }

    @Transactional
    @PreAuthorize("hasPermission(#personAssessment, admin)")
    void deletePermission(PersonAssessment personAssessment, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personAssessment)

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