package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.Seconds

@Log4j

// Within a program learning is associated with assessment

class PersonContent {
  
    PersonInstruction personInstruction
    Content content
   PersonAssessment personAssessment
   // PersonAssessmentItem personAssessmentItem
      static hasMany = [pauseTimes: Date, restartTimes: Date]
      static transients = ['durationSeconds']
      
    ContentLocation contentLocation = ContentLocation.PAGE
    String sectionLocation
    Integer pageLocation
    Float percentLocation
    Float timeLocation

 
    String annotation
    String annotationUrl
    String annotationLocation       // could be highlighted etc.
    
    
    State state = State.STARTED
    Date startTime
    Date endTime
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    public int getDurationSeconds() {
        
        def total = 0
        def prev = startTime
        def sortedRestartTimes = restartTimes.sort()
        def i = 0
        if (pauseTimes.size() < (restartTimes.size()  -1 ))
          log.warn "getDurationMinutes: pauses are too fiew pauses - ${pauseTimes.size() }" +
                    " restartrs - ${restartTimes.size()}"
        for (pause in pauseTimes.sort()) {
            
            total = total + Seconds.secondsBetween(new DateTime(prev), new DateTime(pause)).seconds
            if (sortedRestartTimes[i])
               prev = sortedRestartTimes[i]
               else 
               prev = null
             i++
             log.warn ("getDurationSeconds $i $pause total $total prev $prev")
        }
        if (endTime && prev)
            total = total + Seconds.secondsBetween(new DateTime(prev), new DateTime(endTime)).seconds
        
      return total
    }

    
    String toString(){

        "$personInstruction $content"
    }
  
    static constraints = {
    
        content(nullable:true, editable:true)
        personInstruction(nullable:true, editable:false)
        personAssessment (editable:true, nullable:true)
        // personAssessmentItem (editable:true, nullable:true)
        pauseTimes()
        restartTimes()
        
      
            state (nullable:false)
        startTime(nullable:true)
        endTime(nullable:true)
        
        contentLocation(nullable:false)
        sectionLocation (nullable:true)
         pageLocation (nullable:true)
     percentLocation (nullable:true)
     timeLocation (nullable:true)
     
   
     annotation (nullable:true)
     annotationUrl (nullable:true)
     annotationLocation (nullable:true)
        

     
        status()
        dateCreated()
        lastUpdated()
    }


    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def personContentService = ctx.personContentService
     
        return personContentService.list()
    }
  
}