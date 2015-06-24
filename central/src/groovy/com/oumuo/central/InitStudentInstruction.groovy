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
class InitStudentInstruction {
    
    // def studentInstructionFactory
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
        
        def instruction = ClassSession.findByName('Computer Science I - Fall 2015 Class Session')?.
            instructions[0]
        
        if (!instruction)
            log.warn "load: instruction not found for classSession Computer Science I - Fall 2015 Class Session"
        
        def person = Person.findByUserName ('jsmith')
        
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new  ${studentInstruction}:${error} for jsmith")
            }
        } 
        
        def studentInstructions = [ 
            new StudentInstruction(
                sequence:'1',
                state :State.STARTED
                
            )
            
        ]
        
        
       
        // save all the studentInstructionss and create ACLs
   
        for (studentInstruction in studentInstructions) {     
           log.trace "processing  studentInstruction ${studentInstruction} "
            
            person.addToStudentInstructions(studentInstruction)
            
         
       
            if (!person.save(flush:true)) { 
                person.errors.allErrors.each {error ->
                    log.warn "An error occured with ${person} $error"
                }
            }
         
            
            // now add it to instruction
            
            instruction.addToStudentInstructions(studentInstruction)
            
                if (!instruction.save(flush:true)) { 
                instruction.errors.allErrors.each {error ->
                    log.warn "An error occured with ${instruction} $error"
                }
            }
            
            else {     
                // give permissions to two users
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(studentInstruction, user)
              
                }
             
            }
            log.debug "created StudentInstruction ${studentInstruction}"
        }
    
    
        log.info ("load: loaded ${StudentInstruction.count()} out of ${studentInstructions.size()} studentInstructions")
       
        
    }
    

}