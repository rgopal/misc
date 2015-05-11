package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
// performed by system programs and not users
@Secured(['ROLE_READ_ALL'])
class RankingItemController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def rankingItemService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rankingItemInstanceList: rankingItemService.list(params),
            rankingItemInstanceTotal: rankingItemService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [rankingItemInstance: rankingItemService.getNew(params)]
    }

    def save() {
        def rankingItem = rankingItemService.create(params)
        if (!renderWithErrors('create', rankingItem)) {
            redirectShow "RankingItem $rankingItem.id created", rankingItem.id
        }
    }

    def show() {
        def rankingItem = findInstance()
        if (!rankingItem)  return

        [rankingItemInstance: rankingItem]
    }

    def edit() {
        def rankingItem = findInstance()
        if (!rankingItem) return

        [rankingItemInstance: rankingItem]
    }

    def update() {
        def rankingItemInstance = findInstance()
        if (!rankingItemInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(rankingItemInstance.version > version) {
                rankingItemInstance.errors.rejectValue("version" ,
                    "rankingItem.optimistic.locking.failure" ,
                    "Another user has updated this rankingItem " +
                    "while you were editing." )
                render(view:'edit' ,model:[rankingItemInstance:rankingItemInstance])
                return
            }
        }
        rankingItemInstance.properties = params
        rankingItemService.update rankingItemInstance, params
        
        if (!renderWithErrors('edit', rankingItemInstance)) {
            redirectShow "RankingItem $rankingItemInstance.id updated", rankingItemInstance.id
        }
    }

    def delete() {
        def rankingItem = findInstance()
        if (!rankingItem) return

        try {
            rankingItemService.delete rankingItem
            flash.message = "RankingItem $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "RankingItem $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def rankingItem = findInstance()
        if (!rankingItem) return

        if (!request.post) {
            return [rankingItemInstance: rankingItem]
        }

        rankingItemService.addPermission(rankingItem, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on RankingItem $rankingItem.id " +
                   "to $params.recipient", rankingItem.id
    }

    private RankingItem findInstance() {
        def rankingItem = rankingItemService.get(params.long('id'))
        if (!rankingItem) {
            flash.message = "RankingItem not found with id $params.id"
            redirect action: index
        }
        rankingItem
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, RankingItem rankingItem) {
        if (rankingItem.hasErrors()) {
            render view: view, model: [rankingItemInstance: rankingItem]
            return true
        }
        false
    }
    def scaffold = RankingItem
} 
