package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonJobService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonJob personJob, String username, int permission) {
        addPermission personJob, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personJob, admin)")
    @Transactional
    void addPermission(PersonJob personJob, String username,
        Permission permission) {
        aclUtilService.addPermission personJob, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonJob getNew(Map params) {
        def personJob = new PersonJob()
        
  
        def person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (since comment belongsTo Person
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new personJob ${personJob}: ${error}")
            }
        } else
        log.trace "getNew: creating new personJob for $personJob.person"
       
        person.addToPersonJobs(personJob)
        
        // could reach it by any one of the following
        
        if (params.job)
        Job.findById(params.job?.id).addToPersonJobs(personJob)
       
        
        log.trace "getNew: new personJob $personJob instance created"
        personJob
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonJob create(Map params) {
        PersonJob personJob = new PersonJob(params)
       
        if (!personJob.save(flush:true)) {
            personJob.errors.allErrors.each {
                log.warning ("create: error while saving personJob ${personJob}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission personJob, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission personJob, 'admin',
        BasePermission.READ
        
        personJob
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonJob', read) or hasPermission(#id, 'com.oumuo.central.PersonJob', admin) or hasRole('ROLE_READ_ALL')")
    PersonJob get(long id) {
        PersonJob.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_READ_ALL')")
    List<PersonJob> list(Map params) {
        PersonJob.list()
    }

    int count() {
        PersonJob.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personJob, write) or hasPermission(#personJob, admin)")
    void update(personJob, Map params) {
        
        log.trace "udpate: before binding ${personJob}"    
        personJob.properties = params
        if (!personJob.save(flush:true)) {         
            personJob.errors.allErrors.each {
                log.warning ("create: error while saving personJob ${personJob}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personJob, delete) or hasPermission(#personJob, admin)")
    void delete(PersonJob personJob) {
        personJob.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personJob
    }

    @Transactional
    @PreAuthorize("hasPermission(#personJob, admin)")
    void deletePermission(PersonJob personJob, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personJob)

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