import central.InitWebSite

import central.InitPerson
import central.InitCountryStateCity
import central.InitCountryLookup
import com.oumuo.InitSpringSecurity
import com.oumuo.InitOrganization

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