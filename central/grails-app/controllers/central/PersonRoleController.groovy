package central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class PersonRoleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def personRoleService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personRoleInstanceList: personRoleService.list(params),
            personRoleInstanceTotal: personRoleService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [personRoleInstance: personRoleService.getNew(params)]
    }

    def save() {
        def personRole = personRoleService.create(params)
        if (!renderWithErrors('create', personRole)) {
            redirectShow "PersonRole $personRole.id created", personRole.id
        }
    }

    def show() {
        def personRole = findInstance()
        if (!personRole)  return

        [personRoleInstance: personRole]
    }

    def edit() {
        def personRole = findInstance()
        if (!personRole) return

        [personRoleInstance: personRole]
    }

    def update() {
        def personRoleInstance = findInstance()
        if (!personRoleInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(personRoleInstance.version > version) {
                personRoleInstance.errors.rejectValue("version" ,
                    "personRole.optimistic.locking.failure" ,
                    "Another user has updated this personRole " +
                    "while you were editing." )
                render(view:'edit' ,model:[personRoleInstance:personRoleInstance])
                return
            }
        }
        personRoleInstance.properties = params
        personRoleService.update personRoleInstance, params
        
        if (!renderWithErrors('edit', personRoleInstance)) {
            redirectShow "PersonRole $personRoleInstance.id updated", personRoleInstance.id
        }
    }

    def delete() {
        def personRole = findInstance()
        if (!personRole) return

        try {
            personRoleService.delete personRole
            flash.message = "PersonRole $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "PersonRole $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def personRole = findInstance()
        if (!personRole) return

        if (!request.post) {
            return [personRoleInstance: personRole]
        }

        personRoleService.addPermission(personRole, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on PersonRole $personRole.id " +
                   "to $params.recipient", personRole.id
    }

    private PersonRole findInstance() {
        def personRole = personRoleService.get(params.long('id'))
        if (!personRole) {
            flash.message = "PersonRole not found with id $params.id"
            redirect action: index
        }
        personRole
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, PersonRole personRole) {
        if (personRole.hasErrors()) {
            render view: view, model: [personRoleInstance: personRole]
            return true
        }
        false
    }
    def scaffold = PersonRole
} 
