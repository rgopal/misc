package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// A program hasOne catalog so it will appear as Add Catalog

class Program {

    String name

    Person person
    Organization organization
    Integer ranking = 500           // automatically derived from multiple items
    Credential credential = Credential.NONE  
    AcademicStratum academicStratum = AcademicStratum.OTHER
    AcademicMajor academicMajor = AcademicMajor.GENERAL 
    Grade minimumGrade = Grade.C
    Double minimumPercentage
    AcademicSession academicSession = AcademicSession.FREE_FORM
    Float sessionFee = 0.0f
    
    static hasOne = [catalog: Catalog]                     // root catalog
    static hasMany = [allCatalogs:Catalog]  // flattened list for browsing
 
    Date startDate = new Date()
    Date endDate
     // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    String toString(){

    "${name} "
    }
   
    static constraints = {
        // named association so not needed owner()
        
        name (nullable:false)
        person (nullable:false, editable:false)
        organization (nullable:true, editable:true)
 
        catalog(nullable:true, editable:true)   // to create structure
        allCatalogs(editable:false)         // for browsing only
        
        credential()
        academicStratum()
        academicMajor()
        minimumGrade()
        minimumPercentage(min:0.0d, max:100.0d, nullable:true)
        academicSession()
        sessionFee(nullable:true)
        ranking(range:0..1000, editable:false)
 
        startDate(nullable:true)
        endDate(nullable:true)
        
        status()
        dateCreated()
        lastUpdated()
    }


    static secureList() {
        def grailsApplication = new Program().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def programService = ctx.programService
     
        return programService.list()
    }

}