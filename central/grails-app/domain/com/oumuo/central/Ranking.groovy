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
    
    static hasMany =[organizationRankingItems: RankingItem, programRankingItems:RankingItem]
    static mappedBy = [organizationRankingItems:'organizationRanking',
        programRankingItems:'programRanking'] 
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
        organizationRankingItems()
        programRankingItems()
        
        overall(range:0..1000, editable:false)
        prestige(range:0..1000, editable:false) 
        learning(range:0..1000, editable:false) 
        affordability(range:0..1000, editable:false) 
        recommendation(range:0..1000, editable:false) 
        improvement(range:0..1000, editable:false) 
        jobPlacement(range:0..1000, editable:false)


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
        if (organization) {
            // use sequence number for organization
            sequence = organization.rankings ?
                organization.rankings.size() + 1 : 1
            log.trace "beforeInsert:  seqeuence is $sequence with organization "
            
        }
        if (program) {
            // use sequence number for organization
            sequence = program.rankings ?
            program.rankings.size() + 1 : 1
            log.trace "beforeInsert: seqeunce is $sequence with program "
            
        }
    }
    def beforeUpdate () {
        if (this.current == true) {
            checkMain()
        }
       
    }
    def checkMain() {
        // Ranking is for Organization, Course, etc. so find for each type
        setCurrent('organization', 'Organization')
    }
    def setCurrent(String instance, String claz)
    {
        if (ranking.$instance) {
            log.trace "setCurrent: rankings ${Claz} ${instance} "
            def other = $claz.findById(
                ranking.${instance}.id).rankings.findAll {it.current == true}
  
            // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
            log.trace "checkMain: other $claz after removing this - $other"
            if (other.size() > 1) {
                // should be 1 or zero
                log.warn "checkMain: ${other.size()} rankings found"
            } else if (other.size() == 1) {
      
                other[0].current = false;
            
                log.trace "setCurrent: reseted other $claz $other[0] to false"
            } else {
                log.trace "setCurrent: no other $claz Ranking with current = true"
            }
        } 
    }
}