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
class InitAssessmentItem {
    

   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def assessments = [
            'Introduction Test',
            'Introduction Test'
        ]

        def assessmentItems = [ 
            new AssessmentItem(name: 'Introduction Test Item 1',
                sequence: "1",
                effort: 10,
                effortUnit: DurationUnit.MINUTES
             
                
                
                
            ).addToSubAssessmentItems(
                new AssessmentItem(name: 'Introduction Test Item 1.a',
                    sequence: "1.1",
                    effort: 10,
                effortUnit: DurationUnit.MINUTES
                      
                )
            ),
          
          
        ]
        
       
        def i = 0
        // save all the assessmentItems and create ACLs
        for (assessmentItem in assessmentItems) {  
            
            def assessment = Assessment.findByName(assessments[i])
            if (!assessment) {
                log.warn "load: could not find assessment assessment[i]"
                return
            }     
            i++
            assessment.addToAssessmentItems(assessmentItem)
            log.trace "processing  assessmentItem ${assessmentItem} "
       
            log.trace assessmentItem.properties.collect{it}.join('\n')
            
            if (!assessment.save(flush:true)) { 
                assessment.errors.allErrors.each {error ->
                    log.warn "An error occured with ${assessment} $error"
                }
            } else {     
                // give permissions to one user
                for (user in ['jfields']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(assessmentItem, user)
                }
             
                for (subAssessmentItem in assessmentItem.subAssessmentItems) {
                    if (!subAssessmentItem.save(flush:true)) { 
                        subAssessmentItem.errors.allErrors.each {error ->
                            log.warn "An error occured with ${subAssessmentItem} $error"
                        }
                    } else {     
                        // give permissions to two users
                        for (user in ['jfields', 'mjohns']) {
                            log.trace "   starting ACL creations for $user"
                            InitSpringSecurity.grantACL(subAssessmentItem, user)
                    
                        } 
                    }
                }
                log.debug "created AssessmentItem ${assessmentItem}"
            }
        }
    
        log.info ("load: loaded ${AssessmentItem.count()} out of ${assessmentItems.size()} assessmentItems")
       
        
    }
    

}