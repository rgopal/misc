package com.oumuo.lookup
\
import grails.plugin.springsecurity.annotation.Secured


@Secured(['ROLE_USER'])
class WebSiteController {
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond WebSite.list(params), model:[webSiteInstanceCount: WebSite.count()]
    }

    def show(WebSite webSiteInstance) {
        respond webSiteInstance
    }
    
    // default security
    def scaffold = WebSite
}
