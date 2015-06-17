package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// This could be truely standalone and not part of any program or course

class Learning extends Material{
    
    String name
    //  keep all associations first
  
    Organization organization 
    
    static hasMany = [comments: Comment, 
        objectives: Objective,
         teachingRequirements: Requirement,
         requirements: Requirement,  
         learningAssessments: LearningAssessment,
         contents: Content,
        rankings:Ranking] 
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
        contents()
        learningAssessments()
        teachingRequirements()
     
        hierarchy()
        academicLevel(nullable:true)
        academicStratum(nullable:true)
        learningType(nullable:true)
        
        duration(nullable:true)
        durationUnit(nullable:true)
    
    }


    static secureList() {
        def grailsApplication = new Learning().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def learningService = ctx.learningService
     
        return learningService.list()
    }

}
