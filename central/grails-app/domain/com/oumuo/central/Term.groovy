package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

class Term {

    String name
  
    static hasMany = [clazss: Clazs,
    enrollments: Enrollment] 

    Program program
    
  
    Float fee = 0.0f

    State state = State.STARTED
    Integer duration
    DurationUnit durationUnit = DurationUnit.MONTHS
    AcademicSession academicSession = AcademicSession.SEMESTER
    
    Date startDate = new Date()
    Date endDate
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${name} "
    }
   
    static constraints = {
        // named association so not needed owner()
        
        name (nullable:false)
        program(nullable:false, editable:false)
        clazss()
        enrollments()
        
        state()
        duration(nullable:true)
        durationUnit(nullable:true)
        academicSession(nullable:true)
        fee(nullable:true)
      
        startDate(nullable:true)
        endDate(nullable:true)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Term().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def termService = ctx.termService
     
        return termService.list()
    }

}