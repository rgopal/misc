package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class LearningAssessment {
  
    Learning learning
    Assessment assessment
    Program program
    // Does not make sense String sequence
  
    // static belongsTo = [program: Program]
    
    TimeRelation timeRelation = TimeRelation.BEFORE

    Float weight
    GradeType passingGradeType = GradeType.LETTER
    Grade passingGrade = Grade.C
    Float passingPercentage = 60.0f
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$program $learning $assessment"
    }
  
    static constraints = {
    
        learning (editable:true)
        assessment (editable:true)
        timeRelation (nullable:false)
        program(editable:true, nullable:true)      // so that you can create from learning or assessment
        weight(nullable:true)
        
        passingGradeType()
        passingGrade(nullable:true)
        passingPercentage(nullable:true)

        // in future make all other editable false as well
     
      
   
        status()
        dateCreated()
        lastUpdated()
    }


    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def learningAssessmentService = ctx.learningAssessmentService
     
        return learningAssessmentService.list()
    }
}