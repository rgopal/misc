package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonContentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonContent personContent, String username, int permission) {
        addPermission personContent, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personContent, admin)")
    @Transactional
    void addPermission(PersonContent personContent, String username,
        Permission permission) {
        aclUtilService.addPermission personContent, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonContent getNew(Map params) {
        def personContent = new PersonContent()
        
     
        // use addTo for person so that hibernate works fine.  Find  the logged
        // user
        
     
        if (params.personInstruction)
        PersonInstruction.findById(params.personInstruction?.id).addToPersonContents(personContent)
        else if (params.personAssessment)
        PersonAssessment.findById(params.personAssessment?.id).addToPersonContents(personContent)
        else
        log.warn "getNew: personInstruction or personAssessment/Items should be non null"
    
        log.trace "getNew: new personContent $personContent instance created"
        personContent
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonContent create(Map params) {
        PersonContent personContent = new PersonContent(params)
        if (!personContent.save(flush:true)) {
            personContent.errors.allErrors.each {
                log.warning ("create: error while saving personContent ${personContent}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personContent, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission personContent, 'admin',
        BasePermission.READ
        
        personContent
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonContent', read) or hasPermission(#id, 'com.oumuo.central.PersonContent', admin) or hasRole('ROLE_USER')")
    PersonContent get(long id) {
        PersonContent.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<PersonContent> list(Map params) {
        PersonContent.list()
    }

    int count() {
        PersonContent.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personContent, write) or hasPermission(#personContent, admin)")
    void update(personContent, Map params) {
        
        log.trace "udpate: before binding ${personContent}"    
        personContent.properties = params
        if (!personContent.save(flush:true)) {         
            personContent.errors.allErrors.each {
                log.warning ("create: error while saving personContent ${personContent}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personContent, delete) or hasPermission(#personContent, admin)")
    void delete(PersonContent personContent) {
        personContent.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personContent
    }

    @Transactional
    @PreAuthorize("hasPermission(#personContent, admin)")
    void deletePermission(PersonContent personContent, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personContent)

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