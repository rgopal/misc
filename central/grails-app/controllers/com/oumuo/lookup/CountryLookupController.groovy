package com.oumuo.lookup
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class CountryLookupController {

    def scaffold = CountryLookup
}
