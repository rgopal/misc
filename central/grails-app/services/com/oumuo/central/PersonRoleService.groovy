package com.oumuo.central

import com.oumuo.central.PersonRole

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonRoleService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonRole personRole, String username, int permission) {
        addPermission personRole, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personRole, admin)")
    @Transactional
    void addPermission(PersonRole personRole, String username,
        Permission permission) {
        aclUtilService.addPermission personRole, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // person is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonRole getNew(Map params) {
        def personRole = new PersonRole()
        personRole.person = Person.findById(params.person.id)
        if (!personRole.person) {
            personRole.errors.allErrors.each {
                log.warning ("create: error while getting new personRole ${personRole}: ${error}")
            }
        }
        personRole
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonRole create(Map params) {
        PersonRole personRole = new PersonRole(params)
        if (!personRole.save(flush:true)) {
            personRole.errors.allErrors.each {
                log.warning ("create: error while saving personRole ${personRole}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personRole, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
          // also give permission to ADMIN

        addPermission personRole, 'admin',
        BasePermission.READ

        personRole
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonRole', read) or hasPermission(#id, 'com.oumuo.central.PersonRole', admin)")
    PersonRole get(long id) {
        PersonRole.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)")
    List<PersonRole> list(Map params) {
        PersonRole.list()
    }

    int count() {
        PersonRole.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personRole, write) or hasPermission(#personRole, admin)")
    void update(personRole, Map params) {
        
        log.trace "udpate: before binding ${personRole}"    
        personRole.properties = params
        if (!personRole.save(flush:true)) {         
            personRole.errors.allErrors.each {
                log.warning ("create: error while saving personRole ${personRole}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personRole, delete) or hasPermission(#personRole, admin)")
    void delete(PersonRole personRole) {
        personRole.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personRole
    }

    @Transactional
    @PreAuthorize("hasPermission(#personRole, admin)")
    void deletePermission(PersonRole personRole, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personRole)

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

