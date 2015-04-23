import central.InitAccount
import central.InitPerson
import central.InitCountryStateCity
import central.InitCountryLookup
import com.oumuo.InitSpringSecurity

class BootStrap {
    def aclService
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
    def init = { servletContext ->
println "aclService - ${aclService}"
        // keep all database table load code in src/groovy
        InitCountryLookup.load()
        InitAccount.load()
        
        // person associated as part of Userlogin
        // InitPerson.load()
        InitCountryStateCity.load()
        def iss = new InitSpringSecurity()
        iss.load(aclUtilService, aclService, objectIdentityRetrievalStrategy)
    }
   
    def destory = {
        
    }
    
    
}