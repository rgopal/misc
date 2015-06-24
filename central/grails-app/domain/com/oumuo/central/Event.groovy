package com.oumuo.central
import com.oumuo.central.Person
import com.oumuo.lookup.*
import groovy.util.logging.Log4j


import org.joda.time.DateTime
import org.joda.time.Minutes

@Log4j
class Event {

    String name
    // If there are referred then hasMany will work but not single reference
    // object creation problem in initClazs
    // Clazs clazs
    // ClassSession classSession

    Date startTime
    Date endTime
 

    // Recurring Options
    boolean isRecurring = false
    Recurring recurType
    Integer recurInterval = 1

    // Backlink to original recurring event this event was created from
    Event sourceEvent

    static hasMany = [recurDaysOfWeek: Integer, excludeDays: Date]
    static transients = ['durationMinutes']
   
    Date recurUntil
    Integer recurCount
    
    Status status = Status.ACTIVE
    Date    dateCreated
    Date    lastUpdated
    
    static constraints = {
        //clazs(nullable:true)
        // classSession(nullable:true)
        name (nullable: true)
       
        recurType(nullable: true)
        recurInterval(nullable: true)
        recurUntil(nullable: true)
        recurCount(nullable: true)
    
        excludeDays(nullable: true)
        sourceEvent(nullable: true)
        // hibernate was choking on startTime not nullable
        // saw datepicker etc.
        startTime(nullable: true)
        endTime(nullable: true) // validator: {val, obj -> val > obj.startTime} )
        recurDaysOfWeek(validator: {val, obj ->
            if (obj.recurType == Recurring.WEEKLY && !val) {return 'null'}
            
        })
    
        status()
        dateCreated()
        lastUpdated()
    }

    public int getDurationMinutes() {
        
      Minutes.minutesBetween(new DateTime(startTime), new DateTime(endTime)).minutes
    }


    
 
    static secureList () {
        def grailsApplication = new Event().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def eventService = ctx.eventService
        return eventService.list()
    }
    
}