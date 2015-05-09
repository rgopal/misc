package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// this associated with either or both of Person and Organization, but a Person
// does this on their own so it is not null 

class Course {
    String name
    Person person
    Organization organization
    Integer ranking = 500           // automatically derived from multiple items
    Credential credential = Credential.NONE  
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicMajor academicMajor = AcademicMajor.GENERAL 
    Grade passingGrade = Grade.C
    Double passingPercentage
    AcademicSession academicSession = AcademicSession.FREE_FORM
    Float fee = 0.0f
    Integer duration = 1  
    AcademicSession durationUnit = AcademicSession.SEMESTER
    
    Integer effortRequired = 40 
    DurationUnit effortUnit = DurationUnit.HOURS
    GradeType gradeType = GradeType.LETTER 
    LearningType learningType = LearningType.ONLINE 
  
    String synopsis
    String objective
    String content 
    String background
    String assessment
    String certification
    
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
        person (nullable:false, editable:false)
        organization (nullable:true, editable:true)
        
        credential()
        academicStratum()
        academicMajor()
        passingGrade()
        passingPercentage(min:0.0d, max:100.0d, nullable:true)
        gradeType()
        academicSession()
        duration()
        durationUnit()
        effortRequired()
        effortUnit()
        fee(nullable:true)
        ranking(range:0..1000, editable:false)
 
        synopsis(nullable:true)
        objective(nullable:true) 
        content(nullable:true)
        background(nullable:true)
        assessment(nullable:true) 
        certification (nullable:true)
        
        startDate(nullable:true)
        endDate(nullable:true)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Course().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def courseService = ctx.courseService
     
        return courseService.list()
    }

}