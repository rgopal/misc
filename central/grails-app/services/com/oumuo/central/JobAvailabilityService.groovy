package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class JobAvailabilityService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(JobAvailability jobAvailability, String username, int permission) {
        addPermission jobAvailability, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#jobAvailability, admin)")
    @Transactional
    void addPermission(JobAvailability jobAvailability, String username,
        Permission permission) {
        aclUtilService.addPermission jobAvailability, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    JobAvailability getNew(Map params) {
        def jobAvailability = new JobAvailability()
        
 
        // could reach it by any one of the following
        
        if (params.job)
        Job.findById(params.job?.id).addToJobAvailabilitys(jobAvailability)
       
        
        log.trace "getNew: new jobAvailability $jobAvailability instance created"
        jobAvailability
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    JobAvailability create(Map params) {
        JobAvailability jobAvailability = new JobAvailability(params)
       
        if (!jobAvailability.save(flush:true)) {
            jobAvailability.errors.allErrors.each {
                log.warning ("create: error while saving jobAvailability ${jobAvailability}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission jobAvailability, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission jobAvailability, 'admin',
        BasePermission.READ
        
        jobAvailability
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.JobAvailability', read) or hasPermission(#id, 'com.oumuo.central.JobAvailability', admin) or hasRole('ROLE_USER')")
    JobAvailability get(long id) {
        JobAvailability.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<JobAvailability> list(Map params) {
        JobAvailability.list()
    }

    int count() {
        JobAvailability.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#jobAvailability, write) or hasPermission(#jobAvailability, admin)")
    void update(jobAvailability, Map params) {
        
        log.trace "udpate: before binding ${jobAvailability}"    
        jobAvailability.properties = params
        if (!jobAvailability.save(flush:true)) {         
            jobAvailability.errors.allErrors.each {
                log.warning ("create: error while saving jobAvailability ${jobAvailability}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#jobAvailability, delete) or hasPermission(#jobAvailability, admin)")
    void delete(JobAvailability jobAvailability) {
        jobAvailability.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl jobAvailability
    }

    @Transactional
    @PreAuthorize("hasPermission(#jobAvailability, admin)")
    void deletePermission(JobAvailability jobAvailability, String username, Permission permission) {
        def acl = aclUtilService.readAcl(jobAvailability)

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