package central
import central.Person
import groovy.util.logging.Log4j

@Log4j
class WebSite {

    String name
  
    // Person owner (does not work well with sequence updates)
 
    URL url
    boolean loginSupported = false
    Date dateCreated
    Date lastUpdated

    String toString(){
            "${name} $url"
    }
 
    static constraints = {   
        name (nullable:true)
        loginSupported()
        url()
        dateCreated()
        lastUpdated()
    }
    
}