package com.oumuo.central




import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured


@Secured(['ROLE_USER'])
class PersonController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def personInstanceService

    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personInstanceList: personInstanceService.list(params),
            personInstanceTotal: personInstanceService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here personInstance)
        
        [personInstance: personInstanceService.getNew(params)]
    }

 

    def save() {
        def personInstance = personInstanceService.create(params)
        if (!renderWithErrors('create', personInstance)) {
            redirectShow "Person personInstance.id created", personInstance.id
        }
    }

    def show() {
        def personInstance = findInstance()
        if (!personInstance)  return

        [personInstance: personInstance]
    }

    def edit() {
        def personInstance = findInstance()
        if (!personInstance) return

        [personInstance: personInstance]
    }

    
    def update() {
        def personInstance = findInstance()
        if (!personInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(personInstance.version > version) {
                personInstance.errors.rejectValue("version" ,
                    "personInstance.optimistic.locking.failure" ,
                    "Another user has updated this personInstance " +
                    "while you were editing." )
                render(view:'edit' ,model:[personInstance:personInstance])
                return
            }
        }
        personInstance.properties = params
        personInstanceService.update personInstance, params
        
        if (!renderWithErrors('edit', personInstance)) {
            redirectShow "Person personInstance.id updated", personInstance.id
        }
    }
    def delete() {
        def personInstance = findInstance()
        if (!personInstance) return

        try {
            personInstanceService.delete personInstance
            flash.message = "Person " + params.id + " deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Person " + params.id + " could not be deleted", params.id
        }
    }
    def grant() {

        def personInstance = findInstance()
        if (!personInstance) return

        if (!request.post) {
            return [personInstance: personInstance]
        }

        personInstanceService.addPermission(personInstance, params.recipient,
            params.int('permission'))

        redirectShow "Permission " + params.permission + " granted on Person " +
                    personInstance.id  + " to " + params.recipient,  personInstance.id
        
    }

    private Person findInstance() {
        def personInstance = personInstanceService.get(params.long('id'))
        if (!personInstance) {
            flash.message = "Person not found with id " + params.id
            redirect action: index
        }
        personInstance
    }

    
    private boolean renderWithErrors(String view, Person personInstance) {
        if (personInstance.hasErrors()) {
            render view: view, model: [personInstance: personInstance]
            return true
        }
        false
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

 
}
