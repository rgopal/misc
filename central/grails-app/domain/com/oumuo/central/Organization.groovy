package com.oumuo.central
import com.oumuo.lookup.*


class Organization {

    String name     // this is full name

    
    // associations
    static hasMany = [staffings:Staffing, comments: Comment, programs:Program, 
        courses:Course, rankings:Ranking, learnings: Learning]

    String addressLine1
    String addressLine2
    String city = 'Germantown'
    String state  = 'Maryland'
    String zip = '20876'
    Country country = Country.USA
    Language preferredLanguage = Language.ENGLISH
    AcademicStratum academicStratum = AcademicStratum.OTHER
    OrganizationType organizationType = OrganizationType.OTHER

  // these are common to all
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated
    
    String comment

    String workEmail
    String workPhone
    String mobilePhone

    String toString(){

         "$name"
    }
    static constraints = {
  
        name (blank:false, size:2..64)
    
        staffings()
        comments()
        programs()
        courses()
        learnings()
        rankings()
        
        status ()
        preferredLanguage(nullable:false)
        academicStratum(nullable:true)
        organizationType()

        workEmail(nullable:true, email:true)
        workPhone(nullable:true, matches: '\\d{3}\\-\\d{7}')
        mobilePhone(nullable:true, matches: '\\d{3}\\-\\d{7}')
    
        addressLine1(nullable:true)
        addressLine2(nullable:true)
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
        def grailsApplication = new Organization().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def organizationService = ctx.organizationService
      
        return organizationService.list()
    }
}
