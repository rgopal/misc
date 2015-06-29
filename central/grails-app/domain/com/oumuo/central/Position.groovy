package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Position {
 
    Job job
    static hasMany = [
        requirements: Requirement
    ]
    String company
    Industry industry = Industry.OTHER
    Float salary
    boolean healthBenefits
    boolean retirementBenefits 
    Date startDate
    JobType positionType = JobType.OTHER
    
    String skillRequirements
    String knowledgeRequirement

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
        requirements()
        
        company(nullable:true)
        industry()
        salary (nullable:true, min:0.0f)
        healthBenefits()
        retirementBenefits ()
        startDate (nullable:true)
        positionType()
    
        skillRequirements (nullable:true)
        knowledgeRequirement (nullable:true)
   
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
        def positionService = ctx.positionService
     
        return positionService.list()
    }
}