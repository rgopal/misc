package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Within a program learning is associated with assessment

class PersonAssessmentItem {
  
    Long sequence
    PersonAssessment personAssessment
    AssessmentItem assessmentItem
       static hasMany = [
 
        comments : Comment,
        personContents: PersonContent
    ]
  
    // Does not make sense String sequence
  
 
    State state = State.STARTED
    Date startTime
    Date endTime
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    
    String toString(){

        "$sequence $personInstruction"
    }
  
    static constraints = {
    
        sequence(nullable:true, editable:false)
        personAssessment (editable:true, nullable:true)
        assessmentItem (editable:true, nullable:true)
     
        comments ()
        personContents()
    
        
        state (nullable:false)
        startTime(nullable:true)
        endTime(nullable:true)
     
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
        def personAssessmentItemService = ctx.personAssessmentItemService
     
        return personAssessmentItemService.list()
    }
    // number them after all prior instructions for a person
       def beforeInsert() {
        if (!sequence) {

            // InitPerson uses explict 1 for sequence
            // sequence = Person.findById(person.id).personAssessmentItems*.getAll{it->instruction.id == instruction.id}.size() + 1
            sequence = PersonAssessmentItem.countByPersonAndInstruction(person, instruction) + 1
            log.trace "beforeInsert: sequence updated to $sequence"
            
        }
       }
}