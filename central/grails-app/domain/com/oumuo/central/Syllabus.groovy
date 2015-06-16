package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Syllabus includes a tree of syllabus nodes (recursive) each referring to a
// a Learning.  A root syllabus can be recursively traversed from Course.
// Each Learning of a complete syllabus are also listed in a program as a sorted
// list under the property allLearnings (so each syllabus node has a reference to
// that program.

class Syllabus implements Comparable {
   
    // each level is sorted
    SortedSet subSyllabuss
    static hasMany = [ subSyllabuss: Syllabus ] 
    static mappedBy = [ subSyllabuss: 'parentSyllabus']
    
    Course course                 // weaker for sharing syllabuss
    Learning learning //also section
    
    // parent could be null
    Syllabus parentSyllabus
    
    String name
    String sequence
   
    //static belongsTo = [ program: Program ]
    static transients = ['allSubSyllabuss']
     
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    // TODO  parse sequence and perform recursive numeric sort on each field 
    int compareTo(obj) {
        sequence.compareTo(obj.sequence)
    }
  
    def getAllSubSyllabuss() {
        return subSyllabuss ? subSyllabuss*.allSubSyllabuss.flatten() + subSyllabuss : []
    }
    
    String toString(){

        sequence + " " + name?.substring(0,Math.min(15, name? name.length():0))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:true, editable:false)
        name(nullable:false)
             
        // this allows the user to make parentSyllabus null (and thus a new root)
        parentSyllabus (nullable:true, editable:false)
     
        // in future make all other editable false as well
        course (nullable:true)
        learning()
        
        subSyllabuss()

        status()
        dateCreated()
        lastUpdated()
    }

    // beforeInsert is gone (included in SyllabusService)
    
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Syllabus ().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def syllabusService = ctx.syllabusService
     
        return syllabusService.list()
    }
}