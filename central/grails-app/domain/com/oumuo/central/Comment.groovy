package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Comment {
  
    Organization organization
    Course course
    Assessment assessment
    Learning learning
    PersonInstruction personInstruction
    PersonAssessment personAssessment
  
    // parent could be null
    Comment parentComment
    static hasMany = [ subComments: Comment ]    
    static belongsTo = [ person: Person ]
    static transients = ['allSubComments']
   
    String comment
    String sequence
    String detailedComment;
    ItemType commentType = ItemType.TEXT
  
    // there will be multiple MtoM associations and Person is not null
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    def getAllSubComments() {
        return subCommentsfb ? subComments*.allSubComments.flatten() + subComments : []
    }
    
    String toString(){

        sequence + " " + comment?.substring(0,Math.min(15, comment? comment.length():0)) +
         "${organization ? 'ORG':' '} ${course ? 'COURSE':' '} ${personInstruction ? 'PINST':' '}" +
        "${assessment ? 'ASSESM':' '} " +"${assessment ? 'LEARN':' '} "
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        
        // this allows the user to make parentComment null (and thus a new root)
        person(editable:false)
        personInstruction(editable:false, nullable:true)
        personAssessment(editable:false, nullable:true)
        parentComment (nullable:true, editable:false)
        // this will become editable:false (same for other)
        organization (editable:false, nullable: true)
        course (editable:false, nullable: true)
        assessment (editable:false, nullable: true)
         learning (editable:false, nullable: true)
        subComments()

        // in future make all other editable false as well
        comment (nullable:true)
        detailedComment (nullable:true)
        commentType ()   
   
        status()
        dateCreated()
        lastUpdated()
    }

    
    def beforeInsert() {
        if (!sequence) {
                 
            if (parentComment) 
            sequence = parentComment.sequence + "." + 
            (parentComment.subComments.size() + 1).toString() 
            else
            sequence = selectSequence()
        
            log.trace "beforeInsert: sequence updated to $sequence"
               
        }     
    }
    
    String selectSequence() {
        if (organization)
        return Comment.countByPersonAndOrganization(person,organization) + 1
        else if (course)
        return Comment.countByPersonAndCourse(person,course) + 1
        else if (assessment)
        return Comment.countByPersonAndAssessment(person,assessment) + 1
        else if (learning)
        return Comment.countByPersonAndLearning(person,learning) + 1
        else if (personInstruction)
        return Comment.countByPersonAndPersonInstruction(person,personInstruction) + 1
        else if (personAssessment)
        return Comment.countByPersonAndPersonAssessment(person,personAssessment) + 1
    }
    
 
    // for classes with 1toM relation, may need to control the many side in
    // the popup list.  Used in renderTemplate edit
    static secureList() {
        def grailsApplication = new Account().domainClass.grailsApplication
        def ctx = grailsApplication.mainContext
        def config = grailsApplication.config
        def commentService = ctx.commentService
     
        return commentService.list()
    }
}