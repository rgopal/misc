package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Requirement {
   
    static belongsTo = [course: Course]
    static hasMany = [standardizedTests: StandardizedTest]
  

    Boolean current = false
    Long sequence
        
    AcademicLevel academicLevel 
    AcademicStratum academicStratum 
    
    Integer age 
    Integer yearsOfEduction
    Float gpa 
    AcademicMajor gpaMajor
    
    // minimum value should be explicitly set (but could be null)
    
    // Personality 
    Integer openness = 500
    Integer conscientiousness = 500
    Integer extraversion  = 500
    Integer agreeableness = 500
    Integer neuroticism  = 500

    // multiple intelligence
    
    Integer verbal  = 500
    Integer logical = 500
    Integer spatial = 500
    Integer musical = 500
    Integer intraPersonal = 500
    Integer interPersonal = 500
    Integer bodilyKinesthetic  = 500
    Integer linguistic = 500
    Integer naturalist = 500
    Integer existential = 500
    
    Integer intelligenceQuotient = 500
    Integer emotionalQuotient = 500
    Integer cognitiveQuotient = 500
    String meyerBrigg = "ENTJ"
        
    
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){ 
       
         def tag = ""
        if (current == true) {
            tag = " *"
        }
        " $sequence  $tag" 
    }
    // only one Course can own an Requirement (owner) with cascaded deletes
    // without belongsTo, an requirementType can be associated with multiple courses
    // akin to a lookup field (instead of true master-detail
   
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
       
        course(editable:false)
    
        standardizedTests()
       
        academicLevel(nullable:true)
        academicStratum(nullable:true)
            
        age(nullable:true)
        yearsOfEduction(nullable:true)
        gpa(nullable:true)
        gpaMajor(nullable:true)
        
        openness(min:0, max:1000, nullable:true)
        conscientiousness(min:0, max:1000, nullable:true)
        extraversion (min:0, max:1000, nullable:true)
        agreeableness(min:0, max:1000, nullable:true)
        neuroticism(min:0, max:1000, nullable:true)

        // multiple intelligence
    
        verbal(min:0, max:1000, nullable:true)
        logical(min:0, max:1000, nullable:true)
        spatial(min:0, max:1000, nullable:true)
        musical(min:0, max:1000, nullable:true)
        intraPersonal(min:0, max:1000, nullable:true)
        interPersonal(min:0, max:1000, nullable:true)
        bodilyKinesthetic(min:0, max:1000, nullable:true)
        linguistic(min:0, max:1000, nullable:true)
        naturalist(min:0, max:1000, nullable:true)
        existential(min:0, max:1000, nullable:true)
    
        intelligenceQuotient(min:0, max:1000, nullable:true)
        emotionalQuotient(min:0, max:1000, nullable:true)
        cognitiveQuotient(min:0, max:1000, nullable:true)
        meyerBrigg (matches:/[EI\-\*][SN-\\*][TF\-\*][JP\-\*]/, nullable:true)
    
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def requirementService = ctx.requirementService
        return requirementService.list()
    }
    def beforeInsert() {
        if (!sequence) {

            // InitCourse could uses explict 1 for sequence
            sequence = Course.findById(course.id).requirements.size() + 1
            log.trace "beforeInsert: sequence updated to $sequence"
            
        }
        // if this has become current then other should becomem false
        if (this.current == true) {
            checkMain()
        }
        
    }
    def beforeUpdate () {
        if (this.current == true) {
            checkMain()
        }
       
    }
    def checkMain() {
        // does not work other = Requirement.findByCourseAndMain(this.course.id, current:true)
        
        // find all records with current to be true and not equal to current requirement record
        log.trace "checkMain: requirements ${Course.findById(course.id).requirements.findAll {it.current == true}}"
        def other = Course.findById(course.id).requirements.findAll {it.current == true}
  
        // beforeInsert will not select the current record, but beforeUpdate will
       
        other = other - this
      
        log.trace "checkMain: other after removing this - $other"
        if (other.size() > 1) {
            // should be 1 or zero
            log.warn "checkMain: ${other.size()} requirements found"
        } else if (other.size() == 1) {
      
            other[0].current = false;
            
            log.trace "checkMain: reseted other $other[0] to false"
        } else {
            log.trace "checkMain: no other Requirement with current = true"
        }
       
    }
    }