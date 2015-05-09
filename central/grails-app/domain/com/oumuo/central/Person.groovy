package com.oumuo.central
import com.oumuo.lookup.*
import com.oumuo.lookup.UserRole as ROLE
import com.oumuo.UserLogin

class Person {

    // class specific enums
 
    enum Sex implements org.springframework.context.MessageSourceResolvable {
 
        MALE, FEMALE, UNKNOWN
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }
 
   
    // class unique fields
    // String login
    // String password
    String name     // this is full name
    String userName // it is unique and same as in UserLogin
    Sex sex = Sex.UNKNOWN
    Date dateOfBirth = Date.parse('dd-MM-yyyy','01-01-1960')
    Race race = Race.WHITE
    
    String addressLine1
    String addressLine2
    String city = 'Germantown'
    String state  = 'Maryland'
    String zip = '20878'
    Country country = Country.USA
    Language preferredLanguage = Language.ENGLISH

     // these are common to all; 
    Status status = Status.ACTIVE

    Date dateCreated
    Date lastUpdated
    
    String comment
    
    // will bring it back to composition, spent 2 days 3/30
    String homeEmail
    String workEmail
    String homePhone
    String workPhone
    String mobilePhone
    // NOT WORKING UserLogin userLogin  //hasOne led to null issue

   
    // associations
    static hasMany = [accounts:Account, personRoles: PersonRole, 
        staffings:Staffing, comments:Comment, programs:Program, catalogs:Catalog]

    static belongsTo = [userLogin: UserLogin]
    
    String toString(){

         "$name"
    }
        
     
    // problems with embedded (or non embedded composition)
    // static embedded = ['homeEmail', 'workEmail']
    
    // should be constraints and not constraint
    static constraints = {
     
        //  login blank:false, size:5..15,matches:/[\S]+/, unique:true
        //  password blank:false, size:5..15,matches:/[\S]+/, display:false
        // this is another table with bidirectional reference so one has to be null
        // userLogin(nullable:true)
        name (blank:false, size:2..64)
        // already their belongsTo userName(blank:false, editable:false)
        sex ()
        dateOfBirth(max: new Date(), min:Date.parse('dd-MM-yyyy','01-01-1901'), nullable:true)
        race (nullable:true)
        status ()
        preferredLanguage(nullable:false)
     
        personRoles()
        staffings()
        accounts ()
        comments ()
        userLogin(editable:false)
        homeEmail(nullable:true, email:true)
        workEmail(nullable:true, email:true)
        homePhone(nullable:true, matches: '\\d{3}\\-\\d{7}')
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
        
      
        dateCreated ()
        lastUpdated ()
        comment (nullable:true, maxSize:1000)
        
    }
    static secureList () {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def personService = ctx.personService
        return personService.list()
    }
    /* DID NOT WORK static mapping = { 
    country defaultValue: "'United States'"  
    }
     */
}
