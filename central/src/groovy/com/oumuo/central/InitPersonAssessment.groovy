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
class InitPersonAssessment {
    
    // def personAssessmentFactory
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
                log.warning ("create: error while getting new  ${personAssessment}:${error} for jsmith")
            }
        } 
        
            
        def personInstruction = person.personInstructions[0]
        
        if (!personInstruction)
            log.warn "load: first instruction not found for person $person"
        
        
        def personAssessments = [ 
            new PersonAssessment(
                sequence:'1',
                state :State.STARTED
                
            )
            
        ]
        
       
        // save all the personAssessmentss and create ACLs
   
        for (personAssessment in personAssessments) {     
           log.trace "processing  personAssessment ${personAssessment} "
            
            personInstruction.addToPersonAssessments(personAssessment)           
       
            if (!personInstruction.save(flush:true)) { 
                personInstruction.errors.allErrors.each {error ->
                    log.warn "An error occured with ${personInstruction} $error"
                }
            }
         
            
            // now add it to assessment
            
            def assessment = Assessment.findByName('Introduction Test')
            if (!assessment) 
            log.warn "load: could not find assessment"
            
            assessment.addToPersonAssessments(personAssessment)
            
                if (!assessment.save(flush:true)) { 
                assessment.errors.allErrors.each {error ->
                    log.warn "An error occured with ${assessment} $error"
                    }
                }
            
            else { 
                // now add an instruction (after setting its peron)
                 personAssessment.addToComments(
            new Comment (
                comment: 'this is a good instruction for me',
                person: person
                )
            )
            if (!personAssessment.save(flush:true))
            log.warn "could not save personAssessment $personAssessment after adding comment"
            
                // give permissions to two users
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(personAssessment, user)
              
                }
             
                for (comment in personAssessment.comments) {
                    for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(comment, user)
              
                    }
                log.trace "created comment $comment for $personAssessment"
                }
                log.info "created ${personAssessment.comments?.size()} comments "
            }
            log.debug "created PersonAssessment ${personAssessment}"
        }
    
    
        log.info ("load: loaded ${PersonAssessment.count()} out of ${personAssessments.size()} personAssessments")
       
        
    }
    

}