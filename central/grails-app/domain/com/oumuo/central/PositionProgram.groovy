package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Within a program learning is associated with assessment

class PositionProgram {
  
    Position position
    Program program 
    // Does not make sense String sequence
  
    Role role 
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$program $role $position"
    }
  
    static constraints = {
    
       
        position (editable:true)
        role (nullable:false)
        program(editable:true, nullable:true)      // so that you can create from learning or assessment
  
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
        def positionProgramService = ctx.positionProgramService
     
        return positionProgramService.list()
    }
}