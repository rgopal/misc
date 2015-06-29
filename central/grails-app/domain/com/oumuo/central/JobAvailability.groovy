package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class JobAvailability {
 
    Job job
    Integer year
    Months month
    DataType dataType = DataType.OTHER
    Integer needed
    Integer available
    Date dataDate       // date when the data was collected

    String city
    State state
    Country country = Country.USA
    String source           // BLOS etc.
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$job $year"
    }
  
    static constraints = {
    
        job (editable:false, nullable:true)
        dataType (nullable:false)
        year (nullable:true, range: 1900..2100)
        month (nullable:true)
        needed (nullable:true, min:0)
        available (nullable:true, min:0)
        dataDate (nullable:true)          
   
        city (nullable:true)
        state (nullable:true)
        Country country
        source(nullabel:true)
        
        // in future make all other editable false as well
     
     
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
        def jobAvailabilityService = ctx.jobAvailabilityService
     
        return jobAvailabilityService.list()
    }
}