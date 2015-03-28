import central.InitAccount
import central.InitPerson
import central.InitCountryStateCity

class BootStrap {
    def init = { servletContext ->

        // keep all database table load code in src/groovy
        InitAccount.load()
        InitPerson.load()
        InitCountryStateCity.load()
    }
   
    def destory = {
        
    }
    
    
}