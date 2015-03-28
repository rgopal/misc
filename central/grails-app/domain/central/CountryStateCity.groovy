package central
import central.Status

class CountryStateCity {

    enum Country implements org.springframework.context.MessageSourceResolvable {
 
        USA, INDIA, CANADA, MEXICO
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }
   
    // class unique fields
     
    Country country
    String state
    String city
    String language = ''    // coutnry state cirty values in different languages
    
    Date dateCreated
    Date lastUpdated
    String comment

    Status status = Status.ACTIVE
    
    String toString(){

         "$country $state $city"
    }
        
    // should be constraints and not constraint
    static constraints = {
         
        status ()
        city ()
        state()
        country ()
     
        dateCreated ()
        lastUpdated ()
        comment (nullable:true)
        language (nullable:true)
        
    }
    
}
