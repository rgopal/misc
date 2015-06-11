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
    Integer presentation = 500
    Integer content = 500
    Integer assistance = 500
    Integer convenience = 500
    Integer collaboration = 500
    Integer assessment = 500
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
            presentation (range:0..1000, editable:false, nullable:true)
     content(range:0..1000, editable:false, nullable:true)
     assistance (range:0..1000, editable:false, nullable:true)
     convenience (range:0..1000, editable:false, nullable:true)
     collaboration (range:0..1000, editable:false, nullable:true)
     assessment (range:0..1000, editable:false, nullable:true)
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
        def rankingItemService = ctx.rankingItemService
        return rankingItemService.list()
    }
}