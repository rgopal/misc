package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class ProgramController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def programService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [programInstanceList: programService.list(params),
            programInstanceTotal: programService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [programInstance: programService.getNew(params)]
    }

    def save() {
        def program = programService.create(params)
        if (!renderWithErrors('create', program)) {
            redirectShow "Program $program.id created", program.id
        }
    }

    def show() {
        def program = findInstance()
        if (!program)  return

        [programInstance: program]
    }

    def edit() {
        def program = findInstance()
        if (!program) return

        [programInstance: program]
    }

    def update() {
        def programInstance = findInstance()
        if (!programInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(programInstance.version > version) {
                programInstance.errors.rejectValue("version" ,
                    "program.optimistic.locking.failure" ,
                    "Another user has updated this program " +
                    "while you were editing." )
                render(view:'edit' ,model:[programInstance:programInstance])
                return
            }
        }
        programInstance.properties = params
        programService.update programInstance, params
        
        if (!renderWithErrors('edit', programInstance)) {
            redirectShow "Program $programInstance.id updated", programInstance.id
        }
    }

    def delete() {
        def program = findInstance()
        if (!program) return

        try {
            programService.delete program
            flash.message = "Program $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Program $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def program = findInstance()
        if (!program) return

        if (!request.post) {
            return [programInstance: program]
        }

        programService.addPermission(program, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Program $program.id " +
                   "to $params.recipient", program.id
    }

    private Program findInstance() {
        def program = programService.get(params.long('id'))
        if (!program) {
            flash.message = "Program not found with id $params.id"
            redirect action: index
        }
        program
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Program program) {
        if (program.hasErrors()) {
            render view: view, model: [programInstance: program]
            return true
        }
        false
    }
    def scaffold = Program
} 
