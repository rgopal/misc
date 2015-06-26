package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Structure comes from LearningAssessment linked with Learning (Syllabus)

class Assessment {
    
    String name
    //  keep all associations first
    // assessment can be in multiple programs so no program association

    Organization organization
    
    static hasMany = [comments: Comment, 
        objectives: Objective,
        learningAssessments: LearningAssessment,
        assessmentItems: AssessmentItem,
        rankings:Ranking,
        authorships:Authorship,
        personAssessments: PersonAssessment] 

    
    // then proper fileds
    Integer ranking = 500           // automatically derived from multiple items
   
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicMajor academicMajor = AcademicMajor.GENERAL
    AssessmentType assessmentType
    AssessmentMode assessmentMode = AssessmentMode.ONLINE
     
    Float totalPoints
     GradeType gradeType = GradeType.LETTER 
     Grade passingGrade = Grade.C
    Float passingPercentage
    

    Float fee = 0.0f
    // duration is calendar time
    Integer duration = 3  
    DurationUnit durationUnit = DurationUnit.HOURS
    
    Integer effortRequired = 1 
    DurationUnit effortUnit = DurationUnit.HOURS
   

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
        learningAssessments()
  
        assessmentItems()
        comments()
        objectives()
     
        rankings()
        authorships()
        personAssessments()
        
              name (nullable:false)
        academicStratum(nullable:true)
        academicMajor(nullable:true)
        assessmentType(nullable:true)
        assessmentMode(nullable:true)
        passingGrade(nullable:true)
        passingPercentage(min:0.0f, max:100.0f, nullable:true)
        gradeType(nullable:true)
  
        duration(nullable:true)
        durationUnit(nullable:true)
        
        ranking(range:0..1000, editable:false, nullable:true)
 
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Assessment().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def assessmentService = ctx.assessmentService
     
        return assessmentService.list()
    }

}
