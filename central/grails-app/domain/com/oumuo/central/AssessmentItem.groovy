package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j

// AssessmentItem includes a tree of assessmentItem nodes (recursive) each referring to a
// a Learning.  A root assessmentItem can be recursively traversed from Course.
// Each Learning of a complete assessmentItem are also listed in a program as a sorted
// list under the property allLearnings (so each assessmentItem node has a reference to
// that program.

class AssessmentItem implements Comparable {
   
    // each level is sorted
    SortedSet subAssessmentItems
    static hasMany = [ subAssessmentItems: AssessmentItem,
        questionContents: Content,
        answerContents:Content] 
    static mappedBy = [ subAssessmentItems: 'parentAssessmentItem',
            questionContents:'assessmentItemQuestion',
            answerContents:'assessmentItemAnswer']
   
    Assessment assessment //also section
  
    // parent could be null
    AssessmentItem parentAssessmentItem
    
    String name
    String sequence
   
    //static belongsTo = [ program: Program ]
    static transients = ['allSubAssessmentItems']
     
    AcademicLevel academicLevel
    AcademicStratum academicStratum
    Float successRate
    QuestionType questionType
    Integer difficulty = 500
              // help material to describe answer
    Integer maxAttempts = 1
    Float pointsReductionPercent = 10.0f
    String answer
    Float points
    Integer duration
    DurationUnit durationUnit
    Integer effort
    DurationUnit effortUnit
    
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    // TODO  parse sequence and perform recursive numeric sort on each field 
    int compareTo(obj) {
        sequence.compareTo(obj.sequence)
    }
  
    def getAllSubAssessmentItems() {
        return subAssessmentItems ? subAssessmentItems*.allSubAssessmentItems.flatten() + subAssessmentItems : []
    }
    
    String toString(){

        sequence + " " + name?.substring(0,Math.min(15, name? name.length():0))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:true, editable:false)
        name(nullable:false)
             
        // this allows the user to make parentAssessmentItem null (and thus a new root)
        parentAssessmentItem (nullable:true, editable:false)
     questionContents(nullable:true)
        answerContents(nullable:true) 
        // in future make all other editable false as well
        
        assessment()
       
        subAssessmentItems()

        academicLevel (nullable:true)
        academicStratum(nullable:true)
        successRate(nullable:true)
        questionType(nullable:true)
        difficulty(nullable:true)
                  // help material to describe answer
        answer(nullable:true)
        points(nullable:true)
        maxAttempts(nullable:true)
        pointsReductionPercent(nullable:true)
        duration(nullable:true)
        durationUnit(nullable:true)
        effort(nullable:true)
        effortUnit(nullable:true)

        status()
        dateCreated()
        lastUpdated()
    }

    // beforeInsert is gone (included in AssessmentItemService)
    
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new AssessmentItem ().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def assessmentItemService = ctx.assessmentItemService
     
        return assessmentItemService.list()
    }
}