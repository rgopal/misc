package com.oumuo.central

import com.oumuo.central.Job

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class JobService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Job job, String username, int permission) {
        addPermission job, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#job, admin)")
    @Transactional
    void addPermission(Job job, String username,
        Permission permission) {
        aclUtilService.addPermission job, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // job is sort of hardcoded and needs to be handled for any such association
    // Not just ROLE_USER, need to be a manager to create Job
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Job getNew(Map params) {
        def job = new Job()
    
        log.trace "getNew: params are $params"
        job
    }
    
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Job create(Map params) {
        Job job = new Job(params)
        if (!job.save(flush:true)) {
            job.errors.allErrors.each {
                log.warning ("create: error while saving job ${job}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission job, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION

          // also give permission to ADMIN

        addPermission job, 'admin',
        BasePermission.READ
        
        job
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Job', read) or hasPermission(#id, 'com.oumuo.central.Job', admin) or hasRole('ROLE_USER')")
    Job get(long id) {
        Job.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Job> list(Map params) {
        Job.list()
    }

    int count() {
        Job.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#job, write) or hasPermission(#job, admin)")
    void update(job, Map params) {
        
        log.trace "udpate: before binding ${job}"    
        job.properties = params
        if (!job.save(flush:true)) {         
            job.errors.allErrors.each {
                log.warning ("create: error while saving job ${job}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#job, delete) or hasPermission(#job, admin)")
    void delete(Job job) {
        job.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl job
    }

    @Transactional
    @PreAuthorize("hasPermission(#job, admin)")
    void deletePermission(Job job, String username, Permission permission) {
        def acl = aclUtilService.readAcl(job)

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