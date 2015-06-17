/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central




import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils


import org.springframework.security.core.context.SecurityContextHolder as SCH


import com.oumuo.lookup.*
import com.oumuo.lookup.UserRole as ROLE
import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitAssessment {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def assessments = [ 
            new Assessment(
                name: 'Introduction Test',
                assessmentType: AssessmentType.TEST,
             totalPoints: 100.0f,
                organization: Organization.findByName('Montgomery County Community College')
                ),
                 new Assessment(
                name: 'Data Structures Test',
              assessmentType: AssessmentType.TEST,
              totalPoints: 100.0f,
                organization: Organization.findByName('Montgomery County Community College')
                ),
                  new Assessment(
                name: 'Trees Quiz',
                totalPoints: 100.0f,
              assessmentType: AssessmentType.QUIZ,
            
                organization: Organization.findByName('Montgomery County Community College')
                )
               
        ]
        
       
        // save all the assessmentss and create ACLs
        for (assessment in assessments) {     
            log.trace "processing  assessment ${assessment} "
       
            if (!assessment.save(flush:true)) { 
                assessment.errors.allErrors.each {error ->
                    log.warn "An error occured with ${assessment} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(assessment, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created Assessment ${assessment}"
        }
    
    
    log.info ("load: loaded ${Assessment.count()} out of ${assessments.size()} assessments")
              
    }
}
    