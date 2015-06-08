package com.oumuo.lookup
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_USER'])
class CountryLookupController {
    
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CountryLookup.list(params), model:[countryLookupInstanceCount: CountryLookup.count()]
    }

    def show(CountryLookup countryLookupInstance) {
        respond countryLookupInstance
    }
    
  
    def scaffold = CountryLookup
}
