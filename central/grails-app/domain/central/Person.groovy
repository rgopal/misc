package central

class Person {

    enum Status implements org.springframework.context.MessageSourceResolvable {
 
        ACTIVE, INACTIVE
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }
    Status status = Status.INACTIVE
    String name
    String country = 'United States'
    String city = 'NY'
    String state 

    Date dateCreated  
    Date lastUpdated        
    static hasMany = [accounts: Account]

    // should be constraints and not constraint
    static constraints = {
        name (blank:false, minSize:2)
        status ()
        city ()
        state(inList:['MD', 'VA', 'CA'])
        country ()
        dateCreated ()
        lastUpdated ()

    }
    /* DID NOT WORK static mapping = { 
        country defaultValue: "'United States'"  
    }
    */
}
