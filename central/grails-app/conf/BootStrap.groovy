import central.Account

class BootStrap {
    def init = { servletContext ->

        // create 100 records with random names and emails (all unique)
        for (int i=0; i<100; i++) {
            def a1 = new Account(email: "aim${i}@b.com", userName: "${i} Harry")

            if (!a1.save()){ a1.errors.allErrors.each {error ->
                    println "An error occured with a1: ${error}"

                }
            }
        } 
    }
   
    def destory = {
        
    }
    
    
}