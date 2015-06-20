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

        def organizations = [
            'Montgomery County Community College',
            'Montgomery County Community College',
        'Montgomery County Community College'
        ]
        def assessments = [ 
            new Assessment(
                name: 'Introduction Test',
                assessmentType: AssessmentType.TEST,
                totalPoints: 100.0f
       
            ),
            new Assessment(
                name: 'Data Structures Test',
                assessmentType: AssessmentType.TEST,
                totalPoints: 100.0f
    
            ),
            new Assessment(
                name: 'Trees Quiz',
                totalPoints: 100.0f,
                assessmentType: AssessmentType.QUIZ
            
            
            )
               
        ]
        
       
        // save all the assessmentss and create ACLs through parent
        // or else won't find in the corresponding O2M association
        
        def i = 0
        for (assessment in assessments) {    
            log.trace "processing  assessment ${assessment} "
            
            def organization  = Organization.findByName(organizations[i])
            if (!organization) {
                log.warn "load: could not find organization $organizations[i] for i = i"
                return 
            } else 
            organization.addToAssessments(assessment)
            i++
       
            if (!organization.save(flush:true)) { 
                organization.errors.allErrors.each {error ->
                    log.warn "An error occured with ${organization} $error"
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
    