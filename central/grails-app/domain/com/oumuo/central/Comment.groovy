package com.oumuo.central
import com.oumuo.lookup.*
import groovy.util.logging.Log4j

@Log4j
class Comment {

    String comment
    String sequence
    String detailedComment;
    ItemType commentType = ItemType.TEXT
  
    // there will be multiple MtoM associations and Person is not null

    Organization organization
  
    // parent could be null
    Comment parentComment
    static hasMany = [ subComments: Comment ]    
    static belongsTo = [ person: Person ]
    static transients = ['allSubComments']
   
    
    // these are common to all; state is managed by system
    Status status = Status.ACTIVE
    Date dateCreated
    Date lastUpdated

    def getAllSubComments() {
        return subCommentsfb ? subComments*.allSubComments.flatten() + subComments : []
    }
    
    String toString(){

    sequence + ". " + comment?.substring(0,Math.min(15, comment? comment.length():0))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        
        // this allows the user to make parentComment null (and thus a new root)
        parentComment (nullable:true, editable:false)
        person(editable:false)
        // in future make all other editable false as well
        comment (nullable:true)
        detailedComment (nullable:true)
        commentType ()   
        subComments()

        // this will become editable:false (same for other)
        organization (editable:true, nullable: true)
 
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
            sequence = "1"
        
            log.trace "beforeInsert: sequence updated to $sequence"
               
        }     
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