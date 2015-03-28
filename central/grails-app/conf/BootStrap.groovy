import central.LoadAccount

class BootStrap {
    def init = { servletContext ->

        // keep all code in src/groovy
        LoadAccount.load()  
    }
   
    def destory = {
        
    }
    
    
}