package central

import central.Person
import central.Language
import central.UserRole
import groovy.util.logging.Log4j

@Log4j
class PersonRole {
   
    Long sequence
    Boolean current = false
    // Person owner (does not work well with sequence updates)

    central.Language language = Language.ENGLISH
    UserRole userRole = UserRole.ROLE_ANONYMOUS
    Date expiryDate
    Date dateCreated
    Date lastUpdated

    String toString(){

        def tag = ""
        if (current == true) {
            tag = " *"
        }
    "${sequence} ${userRole} ${tag}"
    }
    // only one Person can own an PersonRole (owner) with cascaded deletes
    // without belongsTo, an personRole can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = [person: Person]
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        current()
        userRole()
        person(editable:false)
        expiryDate(nullable:true)
        dateCreated()
        lastUpdated()
    }
 
    def beforeInsert() {
        if (!sequence) {
                        
            sequence = Person.findById(person.id).personRoles.size() + 1
            log.trace "beforeInsert: sequence updated to $sequence"
            
        }
        // if this has become current then other should becomem false
        if (this.current == true) {
            checkMain()
        }
        
    }
    def beforeUpdate () {
        if (this.current == true) {
            checkMain()
        }
       
    }
    def checkMain() {
        // does not work other = PersonRole.findByPersonAndMain(this.person.id, current:true)
        
        // find all records with current to be true and not equal to current personRole record
        log.trace "checkMain: personRoles ${Person.findById(person.id).personRoles.findAll {it.current == true}}"
        def other = Person.findById(person.id).personRoles.findAll {it.current == true}
  
        // beforeInsert will not select the current record, but beforeUpdate will
       
            other = other - this
      
        log.trace "checkMain: other after removing this - $other"
        if (other.size() > 1) {
            // should be 1 or zero
            log.warn "checkMain: ${other.size()} personRoles found"
        } else if (other.size() == 1) {
      
            other[0].current = false;
            
            log.trace "checkMain: reseted other $other[0] to false"
        } else {
            log.trace "checkMain: no other PersonRole with current = true"
        }
       
    }
}