package com.oumuo.central

import com.oumuo.central.Comment

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CommentService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Comment comment, String username, int permission) {
        addPermission comment, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#comment, admin)")
    @Transactional
    void addPermission(Comment comment, String username,
        Permission permission) {
        aclUtilService.addPermission comment, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    Comment getNew(Map params) {
     
        
        def comment = new Comment()
        
        // first find the person who is authoring the comment
        comment.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!comment.person) {
            comment.errors.allErrors.each {
                log.warning ("create: error while getting new comment ${comment}: ${it}")
            }
        } else
         log.trace "getNew: creating new comment for $comment.person"
        
        // check if it was created against an existin comment (becomes parent)  
        comment.parentComment = Comment.findById(params.comment?.id)
        
        if (!comment.parentComment) {
            // this comment is a new root, created for a specific item.  We
            // are not repeating the association with other items for subcomments
            
            comment.organization = Organization.findById(params.organization?.id)
          
            log.trace "getNew: root comment $comment is created for a org $comment.organization "
            
            // now keep adding all new "owners" such as Organization, Course, etc.
        } else
            log.trace "getNew: $comment is a subComment of $comment.parentComment"
        
        comment
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Comment create(Map params) {
        Comment comment = new Comment(params)
        if (!comment.save(flush:true)) {
            comment.errors.allErrors.each {
                log.warning ("create: error while saving comment ${comment}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission comment, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission comment, 'admin',
        BasePermission.READ
        
        comment
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Comment', read) or hasPermission(#id, 'com.oumuo.central.Comment', admin) or hasRole('ROLE_READ_ALL')")
    Comment get(long id) {
        Comment.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_READ_ALL')")
    List<Comment> list(Map params) {
        Comment.list()
    }

    int count() {
        Comment.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#comment, write) or hasPermission(#comment, admin)")
    void update(comment, Map params) {
        
        log.trace "udpate: before binding ${comment}"    
        comment.properties = params
        if (!comment.save(flush:true)) {         
            comment.errors.allErrors.each {
                log.warning ("create: error while saving comment ${comment}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#comment, delete) or hasPermission(#comment, admin)")
    void delete(Comment comment) {
        comment.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl comment
    }

    @Transactional
    @PreAuthorize("hasPermission(#comment, admin)")
    void deletePermission(Comment comment, String username, Permission permission) {
        def acl = aclUtilService.readAcl(comment)

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