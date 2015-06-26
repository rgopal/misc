package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// this associated with either or both of Person and Organization, but a Person
// does this on their own so it is not null 

class Content {
    
    String name
    //  keep all associations first
    // content can be in multiple programs so no program association
    // Better to use content Authorship for multiple persons
  
    Organization organization
    Learning learning
    AssessmentItem assessmentItemQuestion
    AssessmentItem assessmentItemAnswer
    static hasMany = [
        personContents: PersonContent,
        personAssessments: PersonAssessment,
        personAssessmentItems: PersonAssessmentItem
    ]
    
    // through Learning/Assessment static hasMany = [comments: Comment]
    String content
    String contentUrl
    // no sequence
    ContentType contentType = ContentType.SLIDES
    ContentRole contentRole = ContentRole.PRIMARY

    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicLevel academicLevel = AcademicLevel.K12

    Integer duration = 60  
    DurationUnit durationUnit = DurationUnit.MINUTES
    
    Integer effortRequired = 30
    DurationUnit effortUnit = DurationUnit.MINUTES
    
    Date startDate = new Date()
    Date endDate
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${name} $learning "
    }
   
    static constraints = {
        // named association so not needed owner()
        
        name (nullable:false)
     
        organization (nullable:true, editable:true)
        learning (nullable:true, editable:true)
        assessmentItemQuestion (nullable:true, editable:true)
        assessmentItemAnswer (nullable:true, editable:true)
        personContents()
        personAssessments()
        personAssessmentItems()
       
        content(nullable:true)  // if embedded
        contentUrl (nullable:true, url:true)

        academicStratum(nullable:true)
        academicLevel(nullable:true)
      
        duration(nullable:true)
        durationUnit(nullable:true)
        effortRequired(nullable:true)
        effortUnit(nullable:true)
        
        startDate(nullable:true)
        endDate(nullable:true)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Content().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def contentService = ctx.contentService
     
        return contentService.list()
    }

}
