package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// This could be truely standalone and not part of any program or course

class Learning {
    
    String name
    //  keep all associations first
  
    Organization organization
    ClassSession classSession
    
    static hasMany = [comments: Comment, 
        objectives: Objective,
         teachingRequirements: Requirement,
         requirements: Requirement,  
         learningAssessments: LearningAssessment,
         contents: Content,
        rankings:Ranking,
        authorships:Authorship] 
    static mappedBy = [
            teachingRequirements:'teachingSection',
            requirements: 'learningSection' ]
    

    Hierarchy hierarchy
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicLevel academicLevel
     
    LearningType learningType = LearningType.OTHER
    
    // Language is in content
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
        classSession(nullable:true, editable:true)
        comments()
        objectives()
        requirements()
        contents()
        learningAssessments()
        teachingRequirements()
        rankings()
        authorships()
     
        hierarchy()
        academicLevel(nullable:true)
        academicStratum(nullable:true)
        learningType(nullable:true)
        
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
