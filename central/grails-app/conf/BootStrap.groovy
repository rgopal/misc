import central.InitAccount
import central.InitPerson

class BootStrap {
    def init = { servletContext ->

        // keep all code in src/groovy
        InitAccount.load()
        InitPerson.load()
    }
   
    def destory = {
        
    }
    
    
}