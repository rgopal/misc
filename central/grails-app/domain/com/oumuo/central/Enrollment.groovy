package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Enrollment {
  
    // a person can enroll in multiple items (avoiding multiple classes)
    // and only one item is used at any given time
   
    
    Person person

    Program program
    Term term
    Clazs clazs
    ClassSession classSession
    Instruction instruction             // catch participation in a discussion
    
     
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
        program (editable:true, nullable:true)
        term (editable:true, nullable:true)
        clazs (editable:true, nullable:true)
        classSession (editable:true, nullable:true)
        instruction (editable:true,nullable:true)
    
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
        def authorshipService = ctx.authorshipService
     
        return authorshipService.list()
    }
}