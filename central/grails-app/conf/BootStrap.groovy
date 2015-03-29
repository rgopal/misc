import central.InitAccount
import central.InitPerson
import central.InitCountryStateCity
import central.InitCountryLookup

class BootStrap {
    def init = { servletContext ->

        // keep all database table load code in src/groovy
        InitCountryLookup.load()
        InitAccount.load()
        InitPerson.load()
        InitCountryStateCity.load()
    }
   
    def destory = {
        
    }
    
    
}