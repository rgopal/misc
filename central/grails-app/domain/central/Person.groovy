package central

class Person {

    enum Status implements org.springframework.context.MessageSourceResolvable {
 
        ACTIVE, INACTIVE
 
        public Object[] getArguments() { [] as Object[] }
 
        public String[] getCodes() { [ name() ] }
 
        public String getDefaultMessage() { "?-" + name() }
    }
    Status status
    String name
    String country
    String city
    String state 

    Date dateCreated  
    Date dateUpdated
    static hasMany = [accounts: Account]

    // should be constraints and not constraint
    static constraints = {
    
        state(inList:['MD', 'VA', 'CA'])

    }
}
