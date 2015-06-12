package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// This could be truely standalone and not part of any program or course

class Learning {
    
    String name
    //  keep all associations first
  
    Organization organization 
    
    static hasMany = [comments: Comment, 
        objectives: Objective,
         teachingRequirements: Requirement,
         requirements: Requirement,   
        rankings:Ranking] 
    static mappedBy = [
            teachingRequirements:'teachingSection',
            requirements: 'learningSection' ]
    

    Hierarchy hierarchy
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicLevel academicLevel
     
    
    Integer duration = 1  
    AcademicSession durationUnit = AcademicSession.SEMESTER
    

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
    
        organization (nullable:true, editable:true)
      
        comments()
        objectives()
        requirements()
        teachingRequirements()
     
        hierarchy()
        academicLevel(nullable:true)
        academicStratum(nullable:true)
        
        duration(nullable:true)
        durationUnit(nullable:true)
     
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Learning().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def learningService = ctx.learningService
     
        return learningService.list()
    }

}