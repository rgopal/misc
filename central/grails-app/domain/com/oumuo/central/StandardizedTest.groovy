package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class StandardizedTest {
   
    static belongsTo = [courseRequirement: CourseRequirement]
    Long sequence
    Float score = 600.0f
    Float minScore = 200.0f
    Float maxScore = 800.0f
  
    String reference

    StandardizedTestEnum test = StandardizedTestEnum.SAT
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){  
    "${sequence} ${test} "
    }
    // only one Course can own an StandardizedTest (owner) with cascaded deletes
    // without belongsTo, an objectiveType can be associated with multiple courses
    // akin to a lookup field (instead of true master-detail
   
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, editable:false, display:true)        
        courseRequirement(editable:false)
      
        score(min:0.0f, max:800.0f)
        minScore()
        maxScore()
        reference(nullable:true, url:true)
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def standardizedTestService = ctx.standardizedTestService
        return standardizedTestService.list()
    }
    def beforeInsert() {
        if (!sequence) {

            // InitCourse could uses explict 1 for sequence
            sequence = CourseRequirement.findById(courseRequirement.id).standardizedTests.size() + 1
            log.trace "beforeInsert: sequence updated to $sequence"
            
        }
        // if this has become current then other should becomem false
    }   
   
}
