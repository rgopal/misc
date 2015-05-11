import com.oumuo.lookup.InitWebSite

import com.oumuo.central.InitPerson
import com.oumuo.lookup.InitCountryStateCity
import com.oumuo.lookup.InitCountryLookup
import com.oumuo.central.InitSpringSecurity
import com.oumuo.central.InitOrganization
import com.oumuo.central.InitProgram
import com.oumuo.central.InitCourse
import com.oumuo.central.InitCatalog
import com.oumuo.central.InitRankingItem
class BootStrap {
    def aclService
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
    def init = { servletContext ->
        println "aclService - ${aclService}"
        // keep all database table load code in src/groovy
           
        // need to pass these to plain groovy classes which don't get auto service inject
        // they get stored as static so that other Inits can use them
       
        new InitSpringSecurity()
        .load(aclUtilService, aclService, objectIdentityRetrievalStrategy)
        new InitPerson().load()
        InitCountryLookup.load()

        InitCountryStateCity.load()
       
        InitWebSite.load()

        new InitOrganization().load()
        // Acl services are reused from InitSpringSecurity (static fields)
          
        new InitProgram().load()
        new InitCourse().load()
        new InitCatalog().load()
        new InitRankingItem().load()
    }
   
    def destory = {
        
    }
    
    
}