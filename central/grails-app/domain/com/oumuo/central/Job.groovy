package com.oumuo.central
import com.oumuo.lookup.*


class Job {

    static hasMany = [
        requirements: Requirement,
        personJobs: PersonJob,
        rankings: Ranking,
        jobAvailabilitys: JobAvailability,
        positions: Position
    ]
    String name     // this is full name

    String city 
    String state 
    String zip
    Country country = Country.USA
    Language preferredLanguage = Language.ENGLISH
    
    RegionType regionType = RegionType.URBAN
     JobType jobType = JobType.OTHER
       PositionType positionType = PositionType.OTHER 
       
    Integer year
    Float averageSalary
    Float medianSalary
    Float varianceSalary
    String source

    
    boolean healthBenefit
    boolean retirementBenefit
    boolean relocationBenefit
  

    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated
    String comment
    


    String toString(){

         "$name"
    }
    static constraints = {
  
        requirements()
        personJobs()
        rankings()
        jobAvailabilitys()
        positions()
        
        name (blank:false, size:2..64)      
        status ()
        preferredLanguage(nullable:false)
        country(nullable:false)
        regionType(nullable:false)
        jobType()
         positionType (nullable:false)
         source (nullable:true)
         
        year(nullable:true, range:1900..2100)
        averageSalary(nullable:true, min: 0.0f)
        medianSalary(nullable:true, min: 0.0f)
        varianceSalary(nullable:true, min: 0.0f)
   
    
        healthBenefit ()
        retirementBenefit()
        relocationBenefit()
       
  
        city (nullable:true)
        state(nullable:true)
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
        // validator takes 3 (1 is value, 2 obj, 3 error
        // returns true or null (both good) or error messages
    
        status()
        dateCreated ()
        lastUpdated ()
        comment (nullable:true, maxSize:1000)
        
    }
   
    static secureList () {
        def grailsApplication = new Job().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def jobService = ctx.jobService
      
        return jobService.list()
    }
}
