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
class InitPersonContentAssessmentItem {
    
    // def personContentFactory
    // def springSecurityService
    def aclService  
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
   
    def contents = 
    [
        'Introduction Video',
        'Data Structures',
        'Trees Text'
    ]
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')
      
        // find the instruction which was loaded earlier
        
        def personInstruction = ClassSession.findByName('Computer Science I - Fall 2015 Class Session')?.
            instructions[0].personInstructions[0]
        
        if (!personInstruction)
            log.warn "load: personInstruction not found for classSession Computer Science I - Fall 2015 Class Session"
            
        def personAssessmentItem = personInstruction.personAssessments[0].personAssessmentItems[0]
        
          if (!personAssessmentItem)
            log.warn "load: personAssessmentItem not found for classSession Computer Science I - Fall 2015 Class Session"
        
        def person = Person.findByUserName ('jsmith')
        
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new  ${personContent}:${error} for jsmith")
            }
        } 
        
        
        def personContents = [ 
            new PersonContent(
                
                state :State.STARTED,
                startTime: new Date().parse("d/M/yyyy H:m:s","28/09/2014 16:00:43")
                
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","28/09/2015 16:02:43")
            ).addToRestartTimes (          
                    new Date().parse("d/M/yyyy H:m:s","28/09/2015 16:04:44")
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","28/09/2015 16:07:45")
            ),
            new PersonContent(
                
                state :State.COMPLETED,
                startTime: new Date().parse("d/M/yyyy H:m:s","18/09/2015 16:00:43"),
                endTime:new Date().parse("d/M/yyyy H:m:s","18/09/2015 16:10:45")
                
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","18/09/2015 16:02:43")
            ).addToRestartTimes (          
                    new Date().parse("d/M/yyyy H:m:s","18/09/2015 16:04:44")
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","18/09/2015 16:07:45")
            ),
            new PersonContent(
                
                state :State.COMPLETED,
                startTime: new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:00:43"),
                endTime:new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:10:45")
                
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:02:43")
            ).addToRestartTimes (          
                    new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:04:44")
            ).addToPauseTimes (          
                    new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:07:45")
            ).addToRestartTimes (          
                    new Date().parse("d/M/yyyy H:m:s","08/09/2015 16:08:45")
            
            
            )     
        ]
        
        
       
        // save all the personContentss and create ACLs
   
        def i = 0
        for (personContent in personContents) {     
           log.trace "processing  personContent ${personContent} for personAssessmentItem"
            
            personAssessmentItem.addToPersonContents(personContent)
            
       
            if (!personAssessmentItem.save(flush:true)) { 
                personAssessmentItem.errors.allErrors.each {error ->
                    log.warn "An error occured with ${personAssessmentItem} $error"
                }
            }
         
            
            // now add it to content
    
            def content = Content.findByName (contents[i])
        
            
            i++
            content.addToPersonContents(personContent)
            
                if (!content.save(flush:true)) { 
                content.errors.allErrors.each {error ->
                    log.warn "An error occured with ${content} $error"
                }
            }
            
            else { 
               
                // give permissions to actual user
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(personContent, user)
              
                }
             
            }
            log.info "created PersonContent ${personContent} with durationsSeconds " +
               "${personContent.getDurationSeconds()}"
        }
    
    
        log.info ("load: loaded ${PersonContent.countByPersonAssessmentItem(personAssessmentItem)} " +
                " out of ${personContents.size()} personContents for personAssessmentItem")
       
        
    }
    

}