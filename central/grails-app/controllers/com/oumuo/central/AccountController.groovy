package com.oumuo.central

import groovy.util.logging.Log4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured

@Log4j
@Secured(['ROLE_USER'])
class AccountController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def accountService
    
    // list is not longer the default (index is automatically generated not list)
    def index () {
    
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [accountInstanceList: accountService.list(params),
            accountInstanceTotal: accountService.count()]
    }

    def create() {
        // need to handle associations (editable:false on many-to-one can
        // still get a list from base Person.   For editable:true logic in
        // renderTemplate, need to provide owner.id (here person)
        
        [accountInstance: accountService.getNew(params)]
    }

    def save() {
        def account = accountService.create(params)
        if (!renderWithErrors('create', account)) {
            redirectShow "Account $account.id created", account.id
        }
    }

    def show() {
        def account = findInstance()
        if (!account)  return

        [accountInstance: account]
    }

    def edit() {
        def account = findInstance()
        if (!account) return

        [accountInstance: account]
    }

    def update() {
        def accountInstance = findInstance()
        if (!accountInstance) return

        if(params.version) {
            def version = params.version.toLong()
            if(accountInstance.version > version) {
                accountInstance.errors.rejectValue("version" ,
                    "account.optimistic.locking.failure" ,
                    "Another user has updated this account " +
                    "while you were editing." )
                render(view:'edit' ,model:[accountInstance:accountInstance])
                return
            }
        }
        accountInstance.properties = params
        accountService.update accountInstance, params
        
        if (!renderWithErrors('edit', accountInstance)) {
            redirectShow "Account $accountInstance.id updated", accountInstance.id
        }
    }

    def delete() {
        def account = findInstance()
        if (!account) return

        try {
            accountService.delete account
            flash.message = "Account $params.id deleted"
            redirect action: index
        }
        catch (DataIntegrityViolationException e) {
            redirectShow "Account $params.id could not be deleted", params.id
        }
    }

    def grant() {

        def account = findInstance()
        if (!account) return

        if (!request.post) {
            return [accountInstance: account]
        }

        accountService.addPermission(account, params.recipient,
            params.int('permission'))

        redirectShow "Permission $params.permission granted on Account $account.id " +
                   "to $params.recipient", account.id
    }

    private Account findInstance() {
        def account = accountService.get(params.long('id'))
        if (!account) {
            flash.message = "Account not found with id $params.id"
            redirect action: index
        }
        account
    }

    private void redirectShow(message, id) {
        flash.message = message
        redirect action: show, id: id
    }

    private boolean renderWithErrors(String view, Account account) {
        if (account.hasErrors()) {
            render view: view, model: [accountInstance: account]
            return true
        }
        false
    }
    def scaffold = Account
} 
