package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// this associated with either or both of Person and Organization, but a Person
// does this on their own so it is not null 

class Course {
    
    String name
    //  keep all associations first
    // course can be in multiple programs so no program association
    // Better to use course Authorship for multiple persons
    // Course is not referenced (only Many works that way) here
    // only in Clazs
  
    Organization organization
    SortedSet syllabuss
    
    static hasMany = [syllabuss: Syllabus, comments: Comment, 
        objectives: Objective,
         teachingRequirements: Requirement,
         requirements: Requirement,   
        rankings:Ranking,
        authorships:Authorship] 
    
    // no reference to CourseRelationship
    static mappedBy = [
            teachingRequirements:'teaching',
            requirements: 'learning' ]
    
    // then proper fileds
    Integer ranking = 500           // automatically derived from multiple items
    Credential credential = Credential.NONE  
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicMajor academicMajor = AcademicMajor.GENERAL
     
    Float passingPercentage
    Grade passingGrade = Grade.C
    
    
    AcademicSession academicSession = AcademicSession.FREE_FORM
    Float fee = 0.0f
    Integer duration = 1  
    AcademicSession durationUnit = AcademicSession.SEMESTER
    
    Integer effortRequired = 40 
    DurationUnit effortUnit = DurationUnit.HOURS
    GradeType gradeType = GradeType.LETTER 
    TeachingType learningType = TeachingType.ONLINE 
  
    String synopsis
    String objective
    String content 
    String background
    String testing
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
     
        organization (nullable:true, editable:true)
        syllabuss()
        comments()
        objectives()
        requirements()
        teachingRequirements()
        rankings()
        authorships()
        
        credential(nullable:true)
        academicStratum(nullable:true)
        academicMajor(nullable:true)
        passingGrade(nullable:true)
        passingPercentage(min:0.0f, max:100.0f, nullable:true)
        gradeType(nullable:true)
        academicSession(nullable:true)
        duration(nullable:true)
        durationUnit(nullable:true)
        effortRequired(nullable:true)
        effortUnit(nullable:true)
        fee(nullable:true)
        ranking(range:0..1000, editable:false, nullable:true)
 
        synopsis(nullable:true)
        objective(nullable:true) 
        content(nullable:true)
        background(nullable:true)
        testing(nullable:true) 
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
