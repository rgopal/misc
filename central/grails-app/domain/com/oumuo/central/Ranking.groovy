package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Ranking {
    
   
    Long sequence
    
    String name             // updated by cron job, e.g May 2015 Ranking
    Boolean current = false
    Organization organization
    Program program
    Course course
    Learning learning
    Assessment assessment
    Job job
    
    static hasMany =[organizationRankingItems: RankingItem, 
        programRankingItems:RankingItem,
        courseRankingItems:RankingItem,
        jobRankingItems: RankingItem]
    static mappedBy = [organizationRankingItems:'organizationRanking',
        programRankingItems:'programRanking',
        courseRankingItems:'courseRanking',
    jobRankingItems:'jobRanking'] 
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
        organization(editable:false, nullable:true)
        program(editable:false, nullable:true)
        course(editable:false, nullable:true)
         learning(editable:false, nullable:true)
        assessment(editable:false, nullable:true)
        job(editable:false, nullable:true)
        
        organizationRankingItems()
        programRankingItems()
        courseRankingItems()
        jobRankingItems()
        
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
            if (assessment) {
                // use sequence number for organization
                sequence = Assessment.findById(assessment.id).rankings.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with assessment "
            
            }
            if (learning) {
                // use sequence number for organization
                sequence = Learning.findById(learning.id).rankings.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with learning "
            
            }
             if (job) {
                // use sequence number for organization
                sequence = Job.findById(job.id).rankings.size()  + 1
                log.trace "beforeInsert: seqeunce is $sequence with job "
            
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
        else if (assessment)
        updateCurrent(assessment, 'com.oumuo.central.Assessment')
        else if (learning)
        updateCurrent(learning, 'com.oumuo.central.Learning')
        else if (job)
        updateCurrent(job, 'com.oumuo.central.Job')
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