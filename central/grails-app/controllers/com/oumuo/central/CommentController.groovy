package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class CommentController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def commentService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [commentInstanceList: commentService.list(params),
            commentInstanceTotal: commentService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [commentInstance: commentService.getNew(params)]
    }

    def save() {
        def comment = commentService.create(params)
        if (!renderWithErrors('create', comment)) {
            redirectShow "Comment $comment.id created", comment.id
        }
    }

    def show() {
        def comment = findInstance()
        if (!comment)  return

        [commentInstance: comment]
    }

    def edit() {
        def comment = findInstance()
        if (!comment) return

        [commentInstance: comment]
    }

    def update() {
        def commentInstance = findInstance()
        if (!commentInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(commentInstance.version > version) {
                commentInstance.errors.rejectValue("version" ,
                    "comment.optimistic.locking.failure" ,
                    "Another user has updated this comment " +
                    "while you were editing." )
                render(view:'edit' ,model:[commentInstance:commentInstance])
                return
            }
        }
        commentInstance.properties = params
        commentService.update commentInstance, params
        
        if (!renderWithErrors('edit', commentInstance)) {
            redirectShow "Comment $commentInstance.id updated", commentInstance.id
        }
    }

    def delete() {
        def comment = findInstance()
        if (!comment) return

        try {
            commentService.delete comment
            flash.message = "Comment $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Comment $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def comment = findInstance()
        if (!comment) return

        if (!request.post) {
            return [commentInstance: comment]
        }

        commentService.addPermission(comment, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Comment $comment.id " +
                   "to $params.recipient", comment.id
    }

    private Comment findInstance() {
        def comment = commentService.get(params.long('id'))
        if (!comment) {
            flash.message = "Comment not found with id $params.id"
            redirect action: index
        }
        comment
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Comment comment) {
        if (comment.hasErrors()) {
            render view: view, model: [commentInstance: comment]
            return true
        }
        false
    }
    def scaffold = Comment
} 
