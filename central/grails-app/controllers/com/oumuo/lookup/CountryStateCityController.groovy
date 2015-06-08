package com.oumuo.lookup
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class CountryStateCityController {

        def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CountryStateCity.list(params), model:[countryStateCityInstanceCount: CountryStateCity.count()]
    }

    def show(CountryStateCity countryStateCityInstance) {
        respond countryStateCityInstance
    }
    
      def scaffold = CountryStateCity
    
}
