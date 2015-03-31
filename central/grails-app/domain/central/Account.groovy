package central
import central.Person

class Account {
    String name
    Long sequence
    Boolean main = false
    Person owner
    String email
    Date dateCreated
    Date lastUpdated

    String toString(){

    "$name"
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = Person
    static constraints = {
        owner()
        sequence (nullable:false)
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
}
