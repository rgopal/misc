package com.oumuo.lookup
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.acls.model.Permission
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Log4j

@Log4j
@Secured(['ROLE_USER'])
class WebSiteController {

    // default security
    def scaffold = WebSite
}
