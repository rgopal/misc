package central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class OrganizationController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def organizationService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [organizationInstanceList: organizationService.list(params),
            organizationInstanceTotal: organizationService.count()]
    }

    def create() {
          // need to handle associations (editable:false on many-to-one can
        // still get a list from base Organization.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here organization)
        
        [organizationInstance: organizationService.getNew(params)]
    }

    def save() {
        def organization = organizationService.create(params)
        if (!renderWithErrors('create', organization)) {
            redirectShow "Organization $organization.id created", organization.id
        }
    }

    def show() {
        def organization = findInstance()
        if (!organization)  return

        [organizationInstance: organization]
    }

    def edit() {
        def organization = findInstance()
        if (!organization) return

        [organizationInstance: organization]
    }

    def update() {
        def organizationInstance = findInstance()
        if (!organizationInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(organizationInstance.version > version) {
                organizationInstance.errors.rejectValue("version" ,
                    "organization.optimistic.locking.failure" ,
                    "Another user has updated this organization " +
                    "while you were editing." )
                render(view:'edit' ,model:[organizationInstance:organizationInstance])
                return
            }
        }
        organizationInstance.properties = params
        organizationService.update organizationInstance, params
        
        if (!renderWithErrors('edit', organizationInstance)) {
            redirectShow "Organization $organizationInstance.id updated", organizationInstance.id
        }
    }

    def delete() {
        def organization = findInstance()
        if (!organization) return

        try {
            organizationService.delete organization
            flash.message = "Organization $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Organization $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def organization = findInstance()
        if (!organization) return

        if (!request.post) {
            return [organizationInstance: organization]
        }

        organizationService.addPermission(organization, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Organization $organization.id " +
                   "to $params.recipient", organization.id
    }

    private Organization findInstance() {
        def organization = organizationService.get(params.long('id'))
        if (!organization) {
            flash.message = "Organization not found with id $params.id"
            redirect action: index
        }
        organization
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Organization organization) {
        if (organization.hasErrors()) {
            render view: view, model: [organizationInstance: organization]
            return true
        }
        false
    }
    def scaffold = Organization
} 
