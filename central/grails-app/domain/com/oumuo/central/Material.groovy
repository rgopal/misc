package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Material {
  
    // a person can author material, learning, assessment, program
    // try inheritance with this (has its own Service but not controller
    Status status = Status.ACTIVE
  
    
    static hasMany = [
        authorships: Authorship
    ]
    
  Date dateCreated
    Date lastUpdated
    // these are common to all; state is managed by system
    
    
    static constraints = {
    
          status() 
        dateCreated()
        lastUpdated()
        
        authorships(display:true)
    
        // in future make all other editable false as well
     
     
    }

      static secureList() {
        def grailsApplication = new Material().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def materialService = ctx.materialService
     
        return materialService.list()
    }

    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
  
}