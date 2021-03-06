package com.oumuo.lookup
import com.oumuo.central.Person
import groovy.util.logging.Log4j

@Log4j
class WebSite {

    String name
  
    // Person owner (does not work well with sequence updates)
 
    String siteUrl
    boolean loginSupported = false
    Date dateCreated
    Date lastUpdated

    String toString(){
            "${name} $url"
    }
 
    static constraints = {   
        name (nullable:true)
        loginSupported()
        siteUrl(nullable:true, url: true)
        dateCreated()
        lastUpdated()
    }
    
}
