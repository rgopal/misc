package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Within a program learning is associated with assessment

class Instruction {
  
    ClassSession classSession
    Learning learning
    static hasMany = [
        enrollments: Enrollment
    ]
  
    // Does not make sense String sequence
  
 
    State state = State.STARTED
    Date startTime
    Date endTime
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    
    String toString(){

        "$classSession $learning"
    }
  
    static constraints = {
    
        classSession (editable:true)
        learning (editable:true)
        enrollments()
        
        state (nullable:false)
        startTime(nullable:true)
        endTime(nullable:true)
     
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
        def instructionService = ctx.instructionService
     
        return instructionService.list()
    }
}