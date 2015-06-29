package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Preference {
    
   
    Long sequence
    
    String name             // updated by cron job, e.g May 2015 Preference
    Boolean current = false
    PersonResource organizationPreference
    PersonResource programPreference
    PersonResource coursePreference
  
    // copied from Ranking but has no preference item counterpart
    
    static belongsTo = [person:Person]
    
    // Person owner (does not work well with sequence updates)

    Integer overall = 500
    Integer utility = 500
    Integer complexity = 500
    Integer prestige = 500
    Integer education = 500
    Integer affordability = 500
    Integer recommendation = 500
    Integer improvement = 500
    Integer jobPlacement = 500
    Integer presentation = 500
    Integer content = 500
    Integer assistance = 500
    Integer convenience = 500
    Integer collaboration = 500
    Integer testing = 500       // collides with collaboration so
    Integer certification = 500
    Integer effort = 500
    Integer difficulty = 500
    Integer popularity = 500

    Date expiryDate
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

        def tag = ""
        if (current == true) {
            tag = " *"
        }
    "${sequence} $lastUpdated ${tag}"
    }

    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        name()
        current()
        person(editable:false)
        organizationPreference(editable:false, nullable:true)
        programPreference(editable:false, nullable:true)
        coursePreference(editable:false, nullable:true)
      
        
        overall(range:0..1000, editable:false, nullable:true)
        utility (range:0..1000, editable:false, nullable:true)
        complexity (range:0..1000, editable:false, nullable:true)
        prestige(range:0..1000, editable:false, nullable:true) 
        education(range:0..1000, editable:false, nullable:true) 
        affordability(range:0..1000, editable:false, nullable:true) 
        recommendation(range:0..1000, editable:false, nullable:true) 
        improvement(range:0..1000, editable:false, nullable:true) 
        jobPlacement(range:0..1000, editable:false, nullable:true)
        presentation (range:0..1000, editable:false, nullable:true)
        content(range:0..1000, editable:false, nullable:true)
        assistance (range:0..1000, editable:false, nullable:true)
        convenience (range:0..1000, editable:false, nullable:true)
        collaboration (range:0..1000, editable:false, nullable:true)
        testing (range:0..1000, editable:false, nullable:true)
        certification (range:0..1000, editable:false, nullable:true)
        effort(range:0..1000, editable:false, nullable:true)
        difficulty (range:0..1000, editable:false, nullable:true)
        popularity (range:0..1000, editable:false, nullable:true)


        expiryDate(nullable:true)
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def preferenceService = ctx.preferenceService
        return preferenceService.list()
    }

    // should be here and not in Service
    
    def beforeInsert() {
        if (!sequence) {
            // Init* even when does not provide seqeuence, it gets initialzied
            // to 2, so better to check for all prepopuldated records.
            if (organizationPreference) {
                // go the persistence to get existing preferences
   
                sequence = PersonResource.findById(organizationPreference.id).organizationPreferences.size()  + 1
                log.trace "beforeInsert:  seqeuence is $sequence with organization "
            
            }
            if (programPreference) {
                // use sequence number for organization
                sequence = PersonResource.findById(programPreference.id).programPreferences.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with program "
            
            }
            if (coursePreference) {
                // use sequence number for organization
                sequence = PersonResource.findById(coursePreference.id).coursePreferences.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with course "
            
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
        
        // Preference is for Organization, Course, etc. so find for each type
  
        if (organizationPreference)
        updateCurrent(organizationPreference, 'com.oumuo.central.PersonResource', "organization")
        else if (programPreference)
        updateCurrent(programPreference, 'com.oumuo.central.PersonResource', "program")
        else if (coursePreference)
        updateCurrent(coursePreference, 'com.oumuo.central.PersonResource', "course")
       
    }
    def updateCurrent(Object instance, String owner, String name)
    {
        def grailsApplication = new Account().domainClass.grailsApplication
        def claz = grailsApplication.getClassForName(owner)
        
      
        if (instance) {
            log.trace "updateCurrent: preferences ${claz} ${instance} for $name $instance.id "
            def other = claz.findById(
                instance.id)."${name}Preferences".findAll {it.current == true}
  
            log.trace "updateCurrent: other before subtraction $other"
            // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
            log.trace "updateCurrent: other $claz after removing this - $other"
            if (other.size() > 1) {
                // should be 1 or zero
                log.warn "updateCurrent: ${other.size()} preferences found for $name"
            } else if (other.size() == 1) {
      
                other[0].current = false;
            
                log.trace "setCurrent: reseted other $claz $other[0] to false"
            } else {
                log.trace "setCurrent: no other $claz Preference with current = true"
            }
        } 
    }
}