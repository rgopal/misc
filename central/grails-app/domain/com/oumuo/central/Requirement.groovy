package com.oumuo.central

// used as both requirement (for course, program) and capability (student/teacher)
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Requirement {
   
    // does not appear in show person - static belongsTo = [course: Course]
    
    Course learning
    Course teaching
    // Section is called Learnin
    Learning learningSection
    Learning teachingSection
    Program program
    static hasMany = [standardizedTests: StandardizedTest]
  
    Person person

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
        sequence (nullable:true, editable:false, display:true)
       
        learning(editable:false, nullable:true)
        teaching(editable:false, nullable:true)
          learningSection(editable:false, nullable:true)
        teachingSection(editable:false, nullable:true)
        program(editable:false, nullable:true)
        person(editable:false, nullable:true)
    
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
            // Init* even when does not provide seqeuence, it gets initialzied
            // to 2, so better to check for all prepopuldated records.
         
            if (person) {
                // use sequence number for organization
                sequence = Person.findById(person.id).capabilitys?.size()  + 1
                log.trace "beforeInsert: sequence is $sequence with person "
            
            }
            if (program) {
                // use sequence number for organization
                sequence = Program.findById(program.id).requirements?.size()  + 1
                log.trace "beforeInsert: sequence is $sequence with program "
            
            }
            if (learning) {
                // use sequence number for organization
                sequence = Course.findById(learning.id).requirements?.size()  + 1
                log.trace "beforeInsert: sequence is $sequence with course for learning "
            
            }
            if (teaching) {
                // use sequence number for organization
                sequence = Course.findById(teaching.id).teachingRequirements?.size()  + 1
                log.trace "beforeInsert: sequence is $sequence with course for teaching "
            
            }
        }
        // don't forget to check for new records also
        checkMain()
        
    }
    def beforeUpdate () {
        if (this.current == true) {
            checkMain()
        }
       
    }
    def checkMain() {
        
        // Requirement is for Person, Course, etc. so find for each type
          
        log.trace "checkMain: $person $learning $teaching $program will  be checked"
        if (person)
        updateCurrent(person, 'com.oumuo.central.Person', "capabilitys")
        else if (program)
        updateCurrent(program, 'com.oumuo.central.Program', "requirements")
        else if (learning)
        updateCurrent(learning, 'com.oumuo.central.Course', "requirements")
        else if (teaching)
        updateCurrent(teaching, 'com.oumuo.central.Course', "teachingRequirements")
        else
        log.warn "checkMain: neither course nor person non null"
    }
    def updateCurrent(Object instance, String owner, String name)
    {
        def grailsApplication = new Account().domainClass.grailsApplication
        def claz = grailsApplication.getClassForName(owner)
        
      
        if (instance) {
            log.trace "updateCurrent: $name ${claz} ${instance} for $instance.id "
            def other = claz.findById(
                instance.id)."${name}".findAll {it.current == true}
  
            log.trace "updateCurrent: other before subtraction $other"
            // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
            log.trace "updateCurrent: other $claz after removing this - $other"
            if (other.size() > 1) {
                // should be 1 or zero
                log.warn "updateCurrent: ${other.size()} $name found"
            } else if (other.size() == 1) {
      
                other[0].current = false;
            
                log.trace "setCurrent: reseted other $claz $other[0] to false"
            } else {
                log.trace "setCurrent: no other $claz Ranking with current = true"
            }
        } 
    }
}