package central
import central.UserRole
import central.Person
import groovy.util.logging.Log4j

@Log4j
class Staffing {

    String name
    Long sequence
    UserRole userRole = UserRole.ROLE_MANAGER
    // Person owner (does not work well with sequence updates)
  
    Date startDate = new Date()
    Date endDate
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${sequence} ${name} "
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = [organization: Organization]
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        name (nullable:true)
        userRole()
        organization (editable:false)
 
        startDate(nullable:true)
        endDate(nullable:true)
        dateCreated()
        lastUpdated()
    }

    
    def beforeInsert() {
        if (!sequence) {
                           
            def count = Account.createCriteria().count {
                person {
                    eq ('id', person.id)
                }
            }
           
            log.trace "beforeInsert: count is $count for $person"
            if (count == 0) {
                sequence = 1
            } else {
                
                sequence = Account.createCriteria().get {
                    projections {
                        max('sequence')
                    }
                    person {
                        eq ('id', person.id)
                    }
                } + 1
            }
            // log.debug ("beforeInsert: person.id ${person.id} sequence = ${sequence}")
            
        }
      
        
    }
   

}