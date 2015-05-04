package central
import central.Person
import groovy.util.logging.Log4j

@Log4j
class Account {

    String name
    Long sequence
    Boolean main = false
    // Person owner (does not work well with sequence updates)
    String email
    WebSite webSite
    String loginName
    String password
    Date expiryDate
    Date dateCreated
    Date lastUpdated

    String toString(){

        def tag = ""
        if (main == true) {
            tag = " *"
        }
    "${sequence} ${name} ${tag}"
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = [person: Person]
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        name (nullable:true)
        main()
        person(editable:false)
        email (email: true, blank: false, nullable:true)
        webSite(nullable: true, editable: true)
        loginName(nullable:true)
        password(nullable:true)
        expiryDate(nullable:true)
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
            sequence = Person.findById(person.id).accounts.size() + 1
            log.trace "beforeInsert: sequence updated to $sequence"
           
        }
        // if this has become main then other should becomem false
        if (this.main == true) {
            checkMain()
        }
        
    }
    def beforeUpdate () {
        if (this.main == true) {
            checkMain()
        }
       
    }
    def checkMain() {
        // does not work other = Account.findByPersonAndMain(this.person.id, main:true)
        
        /*
        def other = Account.createCriteria().get {
        person {
        eq ('id', person.id)
        }
        eq ('main', true)
        }
         */
        // find all records with main to be true and not equal to current account record
        log.trace "checkMain: accounts ${Person.findById(person.id).accounts.findAll {it.main == true}}"
        def other = Person.findById(person.id).accounts.findAll {it.main == true}
  
        // remove this (record which is set to true) from the list
        // used to have a check for this.id != null but now the Ids are there
        // beforeInsert will not select the current record, but beforeUpdate will
       
        other = other - this
     
        log.trace "checkMain: other after removing this - $other"
        if (other.size() > 1) {
            // should be 1 or zero
            log.warn "checkMain: ${other.size()} accounts found"
        } else if (other.size() == 1) {
            other[0].main = false;         
            log.trace "checkMain: reseted other $other[0] to false"
        } else {
            log.trace "checkMain: no other Account with main = true"
        }
       
    }
}