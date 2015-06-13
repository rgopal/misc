package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Objective {
   
    Course course
    Assessment assessment
    Long sequence
 
    LearningType objectiveType = LearningType.OTHER
    String objectiveText
    AcademicLevel academicLevel = AcademicLevel.COL1
    AcademicStratum academicStratum = AcademicStratum.COLLEGE
    String reference

    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){  
    "${sequence} ${objectiveType} "
    }
    // only one Course can own an Objective (owner) with cascaded deletes
    // without belongsTo, an objectiveType can be associated with multiple courses
    // akin to a lookup field (instead of true master-detail
   
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        
        course(editable:false, nullable:true)
        assessment(editable:false, nullable:true)
      
        objectiveType()
        objectiveText(nullable:true)
        academicLevel(nullable:true)
        academicStratum(nullable:true)
      
        reference(nullable:true, url: true) 
       
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def objectiveService = ctx.objectiveService
        return objectiveService.list()
    }
    def beforeInsert() {
        if (!sequence) {

            // InitCourse could uses explict 1 for sequence
            if (course)
            sequence = Course.findById(course.id).objectives.size() + 1
            else if (assessment)
            sequence = Assessment.findById(assessment.id).objectives.size() + 1
            log.trace "beforeInsert: sequence updated to $sequence"
            
        }
        // if this has become current then other should becomem false
    }   
   
}
