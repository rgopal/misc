package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// Catalog includes a tree of catalog nodes (recursive) each referring to a
// a course and is created under the constraints of course prerequisites.  A root
// catalog can be recursively traversed from the catalog property of a program.
// Each course of a complete catalog are also listed in a program as a sorted
// list under the property allCourses (so each catalog node has a reference to
// that program.

class Catalog implements Comparable {
   
    // each level is sorted
    SortedSet subCatalogs
    static hasMany = [ subCatalogs: Catalog ] 
    static mappedBy = [subCatalogs: 'parentCatalog']
    
    Program program                 // weaker for sharing catalogs
    // parent could be null
    Catalog parentCatalog
    
    String name
    String sequence
    Person person
    Course course
    CourseType courseType = CourseType.REQUIRED
   
    //static belongsTo = [ program: Program ]
    static transients = ['allSubCatalogs']
     
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    // TODO  parse sequence and perform recursive numeric sort on each field 
    int compareTo(obj) {
        sequence.compareTo(obj.sequence)
    }
  
    def getAllSubCatalogs() {
        return subCatalogs ? subCatalogs*.allSubCatalogs.flatten() + subCatalogs : []
    }
    
    String toString(){

        sequence + " " + name?.substring(0,Math.min(15, name? name.length():0))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:true, editable:false)
        name(nullable:false)
        program(nullable:false)       
        // this allows the user to make parentCatalog null (and thus a new root)
        parentCatalog (nullable:true, editable:false)
        person(editable:false, nullable:false)
        // in future make all other editable false as well
        course (nullable:true)
        courseType (nullable:true) 
        
        subCatalogs()

        status()
        dateCreated()
        lastUpdated()
    }

    // beforeInsert is gone (included in CatalogService)
    
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Catalog ().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def catalogService = ctx.catalogService
     
        return catalogService.list()
    }
}