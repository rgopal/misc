package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ContentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Content content, String username, int permission) {
        addPermission content, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#content, admin)")
    @Transactional
    void addPermission(Content content, String username,
        Permission permission) {
        aclUtilService.addPermission content, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Content getNew(Map params) {
        def content = new Content()
        
      /*  // first find the person who is authoring the comment
        content.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!content.person) {
            content.errors.allErrors.each {
                log.warning ("create: error while getting new content ${content}: ${it}")
            }
        } else
        log.trace "getNew: creating new content for $content.person"
        
        */
       if (params.organization)
        Organization.findById(params.organization?.id).addToContents(content)
        else if (params.learning)
        Learning.findById(params.learning?.id).addToContents(content)
       
        
        log.trace "getNew: new content $content instance created"
        content
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Content create(Map params) {
        Content content = new Content(params)
        if (!content.save(flush:true)) {
            content.errors.allErrors.each {
                log.warning ("create: error while saving content ${content}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission content, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission content, 'admin',
        BasePermission.READ
        
        content
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Content', read) or hasPermission(#id, 'com.oumuo.central.Content', admin) or hasRole('ROLE_USER')")
    Content get(long id) {
        Content.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Content> list(Map params) {
        Content.list()
    }

    int count() {
        Content.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#content, write) or hasPermission(#content, admin)")
    void update(content, Map params) {
        
        log.trace "udpate: before binding ${content}"    
        content.properties = params
        if (!content.save(flush:true)) {         
            content.errors.allErrors.each {
                log.warning ("create: error while saving content ${content}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#content, delete) or hasPermission(#content, admin)")
    void delete(Content content) {
        content.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl content
    }

    @Transactional
    @PreAuthorize("hasPermission(#content, admin)")
    void deletePermission(Content content, String username, Permission permission) {
        def acl = aclUtilService.readAcl(content)

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