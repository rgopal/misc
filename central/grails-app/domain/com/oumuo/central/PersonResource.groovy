package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class PersonResource {
   
       // only one Person can own an PersonResource (owner) with cascaded deletes
    // without belongsTo, an personResource can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = [person: Person]
    
    static hasMany = [
        coursePreferences: Preference,      // overall will give preference
        programPreferences: Preference,
        organizationPreferences: Preference
        
    ]
    
    static mappedBy = [
        coursePreferences: 'coursePreference',
        programPreferences: 'programPreference',
        organizationPreferences : 'organizationPreference'
    ]
    Long sequence
    Boolean current = false
    // Person owner (does not work well with sequence updates)

    Integer minFee
    Integer shortestSchedule
    Float totalMoney
    Float perMonthMoney
    Integer minWeeklyHours
    Integer maxWeeklyHours
    Event availableTime
    Integer highSchoolLevelFriends
    Integer collegeLevelFriends
    Integer professionalFriends
    AcademicLevel motherAcademic
    AcademicLevel fatherAcademic
    
    Event assessedAvailableTime             // (social network crawling based), 


 // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

        def tag = ""
        if (current == true) {
            tag = " *"
        }
    "${sequence} ${tag}"
    }
 
    static constraints = {
        // named association so not needed owner()
        
        person(editable:false)
        
              coursePreferences()
        programPreferences()
        organizationPreferences()
       
          minFee (nullable:true, range:0..1000)
     shortestSchedule (nullable:true, range:0..1000)
     totalMoney(nullable:true, min:0.0f, max:300000.0f)
     perMonthMoney(nullable:true, min:0.0f, max:5000.0f)
     minWeeklyHours (nullable:true, min:1, max:40)
     maxWeeklyHours(nullable:true, min:1, max:80)
     availableTime (nullable:true)
     highSchoolLevelFriends (nullable:true, min:1)
     collegeLevelFriends (nullable:true, min:1)
     professionalFriends(nullable:true, min:1)
     motherAcademic(nullable:true)
     fatherAcademic(nullable:true)
     assessedAvailableTime(nullable:true)
        
  
        
        minFee(nullable:true,range:0..1000)
        shortestSchedule(nullable:true,range:0..1000)
        sequence (nullable:true, display:false)
        current()
  
        
        status()
        dateCreated()
        lastUpdated()
    }
 
     static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def personResourceService = ctx.personResourceService
        return personResourceService.list()
    }
    def beforeInsert() {
        if (!sequence) {

            // InitPerson uses explict 1 for sequence
            sequence = Person.findById(person.id).personResources.size() + 1
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
        // does not work other = PersonResource.findByPersonAndMain(this.person.id, current:true)
        
        // find all records with current to be true and not equal to current personResource record
        log.trace "checkMain: personResources ${Person.findById(person.id).personResources.findAll {it.current == true}}"
        def other = Person.findById(person.id).personResources.findAll {it.current == true}
  
        // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
        log.trace "checkMain: other after removing this - $other"
        if (other.size() > 1) {
            // should be 1 or zero
            log.warn "checkMain: ${other.size()} personResources found"
        } else if (other.size() == 1) {
      
            other[0].current = false;
            
            log.trace "checkMain: reseted other $other[0] to false"
        } else {
            log.trace "checkMain: no other PersonResource with current = true"
        }
       
    }
}