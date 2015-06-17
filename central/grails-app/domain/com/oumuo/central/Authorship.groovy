package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Authorship {
  
    // a person can author course, learning, assessment, program
    // Material provides inheritance for code and common fields
    // But here children of Material (Course, Learning...) are used
    // to keep the web infrastructure happy (otherwise Material would
    // show up in CRUD.  ACL also started looking for Material entry
    // 
    // They are used only one at a time
    
    Person person
    Course course
    Learning learning
    Assessment assessment
    Program program
     
    Role role = Role.PRIMARY
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$person $role "
    }
  
    static constraints = {
    
        person (editable:true, nullable:false)
        course (editable:true, nullable:true)
        learning (editable:true, nullable:true)
        assessment (editable:true, nullable:true)
        program (editable:true, nullable:true)
        role (nullable:false)
    
        // in future make all other editable false as well
     
      
   
        status() 
        dateCreated()
        lastUpdated()
    }


    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def authorshipService = ctx.authorshipService
     
        return authorshipService.list()
    }
}