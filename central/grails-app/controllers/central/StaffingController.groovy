package central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class StaffingController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def staffingService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [staffingInstanceList: staffingService.list(params),
            staffingInstanceTotal: staffingService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [staffingInstance: staffingService.getNew(params)]
    }

    def save() {
        def staffing = staffingService.create(params)
        if (!renderWithErrors('create', staffing)) {
            redirectShow "Staffing $staffing.id created", staffing.id
        }
    }

    def show() {
        def staffing = findInstance()
        if (!staffing)  return

        [staffingInstance: staffing]
    }

    def edit() {
        def staffing = findInstance()
        if (!staffing) return

        [staffingInstance: staffing]
    }

    def update() {
        def staffingInstance = findInstance()
        if (!staffingInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(staffingInstance.version > version) {
                staffingInstance.errors.rejectValue("version" ,
                    "staffing.optimistic.locking.failure" ,
                    "Another user has updated this staffing " +
                    "while you were editing." )
                render(view:'edit' ,model:[staffingInstance:staffingInstance])
                return
            }
        }
        staffingInstance.properties = params
        staffingService.update staffingInstance, params
        
        if (!renderWithErrors('edit', staffingInstance)) {
            redirectShow "Staffing $staffingInstance.id updated", staffingInstance.id
        }
    }

    def delete() {
        def staffing = findInstance()
        if (!staffing) return

        try {
            staffingService.delete staffing
            flash.message = "Staffing $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Staffing $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def staffing = findInstance()
        if (!staffing) return

        if (!request.post) {
            return [staffingInstance: staffing]
        }

        staffingService.addPermission(staffing, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Staffing $staffing.id " +
                   "to $params.recipient", staffing.id
    }

    private Staffing findInstance() {
        def staffing = staffingService.get(params.long('id'))
        if (!staffing) {
            flash.message = "Staffing not found with id $params.id"
            redirect action: index
        }
        staffing
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Staffing staffing) {
        if (staffing.hasErrors()) {
            render view: view, model: [staffingInstance: staffing]
            return true
        }
        false
    }
    def scaffold = Staffing
} 
