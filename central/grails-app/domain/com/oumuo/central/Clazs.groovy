package com.oumuo.central
import com.oumuo.central.Person
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Clazs {

    String name
    
    Course course       // could be online or real
    Term term
    // Location location
    static hasMany = [
        classSessions: ClassSession
    ]
    
    TeachingType teachingType = TeachingType.ONLINE       // but a class could be different
    
    boolean recurring
    Recurring recurringDays
    Integer startHour
    Integer startMinute
    Integer duration
    DurationUnit durationUnit
    
    
    Date startDate
    Date endDate
    
    State state
    Integer size
    Language language = Language.ENGLISH
  
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated 
    Date lastUpdated

    String toString(){
    "${name}"
    }
  
    static constraints = {
        // named association so not needed owner()
      
        name (nullable:false)
        name
    
        course (editable:false, nullable:true)       // could be online or real
        term(editable:false, nullable:true) 
        classSessions()
        // Location location
    
        teachingType (nullable:true)       // but a class could be different
    
        recurring(nullable:true) 
        recurringDays(nullable:true) 
        startHour(nullable:true, range:0..24) 
        startMinute(nullable:true, range:0..60) 
        duration(nullable:true, min:0, max:3600)
        durationUnit (nullable:true)
    
        startDate(nullable:true) 
        endDate(nullable:true) 
    
        state(nullable:true) 
        size(nullable:true) 
        language(nullable:true) 
        
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Clazs().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def clazsService = ctx.clazsService
        return clazsService.list()
    }
    
}