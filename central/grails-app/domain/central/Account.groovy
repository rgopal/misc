package central
import central.Person

class Account {
    String email 
    String userName
    Person owner

    String toString(){

    "$email"
    }
    // only one Person can own an Account (owner) with cascaded deletes
    // without belongsTo, an account can be associated with multiple persons
    // akin to a lookup field (instead of true master-detail
    static belongsTo = Person
    static constraints = {
       
    email email: true, blank: false
    }
}
