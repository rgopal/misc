package central
import central.Person
import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

@Log4j
class Account {
        
    // def log = LogFactory.getLog(this)
    String name
    Long sequence
    Boolean main = false
    // Person owner (does not work well with sequence updates)
    String email
    Date dateCreated
    Date lastUpdated

    String toString(){

    "$name"
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = [person: Person]
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true)
        name (nullable:true)
        main()
        email (email: true, blank: false, nullable:true)
        dateCreated()
        lastUpdated()
    }
    /* did not work.  This should not be used.  Sequence should be user
     * friendly value
    static mapping = {
    sequence generator:'sequence', params:[sequence:'sequence_account',
    initial_value:1000]
    }
     */
    def beforeInsert() {
        if (!sequence) {
                        
            def count = Account.createCriteria().count {
                person {
                    eq ('id', person.id)
                }
            }
           
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