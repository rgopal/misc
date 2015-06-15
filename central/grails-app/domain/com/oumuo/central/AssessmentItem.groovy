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
   
    Assessment assessment //this is the root
    
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
        //  sequence.compareTo(obj.sequence)
        return compareSequence(sequence, obj.sequence)
    }
    static int compareSequence(String s1, String s2) {
      
        def s1First, s2First, s1Remaining, s2Remaining
        // now find the first (before .) and remaing parts
        s1Remaining = s1.substring(s1.indexOf('.')+1)
       
        if (s1.indexOf('.') > -1 )
        s1First = s1.substring(0,s1.indexOf('.'))
        else
        s1First = ""
        
        log.trace "str 1 $s1 $s1First $s1Remaining"
        
        // def s2Remaining = s2.split('.')[1..-1].join('.')
        s2Remaining = s2.substring(s2.indexOf('.')+1)
        if (s2.indexOf('.') > -1)
        s2First = s2.substring(0,s2.indexOf('.'))
        else
        s2First = ""
        
        log.trace "str 2 $s2 $s2First $s2Remaining"
        
        // First becomes empty if there is no .
          if (s1First.length() == 0 && s2First.length() == 0)
        return 0
        else if (s1First.length() == 0)
        return -1
        else if (s2First.length() == 0)
        return 1
          
        
        if (s1First.toInteger() > s2First.toInteger())
        return 1
        else if (s1First.toInteger () < s2First.toInteger()) 
        return -1
        else return compareSequence(s1Remaining, s2Remaining)
        
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
        questionContents(nullable:true, editable:true)
        answerContents(nullable:true, editable:true) 
        // in future make all other editable false as well
        
        assessment(nullable:true)   // this the root but null for children
       
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
        lastUpdated(nullable:true)
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