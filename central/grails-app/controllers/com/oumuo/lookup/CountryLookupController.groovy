package com.oumuo.lookup

@Secured(['ROLE_USER'])
class CountryLookupController {

    def scaffold = CountryLookup
}
