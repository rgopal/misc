import com.oumuo.lookup.InitWebSite

import com.oumuo.central.InitPerson
import com.oumuo.lookup.InitCountryStateCity
import com.oumuo.lookup.InitCountryLookup
import com.oumuo.central.InitSpringSecurity
import com.oumuo.central.InitOrganization

class BootStrap {
    def aclService
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
    def init = { servletContext ->
println "aclService - ${aclService}"
        // keep all database table load code in src/groovy
        InitCountryLookup.load()
       
        
        // person associated as part of Userlogin
        // InitPerson.load()
        InitCountryStateCity.load()
        InitWebSite.load()
        def iss = new InitSpringSecurity()
        
        // need to pass these to plain groovy classes which don't get auto service inject
        iss.load(aclUtilService, aclService, objectIdentityRetrievalStrategy)
        
        def iorg = new InitOrganization()
        // Acl services are reused from InitSpringSecurity (static fields)
        iorg.load()
    }
   
    def destory = {
        
    }
    
    
}