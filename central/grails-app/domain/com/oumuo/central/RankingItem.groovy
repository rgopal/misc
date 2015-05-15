package com.oumuo.central


import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class RankingItem {
   
    String name
    Ranking organizationRanking     // future would have more rankings
    Ranking programRanking          //
    
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
    
    "${name} $lastUpdated"
    }

    static constraints = {
        // named association so not needed owner()
        name()
        person(editable:false)
        organizationRanking(editable:false, nullable:true)
        programRanking(editable:false, nullable:true)
        
        overall(range:0..1000)
        prestige(range:0..1000) 
        learning(range:0..1000) 
        affordability(range:0..1000) 
        recommendation(range:0..1000) 
        improvement(range:0..1000) 
        jobPlacement(range:0..1000)

        expiryDate(nullable:true)
        status()
        dateCreated()
        lastUpdated()
    }
 
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def rankingItemInstanceService = ctx.rankingItemInstanceService
        return rankingItemInstanceService.list()
    }
}