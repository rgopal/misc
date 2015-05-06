package com.oumuo.lookup
import com.oumuo.lookup.Status
import com.oumuo.lookup.Country
import com.oumuo.lookup.Language

class CountryStateCity {

    
   
    // class unique fields
     
    Country country
    String state
    String city
    Language language = Language.ENGLISH    // coutnry state cirty values in different languages
    
    Date dateCreated
    Date lastUpdated
    String comment

    Status status = Status.ACTIVE
    
    String toString(){

         "$country $state $city"
    }
        
    // should be constraints and not constraint
    static constraints = {
         
       
        city ()
        state()
        country ()
        language ()
        
        status ()
     
        dateCreated ()
        lastUpdated ()
        comment (nullable:true)
        language (nullable:true)
        
    }
    
}
