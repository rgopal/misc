package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class PersonJob {
  
 
    // M2M between persons (student) and jobs.  One could be primary 
    
    Person person
    Job job
    Integer matchLevel
    Integer choiceLevel
     
    Role role = Role.PRIMARY
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$person $role "
    }
  
    static constraints = {
    
        person (editable:true, nullable:false)
        job (editable:true, nullable:true)
        matchLevel (editable:true, nullable:true, range:0..1000)
        choiceLevel (editable:true, nullable:true, range:0..1000)
  
        role (nullable:false)
    
        // in future make all other editable false as well
     
      
   
        status() 
        dateCreated()
        lastUpdated()
    }


    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def personJobService = ctx.personJobService
     
        return personJobService.list()
    }
}