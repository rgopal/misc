/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central


import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils


import org.springframework.security.core.context.SecurityContextHolder as SCH

import com.oumuo.central.Organization
import com.oumuo.central.Staffing
import com.oumuo.lookup.*
import com.oumuo.lookup.UserRole as ROLE 


import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitPersonAssessmentItem {
    
    // def personAssessmentItemFactory
    // def springSecurityService
    def aclService  
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')
      
        // find the instruction which was loaded earlier
    
        def person = Person.findByUserName ('jsmith')
         
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new  ${personAssessmentItem}:${error} for jsmith")
            }
        } 
        
            
        def personInstruction = person.personInstructions[0]
        
        if (!personInstruction)
            log.warn "load: first instruction not found for person $person"
        
        def personAssessment = personInstruction.personAssessments[0]
        
         if (!personAssessment)
            log.warn "load: first personAssessment in $personInstruction not found for person $person"
            
        def personAssessmentItems = [ 
            new PersonAssessmentItem(
                sequence:'1',
                state :State.STARTED
                
            )
            
        ]
        
       
        // save all the personAssessmentItemss and create ACLs
   
        for (personAssessmentItem in personAssessmentItems) {     
           log.trace "processing  personAssessmentItem ${personAssessmentItem} "
            
            personAssessment.addToPersonAssessmentItems(personAssessmentItem)           
       
            if (!personAssessment.save(flush:true)) { 
                personAssessment.errors.allErrors.each {error ->
                    log.warn "An error occured with ${personAssessment} $error"
                }
            }
         
            
            // now add it to assessment
            
            def assessmentItem = AssessmentItem.findByName('Introduction Test Item 1')
            if (!assessmentItem) 
            log.warn "load: could not find assessmentItem named - Introduction Test Item 1"
            
            assessmentItem.addToPersonAssessmentItems(personAssessmentItem)
            
                if (!assessmentItem.save(flush:true)) { 
                assessmentItem.errors.allErrors.each {error ->
                    log.warn "An error occured with ${assessmentItem} $error"
                    }
                }
            
            else { 
                // now add an instruction (after setting its peron)
                 personAssessmentItem.addToComments(
            new Comment (
                comment: 'this is a good instruction for personAssessment',
                person: person
                )
            )
            if (!personAssessmentItem.save(flush:true))
            log.warn "could not save personAssessmentItem $personAssessmentItem after adding comment"
            
                // give permissions to two users
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(personAssessmentItem, user)
              
                }
             
                for (comment in personAssessmentItem.comments) {
                    for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(comment, user)
              
                    }
                log.trace "created comment $comment for $personAssessmentItem"
                }
                log.info "created ${personAssessmentItem.comments?.size()} comments "
            }
            log.debug "created PersonAssessmentItem ${personAssessmentItem}"
        }
    
    
        log.info ("load: loaded ${PersonAssessmentItem.count()} out of ${personAssessmentItems.size()} personAssessmentItems")
       
        
    }
    

}