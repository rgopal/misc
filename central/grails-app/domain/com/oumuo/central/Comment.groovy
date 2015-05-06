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
   
    
    State state = State.CURRENT
    Date dateCreated = new Date()
    Date lastUpdated

    def getAllSubComments() {
        return subComments ? subComments*.allSubComments.flatten() + subComments : []
    }
    
    String toString(){

    sequence + ". " + comment.substring(0,Math.min(15, comment.length()))
    }
  
    static constraints = {
        // named association so not needed owner()
        sequence (nullable:true, display:false)
        parentComment (nullable:true)
        comment (nullable:true)
        detailedComment (nullable:true)
        commentType ()   
        subComments()

        organization (editable:true, nullable: true)
 
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