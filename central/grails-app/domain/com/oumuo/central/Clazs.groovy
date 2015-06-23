package com.oumuo.central
import com.oumuo.central.Person
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Clazs {

    String name
    
    Course course       // could be online or real
    Term term
    Event event
    
    // Location location
    static hasMany = [
        classSessions: ClassSession,
        enrollments: Enrollment,
        locationUses: LocationUse
    ]
    
    TeachingType teachingType = TeachingType.ONLINE       // but a class could be different
    
 
    
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
     
        course (editable:false, nullable:true)       // could be online or real
        term(editable:false, nullable:true) 
        classSessions()
        enrollments()
        locationUses()
        event(editable:false, nullable:true)
        // Location location
    
        teachingType (nullable:true)       // but a class could be different
    
   
    
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