package com.oumuo.central

import com.oumuo.central.Organization

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class OrganizationService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Organization organization, String username, int permission) {
        addPermission organization, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#organization, admin)")
    @Transactional
    void addPermission(Organization organization, String username,
        Permission permission) {
        aclUtilService.addPermission organization, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    // Not just ROLE_USER, need to be a manager to create Organization
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Organization getNew(Map params) {
        def organization = new Organization()
    
        log.trace "getNew: params are $params"
        organization
    }
    
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Organization create(Map params) {
        Organization organization = new Organization(params)
        if (!organization.save(flush:true)) {
            organization.errors.allErrors.each {
                log.warning ("create: error while saving organization ${organization}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission organization, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION

          // also give permission to ADMIN

        addPermission organization, 'admin',
        BasePermission.READ
        
        organization
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Organization', read) or hasPermission(#id, 'com.oumuo.central.Organization', admin) or hasRole('ROLE_USER')")
    Organization get(long id) {
        Organization.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Organization> list(Map params) {
        Organization.list()
    }

    int count() {
        Organization.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#organization, write) or hasPermission(#organization, admin)")
    void update(organization, Map params) {
        
        log.trace "udpate: before binding ${organization}"    
        organization.properties = params
        if (!organization.save(flush:true)) {         
            organization.errors.allErrors.each {
                log.warning ("create: error while saving organization ${organization}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#organization, delete) or hasPermission(#organization, admin)")
    void delete(Organization organization) {
        organization.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl organization
    }

    @Transactional
    @PreAuthorize("hasPermission(#organization, admin)")
    void deletePermission(Organization organization, String username, Permission permission) {
        def acl = aclUtilService.readAcl(organization)

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