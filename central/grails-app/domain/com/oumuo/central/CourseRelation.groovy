package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class CourseRelation {
  
    Course firstCourse
    Course secondCourse
  
    static belongsTo = [program: Program]
    
    Relation relation = Relation.PRE_REQ

    GradeType passingGradeType = GradeType.LETTER
    Grade passingGrade = Grade.C
    Float passingPercentage = 60.0f
    


    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$firstCourse $relation $secondCourse"
    }
  
    static constraints = {
     
        firstCourse (editable:true)
        secondCourse (editable:true)
        relation (nullable:false)
        program(editable:false)
        
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
        def courseRelationService = ctx.courseRelationService
     
        return courseRelationService.list()
    }
}