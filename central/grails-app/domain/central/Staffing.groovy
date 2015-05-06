package central
import central.UserRole
import central.Person
import groovy.util.logging.Log4j

@Log4j
class Staffing {

    String name
    Long sequence
    Person person
    Organization organization
    UserRole userRole = UserRole.ROLE_MANAGER
    // Person owner (does not work well with sequence updates)
  
    Date startDate = new Date()
    Date endDate
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${sequence} ${name} ${userRole} "
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    // static belongsTo = [organization: Organization]
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        name (nullable:true)
        person (nullable:true)
        organization (nullable:true)
        userRole()
        organization (editable:true)
 
        startDate(nullable:true)
        endDate(nullable:true)
        dateCreated()
        lastUpdated()
    }

    
    def beforeInsert() {
        if (!sequence) {
                 
            // ideally should be created through organization
            if (organization)
            sequence = Organization.findById(organization.id).staffings.size() + 1
            else if (person)
            sequence = Person.findById(person.id).staffings.size() + 1
            else
            log.warn "beforeInsert: either organization or person should be not null"
            
            log.trace "beforeInsert: sequence updated to $sequence"
               
        }     
    }
    
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def staffingService = ctx.staffingService
     
        return staffingService.list()
    }

}