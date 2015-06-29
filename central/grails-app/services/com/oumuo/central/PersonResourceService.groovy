package com.oumuo.central

import com.oumuo.central.PersonResource

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonResourceService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonResource personResource, String username, int permission) {
        addPermission personResource, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personResource, admin)")
    @Transactional
    void addPermission(PersonResource personResource, String username,
        Permission permission) {
        aclUtilService.addPermission personResource, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // person is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonResource getNew(Map params) {
        def personResource = new PersonResource()
        
        Person.findById(params.person.id).addToPersonResources(personResource)
        
        if (!personResource.person) {
            personResource.errors.allErrors.each {
                log.warning ("create: error while getting new personResource ${personResource}: ${it}")
            }
        }
        personResource
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonResource create(Map params) {
        PersonResource personResource = new PersonResource(params)
        if (!personResource.save(flush:true)) {
            personResource.errors.allErrors.each {
                log.warning ("create: error while saving personResource ${personResource}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personResource, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
          // also give permission to ADMIN

        addPermission personResource, 'admin',
        BasePermission.READ

        personResource
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonResource', read) or hasPermission(#id, 'com.oumuo.central.PersonResource', admin) or hasRole('ROLE_READ_ALL')")
    PersonResource get(long id) {
        PersonResource.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)  or hasRole('ROLE_READ_ALL')")
    List<PersonResource> list(Map params) {
        PersonResource.list()
    }

    int count() {
        PersonResource.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personResource, write) or hasPermission(#personResource, admin)")
    void update(personResource, Map params) {
        
        log.trace "udpate: before binding ${personResource}"    
        personResource.properties = params
        if (!personResource.save(flush:true)) {         
            personResource.errors.allErrors.each {
                log.warning ("create: error while saving personResource ${personResource}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personResource, delete) or hasPermission(#personResource, admin)")
    void delete(PersonResource personResource) {
        personResource.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personResource
    }

    @Transactional
    @PreAuthorize("hasPermission(#personResource, admin)")
    void deletePermission(PersonResource personResource, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personResource)

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

