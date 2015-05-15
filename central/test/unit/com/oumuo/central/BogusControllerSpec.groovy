package com.oumuo.central



import grails.test.mixin.*
import spock.lang.*

@TestFor(BogusController)
@Mock(Bogus)
class BogusControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.bogusInstanceList
            model.bogusInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.bogusInstance!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def bogus = new Bogus()
            bogus.validate()
            controller.save(bogus)

        then:"The create view is rendered again with the correct model"
            model.bogusInstance!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            bogus = new Bogus(params)

            controller.save(bogus)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/bogus/show/1'
            controller.flash.message != null
            Bogus.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def bogus = new Bogus(params)
            controller.show(bogus)

        then:"A model is populated containing the domain instance"
            model.bogusInstance == bogus
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def bogus = new Bogus(params)
            controller.edit(bogus)

        then:"A model is populated containing the domain instance"
            model.bogusInstance == bogus
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/bogus/index'
            flash.message != null


        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def bogus = new Bogus()
            bogus.validate()
            controller.update(bogus)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.bogusInstance == bogus

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            bogus = new Bogus(params).save(flush: true)
            controller.update(bogus)

        then:"A redirect is issues to the show action"
            response.redirectedUrl == "/bogus/show/$bogus.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/bogus/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def bogus = new Bogus(params).save(flush: true)

        then:"It exists"
            Bogus.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(bogus)

        then:"The instance is deleted"
            Bogus.count() == 0
            response.redirectedUrl == '/bogus/index'
            flash.message != null
    }
}
