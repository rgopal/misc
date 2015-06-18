package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class AuthorshipService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Authorship authorship, String username, int permission) {
        addPermission authorship, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#authorship, admin)")
    @Transactional
    void addPermission(Authorship authorship, String username,
        Permission permission) {
        aclUtilService.addPermission authorship, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Authorship getNew(Map params) {
        def authorship = new Authorship()
        
     
        // first find the person who is authoring the comment. Eventually this
        // will be done when some one creates a course (then related Authorship
        // also gets created.
        authorship.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!authorship.person) {
            authorship.errors.allErrors.each {
                log.warning ("create: error while getting new authorship ${authorship}: ${error}")
            }
        } else
        log.trace "getNew: creating new authorship for $authorship.person"
        // could reach it by any one of the following
        
        if (params.course)
        Course.findById(params.course?.id).addToAuthorships(authorship)
        else if (params.learning)
        Learning.findById(params.learning?.id).addToAuthorships(authorship)
        else if (params.assessment)
        Assessment.findById(params.assessment?.id).addToAuthorships(authorship)
          else if (params.program)
        Program.findById(params.program?.id).addToAuthorships(authorship)
        
        log.trace "getNew: new authorship $authorship instance created"
        authorship
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Authorship create(Map params) {
        Authorship authorship = new Authorship(params)
        if (!authorship.save(flush:true)) {
            authorship.errors.allErrors.each {
                log.warning ("create: error while saving authorship ${authorship}: ${it}")
            }
        }
      
        // give permissions 

        // Grant the current principal administrative permission
        addPermission authorship, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission authorship, 'admin',
        BasePermission.READ
        
        authorship
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Authorship', read) or hasPermission(#id, 'com.oumuo.central.Authorship', admin) or hasRole('ROLE_USER')")
    Authorship get(long id) {
        Authorship.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Authorship> list(Map params) {
        Authorship.list()
    }

    int count() {
        Authorship.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#authorship, write) or hasPermission(#authorship, admin)")
    void update(authorship, Map params) {
        
        log.trace "udpate: before binding ${authorship}"    
        authorship.properties = params
        if (!authorship.save(flush:true)) {         
            authorship.errors.allErrors.each {
                log.warning ("create: error while saving authorship ${authorship}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#authorship, delete) or hasPermission(#authorship, admin)")
    void delete(Authorship authorship) {
        authorship.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl authorship
    }

    @Transactional
    @PreAuthorize("hasPermission(#authorship, admin)")
    void deletePermission(Authorship authorship, String username, Permission permission) {
        def acl = aclUtilService.readAcl(authorship)

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