package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
// performed by system programs and not users
@Secured(['ROLE_READ_ALL'])
class RankingController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def rankingService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rankingInstanceList: rankingService.list(params),
            rankingInstanceTotal: rankingService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [rankingInstance: rankingService.getNew(params)]
    }

    def save() {
        def ranking = rankingService.create(params)
        if (!renderWithErrors('create', ranking)) {
            redirectShow "Ranking $ranking.id created", ranking.id
        }
    }

    def show() {
        def ranking = findInstance()
        if (!ranking)  return

        [rankingInstance: ranking]
    }

    def edit() {
        def ranking = findInstance()
        if (!ranking) return

        [rankingInstance: ranking]
    }

    def update() {
        def rankingInstance = findInstance()
        if (!rankingInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(rankingInstance.version > version) {
                rankingInstance.errors.rejectValue("version" ,
                    "ranking.optimistic.locking.failure" ,
                    "Another user has updated this ranking " +
                    "while you were editing." )
                render(view:'edit' ,model:[rankingInstance:rankingInstance])
                return
            }
        }
        rankingInstance.properties = params
        rankingService.update rankingInstance, params
        
        if (!renderWithErrors('edit', rankingInstance)) {
            redirectShow "Ranking $rankingInstance.id updated", rankingInstance.id
        }
    }

    def delete() {
        def ranking = findInstance()
        if (!ranking) return

        try {
            rankingService.delete ranking
            flash.message = "Ranking $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Ranking $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def ranking = findInstance()
        if (!ranking) return

        if (!request.post) {
            return [rankingInstance: ranking]
        }

        rankingService.addPermission(ranking, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Ranking $ranking.id " +
                   "to $params.recipient", ranking.id
    }

    private Ranking findInstance() {
        def ranking = rankingService.get(params.long('id'))
        if (!ranking) {
            flash.message = "Ranking not found with id $params.id"
            redirect action: index
        }
        ranking
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Ranking ranking) {
        if (ranking.hasErrors()) {
            render view: view, model: [rankingInstance: ranking]
            return true
        }
        false
    }
    def scaffold = Ranking
} 
