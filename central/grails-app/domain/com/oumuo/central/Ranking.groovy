package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Ranking {
    
    def grailsApplication
   
    Long sequence
    
    String name             // updated by cron job, e.g May 2015 Ranking
    Boolean current = false
    Organization organization
    Program program
    Course course
    
    static hasMany =[organizationRankingItems: RankingItem, 
        programRankingItems:RankingItem,
        courseRankingItems:RankingItem]
    static mappedBy = [organizationRankingItems:'organizationRanking',
        programRankingItems:'programRanking',
        courseRankingItems:'courseRanking'] 
    static belongsTo = [person:Person]
    
    // Person owner (does not work well with sequence updates)

    Integer overall = 500
    Integer prestige = 500
    Integer learning = 500
    Integer affordability = 500
    Integer recommendation = 500
    Integer improvement = 500
    Integer jobPlacement = 500

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
        organization(editable:false, nullable:true)
        program(editable:false, nullable:true)
        course(editable:false, nullable:true)
        
        organizationRankingItems()
        programRankingItems()
        courseRankingItems()
        
        overall(range:0..1000, editable:false, nullable:true)
        prestige(range:0..1000, editable:false, nullable:true) 
        learning(range:0..1000, editable:false, nullable:true) 
        affordability(range:0..1000, editable:false, nullable:true) 
        recommendation(range:0..1000, editable:false, nullable:true) 
        improvement(range:0..1000, editable:false, nullable:true) 
        jobPlacement(range:0..1000, editable:false, nullable:true)


        expiryDate(nullable:true)
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def rankingService = ctx.rankingService
        return rankingService.list()
    }

    // should be here and not in Service
    
    def beforeInsert() {
        if (!sequence) {
            // Init* even when does not provide seqeuence, it gets initialzied
            // to 2, so better to check for all prepopuldated records.
            if (organization) {
                // go the persistence to get existing rankings
   
                sequence = Organization.findById(organization.id).rankings.size()  + 1
                log.trace "beforeInsert:  seqeuence is $sequence with organization "
            
            }
            if (program) {
                // use sequence number for organization
                sequence = Program.findById(program.id).rankings.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with program "
            
            }
             if (course) {
                // use sequence number for organization
                sequence = Course.findById(course.id).rankings.size()  + 1
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
        
        // Ranking is for Organization, Course, etc. so find for each type
          
        log.trace "checkMain: $program and $organization will both be checked"
        if (organization)
        updateCurrent(organization, 'com.oumuo.central.Organization')
        else if (program)
        updateCurrent(program, 'com.oumuo.central.Program')
          else if (course)
        updateCurrent(course, 'com.oumuo.central.Course')
    }
    def updateCurrent(Object instance, String owner)
    {
        def grailsApplication = new Account().domainClass.grailsApplication
        def claz = grailsApplication.getClassForName(owner)
        
      
        if (instance) {
            log.trace "updateCurrent: rankings ${claz} ${instance} for $instance.id "
            def other = claz.findById(
                instance.id).rankings.findAll {it.current == true}
  
            log.trace "updateCurrent: other before subtraction $other"
            // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
            log.trace "updateCurrent: other $claz after removing this - $other"
            if (other.size() > 1) {
                // should be 1 or zero
                log.warn "updateCurrent: ${other.size()} rankings found"
            } else if (other.size() == 1) {
      
                other[0].current = false;
            
                log.trace "setCurrent: reseted other $claz $other[0] to false"
            } else {
                log.trace "setCurrent: no other $claz Ranking with current = true"
            }
        } 
    }
}