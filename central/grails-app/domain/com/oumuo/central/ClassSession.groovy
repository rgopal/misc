package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// ClassSession includes a tree of classSession nodes (recursive) each referring to a
// a Learning.  A root classSession can be recursively traversed from Course.
// Each Learning of a complete classSession are also listed in a program as a sorted
// list under the property allLearnings (so each classSession node has a reference to
// that program.

class ClassSession implements Comparable {
   
    // could have 0 or more learning (and then get assessments from
    // LearningAssessment
    
    Clazs clazs
    static hasMany = [
        learnings: Learning
       
    ]
    
    String name
    String sequence
    // Location location
    
    Date earliestStart
    Date latestStart
    Date earliestEnd
    Date latestEnd
    
    Date actualStart
    Date actualEnd
    
    State state
   
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    // TODO  parse sequence and perform recursive numeric sort on each field 
    int compareTo(obj) {
        sequence.compareTo(obj.sequence)
       
    }
    
    def getAllSubClassSessions() {
        return subClassSessions ? subClassSessions*.allSubClassSessions.flatten() + subClassSessions : []
    }
    
    String toString(){

        sequence + " " + name?.substring(0,Math.min(15, name? name.length():0))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:true, editable:false)
        name(nullable:false)
             
        clazs(nullable:false, editable:false)

        learnings()
 
        earliestStart (nullable:true)
        latestStart(nullable:true)
        earliestEnd(nullable:true)
        latestEnd(nullable:true)
    
        actualStart(nullable:true)
        actualEnd(nullable:true)
    
        state(nullable:true)

        status()
        dateCreated()
        lastUpdated(nullable:true)
    }

    // beforeInsert is gone (included in ClassSessionService)
    
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new ClassSession ().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def classSessionService = ctx.classSessionService
     
        return classSessionService.list()
    }
}