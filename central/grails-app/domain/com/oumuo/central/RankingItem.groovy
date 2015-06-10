package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class RankingItem {
   
    String name
    Ranking organizationRanking     // future would have more rankings
    Ranking programRanking          //
    Ranking courseRanking
    
    static belongsTo = [person:Person]
    
    // Person owner (does not work well with sequence updates)
    // Start with middle point (but could be null also)

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
    
    "${name} $lastUpdated"
    }

    static constraints = {
        // named association so not needed owner()
        name()
        person(editable:false)
        organizationRanking(editable:false, nullable:true)
        programRanking(editable:false, nullable:true)
         courseRanking(editable:false, nullable:true)
        
        overall(range:0..1000, nullable:true)
        prestige(range:0..1000, nullable:true) 
        learning(range:0..1000, nullable:true) 
        affordability(range:0..1000, nullable:true) 
        recommendation(range:0..1000, nullable:true) 
        improvement(range:0..1000, nullable:true) 
        jobPlacement(range:0..1000, nullable:true)

        expiryDate(nullable:true)
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def rankingItemService = ctx.rankingItemService
        return rankingItemService.list()
    }
}