package central
import central.Status
import central.Country
import central.Language
import central.Email
import central.AcademicStratum
import central.OrganizationType

class Organization {

    String name     // this is full name
    
    String addressLine1
    String addressLine2
    String city = 'Germantown'
    String state  = 'Maryland'
    String zip = '20876'
    Country country = Country.USA
    Language preferredLanguage = Language.ENGLISH
    AcademicStratum academicStratum = AcademicStratum.OTHER
    OrganizationType organizationType = OrganizationType.OTHER

    // common across all domains
    
    Status status = Status.ACTIVE
    
    Date dateCreated  
    Date lastUpdated 
    String comment

    String workEmail
    String workPhone
    String mobilePhone

    // associations
    static hasMany = [staffings:Staffing]

    String toString(){

         "$name"
    }
    static constraints = {
  
        name (blank:false, size:2..64)
    
        status ()
        preferredLanguage(nullable:false)
        academicStratum()
        organizationType()
        staffings()
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
        
        dateCreated ()
        lastUpdated ()
        comment (nullable:true, maxSize:1000)
        
    }
   
}
