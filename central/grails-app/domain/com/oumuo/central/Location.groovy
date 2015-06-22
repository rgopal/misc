package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

class Location {

    String name

    Organization organization

    // these are common to all; state is managed by system
    LocationType locationType
    Integer capacity
    
    String locationUrl
    String building
    String floor
    String room
    State state 
    Float fee
    
    String workEmail
    String workPhone
    String mobilePhone
    String addressLine1
    String addressLine2
    String city 
    String province  
    Country country = Country.USA
    String zip
   
    Integer startHour
    Integer startMinute
    Integer endHour
    Integer endMinute
    
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${name} "
    }
   
    static constraints = {
        // named association so not needed owner()
        
        name (nullable:false)
     
        organization (nullable:true, editable:true)
        
        locationType(nullable:false)
        capacity(nullable:true)
    
          locationUrl(nullable:true, url:true)
     building (nullable:true)
     floor(nullable:true)
     room(nullable:true)
     state (nullable:false)
     fee(nullable:true)
        
  workEmail(nullable:true, email:true)
        workPhone(nullable:true, matches: '\\d{3}\\-\\d{7}')
        mobilePhone(nullable:true, matches: '\\d{3}\\-\\d{7}')   
        addressLine1(nullable:true)
        addressLine2(nullable:true)
        city (nullable:true)
        province (nullable:true)
        country (nullable:false)
        zip (nullable:true, size: 5..10, validator:{zip, obj ->
                if (!zip) {
                    true
                } else if (obj.country == Country.USA) {
                    (zip ==~ /^(\d{5}-\d{4})|(\d{5})$/) ? true : false 
                }
                else if (obj.country == Country.INDIA) {
                    (zip ==~ /^(\d{6}-\d{4})|(\d{6})$/) ? true : false
                } else {
                    true
                }
            })
        
 
        startHour(nullable:true, range:0..24)
        endHour(nullable:true, range:0..24)
              startMinute(nullable:true, range:0..60)
        endMinute(nullable:true, range:0..60)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Location().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def locationService = ctx.locationService
     
        return locationService.list()
    }

}