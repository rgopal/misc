package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class CatalogController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def catalogService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [catalogInstanceList: catalogService.list(params),
            catalogInstanceTotal: catalogService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [catalogInstance: catalogService.getNew(params)]
    }

    def save() {
        def catalog = catalogService.create(params)
        if (!renderWithErrors('create', catalog)) {
            redirectShow "Catalog $catalog.id created", catalog.id
        }
    }

    def show() {
        def catalog = findInstance()
        if (!catalog)  return

        [catalogInstance: catalog]
    }

    def edit() {
        def catalog = findInstance()
        if (!catalog) return

        [catalogInstance: catalog]
    }

    def update() {
        def catalogInstance = findInstance()
        if (!catalogInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(catalogInstance.version > version) {
                catalogInstance.errors.rejectValue("version" ,
                    "catalog.optimistic.locking.failure" ,
                    "Another user has updated this catalog " +
                    "while you were editing." )
                render(view:'edit' ,model:[catalogInstance:catalogInstance])
                return
            }
        }
        catalogInstance.properties = params
        catalogService.update catalogInstance, params
        
        if (!renderWithErrors('edit', catalogInstance)) {
            redirectShow "Catalog $catalogInstance.id updated", catalogInstance.id
        }
    }

    def delete() {
        def catalog = findInstance()
        if (!catalog) return

        try {
            catalogService.delete catalog
            flash.message = "Catalog $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Catalog $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def catalog = findInstance()
        if (!catalog) return

        if (!request.post) {
            return [catalogInstance: catalog]
        }

        catalogService.addPermission(catalog, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Catalog $catalog.id " +
                   "to $params.recipient", catalog.id
    }

    private Catalog findInstance() {
        def catalog = catalogService.get(params.long('id'))
        if (!catalog) {
            flash.message = "Catalog not found with id $params.id"
            redirect action: index
        }
        catalog
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Catalog catalog) {
        if (catalog.hasErrors()) {
            render view: view, model: [catalogInstance: catalog]
            return true
        }
        false
    }
    def scaffold = Catalog
} 
