import central.Account

class BootStrap {
    def init = { servletContext ->

        def a1 = new Account(email:'a@b.com', userName: 'Harry')

        if (!a1.save()){ a1.errors.allErrors.each {error ->
                println "An error occured with a1: ${error}"

            }
        }
    }
   
    def destory = {
        
    }
    
    
}