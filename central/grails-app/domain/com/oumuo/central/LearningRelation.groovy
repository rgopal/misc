package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class LearningRelation {
  
    Learning firstLearning
    Learning secondLearning
  
    static belongsTo = [program: Program]
    
    Relation relation = Relation.ALTERNATE


    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$firstLearning $relation $secondLearning"
    }
  
    static constraints = {
     
        firstLearning (editable:true)
        secondLearning (editable:true)
        relation (nullable:false)
        program(editable:false)
   
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
        def learningRelationService = ctx.learningRelationService
     
        return learningRelationService.list()
    }
}