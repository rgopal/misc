package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class StudentProgram {
  
   
    // M2M between Student and Program
    // Personlized program for a student
    
    String name
    Person person
    Program program  // the new program for a student
    Program clonedFromProgram // when it was created from (not all fields)
    
    Date paymentDate
    PaymentState paymentState
    Integer minEffort
    Integer maxEffort
    AcademicSession perPeriod      // effort per week or semester
    Float totalFee
    GradeType gradeType = GradeType.LETTER
    Float gpa               // 0 to 5.0 (A is 4.0)
    Float percentage    

    State state 
    Date startDate
    Date endDate
    
    
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated


    
    String toString(){

        "$person $program "
    }
  
    static constraints = {
    
        name(nullable:false)
        
        // this is created by a program 
        person (editable:false, nullable:true)
        program (editable:false, nullable:true)
        clonedFromProgram (editable:false, nullable:true)

         paymentDate (nullable:true)
     paymentState (nullable:true)
     minEffort(nullable:true)
     maxEffort(nullable:true)
     perPeriod(nullable:true)      // effort per week or semester
     totalFee(nullable:true)
     gradeType(nullable:true)
     gpa(nullable:true)
     percentage(nullable:true)
     
        state (nullable:true)
        startDate (nullable:true)
        endDate (nullable:true)
    
    
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
        def studentProgramService = ctx.studentProgramService
     
        return studentProgramService.list()
    }
}