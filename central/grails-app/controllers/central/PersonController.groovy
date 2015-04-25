package central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class PersonController {

    def personService

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [personInstanceList: personService.list(params),
            personInstanceTotal: personService.count()]
    }

    def create() {
        [personInstance: new Person()]
    }

    def save() {
        def person = personService.create(params)
        if (!renderWithErrors('create', person)) {
            redirectShow "Person $person.id created", person.id
        }
    }

    def show() {
        def person = findInstance()
        if (!person) return

        [personInstance: person]
    }

    def edit() {
        def person = findInstance()
        if (!person) return

        [personInstance: person]
    }

    def update() {
        def person = findInstance()
        if (!person) return

        personService.update params
        if (!renderWithErrors('edit', person)) {
            redirectShow "Person $person.id updated", person.id
        }
    }

    def delete() {
        def person = findInstance()
        if (!person) return

        try {
            personService.delete person
            flash.message = "Person $params.id deleted"
            redirect action: list
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Person $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def person = findInstance()
        if (!person) return

        if (!request.post) {
            return [personInstance: person]
        }

        personService.addPermission(person, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Person $person.id " +
                   "to $params.recipient", person.id
    }

    private Person findInstance() {
        def person = personService.get(params.long('id'))
        if (!person) {
            flash.message = "Person not found with id $params.id"
            redirect action: list
        }
        person
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Person person) {
        if (person.hasErrors()) {
            render view: view, model: [personInstance: person]
            return true
        }
        false
    }

   
    def scaffold = Person
} 
