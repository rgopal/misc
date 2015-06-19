package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

class Program {

    String name
    SortedSet catalogs
    static hasMany = [catalogs: Catalog, rankings: Ranking, requirements: Requirement,
        learningAssessments: LearningAssessment,
    courseRelations: CourseRelation,
    authorships:Authorship,
    terms: Term,
    studentPrograms: StudentProgram] 
    
    // REMOVED Person person
    Organization organization
    Integer ranking            // automatically derived from multiple items
    Credential credential = Credential.NONE  
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicMajor academicMajor = AcademicMajor.GENERAL 
    Grade minimumGrade = Grade.C
    Double minimumPercentage
    AcademicSession academicSession = AcademicSession.FREE_FORM
    Float termFee = 0.0f

      
    // spent a lot of time when it was pure Catalog catalog -- this transient problem Catalog 5/10

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
    
        // still not happy TODO this was supposed to be a singleton
        catalogs()
        rankings()
        requirements()
        learningAssessments()
        courseRelations()
        authorships()
        terms()
        studentPrograms()
        
        credential(nullable:true)
        academicStratum(nullable:true)
        academicMajor(nullable:true)
        minimumGrade(nullable:true)
        minimumPercentage(min:0.0d, max:100.0d, nullable:true)
        academicSession(nullable:true)
        termFee(nullable:true)
        ranking(range:0..1000, editable:false, nullable:true)
 
        startDate(nullable:true)
        endDate(nullable:true)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Program().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def programService = ctx.programService
     
        return programService.list()
    }

}