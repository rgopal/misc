package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class CourseController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def courseService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [courseInstanceList: courseService.list(params),
            courseInstanceTotal: courseService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [courseInstance: courseService.getNew(params)]
    }

    def save() {
        def course = courseService.create(params)
        if (!renderWithErrors('create', course)) {
            redirectShow "Course $course.id created", course.id
        }
    }

    def show() {
        def course = findInstance()
        if (!course)  return

        [courseInstance: course]
    }

    def edit() {
        def course = findInstance()
        if (!course) return

        [courseInstance: course]
    }

    def update() {
        def courseInstance = findInstance()
        if (!courseInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(courseInstance.version > version) {
                courseInstance.errors.rejectValue("version" ,
                    "course.optimistic.locking.failure" ,
                    "Another user has updated this course " +
                    "while you were editing." )
                render(view:'edit' ,model:[courseInstance:courseInstance])
                return
            }
        }
        courseInstance.properties = params
        courseService.update courseInstance, params
        
        if (!renderWithErrors('edit', courseInstance)) {
            redirectShow "Course $courseInstance.id updated", courseInstance.id
        }
    }

    def delete() {
        def course = findInstance()
        if (!course) return

        try {
            courseService.delete course
            flash.message = "Course $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Course $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def course = findInstance()
        if (!course) return

        if (!request.post) {
            return [courseInstance: course]
        }

        courseService.addPermission(course, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Course $course.id " +
                   "to $params.recipient", course.id
    }

    private Course findInstance() {
        def course = courseService.get(params.long('id'))
        if (!course) {
            flash.message = "Course not found with id $params.id"
            redirect action: index
        }
        course
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Course course) {
        if (course.hasErrors()) {
            render view: view, model: [courseInstance: course]
            return true
        }
        false
    }
    def scaffold = Course
} 
