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
                        
            def count = PersonRole.createCriteria().count {
                person {
                    eq ('id', person.id)
                }
            }
           
            log.trace "beforeInsert: count is $count for $person"
            if (count == 0) {
                sequence = 1
            } else {
                
                sequence = PersonRole.createCriteria().get {
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
        
        def other = PersonRole.createCriteria().get {
            person {
                eq ('id', person.id)
            }
            eq ('current', true)
        }
        if (other) {
            other.current = false;
            log.trace "checkMain: resetting other $other to false"
        } else {
            log.trace "checkMain: no other PersonRole with current = true"
        }
       
    }
}