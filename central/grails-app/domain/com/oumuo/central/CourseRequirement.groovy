package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class CourseRequirement {
   
    static belongsTo = [course: Course]
    static hasMany = [standardizedTests: StandardizedTest]
  

    Boolean current = false
    Long sequence
        
    AcademicLevel academicLevel = AcademicLevel.COL1
    AcademicStratum academicStratum = AcademicStratum.COLLEGE
    
    Integer age = 15
    Integer yearsOfEduction = 12
    Float gpa = 2.0f
    AcademicMajor gpaMajor = AcademicMajor.CS
    
    // Personality 
    Integer openness = 500
    Integer conscientiousness = 500
    Integer extraversion = 500
    Integer agreeableness = 500
    Integer neuroticism = 500

    // multiple intelligence
    
    Integer verbal = 500
    Integer logical = 500
    Integer spatial = 500
    Integer musical = 500
    Integer intraPersonal = 500
    Integer interPersonal = 500
    Integer bodilyKinesthetic = 500
    Integer linguistic = 500
    Integer naturalist = 500
    Integer existential = 500
    
    Integer intelligenceQuotient = 500
    Integer emotionalQuotient = 500
    Integer cognitiveQuotient = 500
    String meyerBrigg = 'ENTJ'
        
    
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){  
    "${sequence} "
    }
    // only one Course can own an CourseRequirement (owner) with cascaded deletes
    // without belongsTo, an requirementType can be associated with multiple courses
    // akin to a lookup field (instead of true master-detail
   
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
       
        course(editable:false)
    
        standardizedTests()
       
        academicLevel()
        academicStratum()
            
        age()
        yearsOfEduction()
        gpa()
        gpaMajor()
        
        openness(min:0, max:1000)
        conscientiousness(min:0, max:1000)
        extraversion (min:0, max:1000)
        agreeableness(min:0, max:1000)
        neuroticism(min:0, max:1000)

        // multiple intelligence
    
        verbal(min:0, max:1000)
        logical(min:0, max:1000)
        spatial(min:0, max:1000)
        musical(min:0, max:1000)
        intraPersonal(min:0, max:1000)
        interPersonal(min:0, max:1000)
        bodilyKinesthetic(min:0, max:1000)
        linguistic(min:0, max:1000)
        naturalist(min:0, max:1000)
        existential(min:0, max:1000)
    
        intelligenceQuotient(min:0, max:1000)
        emotionalQuotient(min:0, max:1000)
        cognitiveQuotient(min:0, max:1000)
        meyerBrigg (matches:/[EI\-\*][SN-\\*][TF\-\*][JP\-\*]/)
    
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def courseRequirementService = ctx.courseRequirementService
        return courseRequirementService.list()
    }
    def beforeInsert() {
        if (!sequence) {

            // InitCourse could uses explict 1 for sequence
            sequence = Course.findById(course.id).courseRequirements.size() + 1
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
        // does not work other = CourseRequirement.findByCourseAndMain(this.course.id, current:true)
        
        // find all records with current to be true and not equal to current courseRequirement record
        log.trace "checkMain: courseRequirements ${Course.findById(course.id).courseRequirements.findAll {it.current == true}}"
        def other = Course.findById(course.id).courseRequirements.findAll {it.current == true}
  
        // beforeInsert will not select the current record, but beforeUpdate will
       
        other = other - this
      
        log.trace "checkMain: other after removing this - $other"
        if (other.size() > 1) {
            // should be 1 or zero
            log.warn "checkMain: ${other.size()} courseRequirements found"
        } else if (other.size() == 1) {
      
            other[0].current = false;
            
            log.trace "checkMain: reseted other $other[0] to false"
        } else {
            log.trace "checkMain: no other CourseRequirement with current = true"
        }
       
    }
}