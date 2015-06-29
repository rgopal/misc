package com.oumuo.central

import com.oumuo.central.Preference

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PreferenceService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Preference preference, String username, int permission) {
        addPermission preference, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#preference, admin)")
    @Transactional
    void addPermission(Preference preference, String username,
        Permission permission) {
        aclUtilService.addPermission preference, username, permission
    }
    
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    Preference getNew(Map params) {
     
        // this serves the purpose to drive a form but save again gets a new
        // after the form returns
        def preference = new Preference()
        
        // first find the person who is authoring the preference (has to be admin)
        preference.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (not using belongsTo for flexibility)
        if (!preference.person) {
            preference.errors.allErrors.each {
                log.warning ("getNew: error while getting new preference ${preference}: ${it}")
            }
        } else
        log.trace "getNew: creating new preference for person $preference.person"
        
        // used by many domains (only one at a time from web)
        
        if (params.personResource)  {
             if (params.oneToManyPropertyName == "organizationPreferences") {
                
                PersonResource.findById(params.personResource.id).addToOrganizationPreferences(preference)
            } else if (params.oneToManyPropertyName == "programPreferences") {
                
                PersonResource.findById(params.personResource.id).addToProgramPreferences(preference)
            } else if (params.oneToManyPropertyName == "coursePreferences") {
                
                PersonResource.findById(params.personResource.id).addToCoursePreferences(preference)
            }
            else
            log.warn "getNew: $oneToManyPropertyName has to be either requirements or teachingRequirements"
            
        }
  
      
        // check for other domains which would have preference (only one would be
        // with non null value in params
        preference
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    Preference create(Map params) {
        Preference preference = new Preference(params)
        if (!preference.save(flush:true)) {
            preference.errors.allErrors.each {
                log.warning ("create: error while saving preference ${preference}: ${it}")
            }
        }
      
        // Grant the current principal administrative permission
        addPermission preference, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission preference, 'admin',
        BasePermission.READ
        
        preference
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Preference', read) or hasPermission(#id, 'com.oumuo.central.Preference', admin) or hasRole('ROLE_USER')")
    Preference get(long id) {
        Preference.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)  or hasRole('ROLE_READ_ALL')")
    List<Preference> list(Map params) {
        Preference.list()
    }

    int count() {
        Preference.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#preference, write) or hasPermission(#preference, admin)")
    void update(preference, Map params) {
        
        log.trace "udpate: before binding ${preference}"    
        preference.properties = params
        if (!preference.save(flush:true)) {         
            preference.errors.allErrors.each {
                log.warning ("create: error while saving preference ${preference}: ${it}")
            }
        }
            
    }

    @Transactional
    @PreAuthorize("hasPermission(#preference, delete) or hasPermission(#preference, admin)")
    void delete(Preference preference) {
        preference.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl preference
    }

    @Transactional
    @PreAuthorize("hasPermission(#preference, admin)")
    void deletePermission(Preference preference, String username, Permission permission) {
        def acl = aclUtilService.readAcl(preference)

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