package central
import central.Status
import central.Country
import central.Language
import central.Email
import central.Race

class Person {

    // class specific enums
 
    enum Sex implements org.springframework.context.MessageSourceResolvable {
 
        MALE, FEMALE, UNKNOWN
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }
 
   
    // class unique fields
     
    String name
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

    // common across all domains
    
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

   
    // associations
    static hasMany = [accounts:Account]

    String toString(){

         "$name"
    }
        
     
    // problems with embedded (or non embedded composition)
    // static embedded = ['homeEmail', 'workEmail']
    
    // should be constraints and not constraint
    static constraints = {
     
        name (blank:false, size:2..64)
        sex ()
        dateOfBirth(max: new Date(), min:Date.parse('dd-MM-yyyy','01-01-1901'), nullable:true)
        race (nullable:true)
        status ()
        preferredLanguage(nullable:false)
     
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
        
        accounts ()
   
        dateCreated ()
        lastUpdated ()
        comment (nullable:true, maxSize:1000)
        
    }
    /* DID NOT WORK static mapping = { 
    country defaultValue: "'United States'"  
    }
     */
}
