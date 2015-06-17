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
class InitLearningAssessment {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def learningAssessments = [ 
            new LearningAssessment(
                learning: Learning.findByName('Introduction'),
                assessment: Assessment.findByName('Introduction Test'),
                timeRelation: TimeRelation.BEFORE,
                
                passingGrade: Grade.C,
                program: Program.findByName('Computer Science Diploma')
            ),
              new LearningAssessment(
                learning: Learning.findByName('Data Structures'),
                assessment: Assessment.findByName('Data Structures Test'),
                timeRelation: TimeRelation.BEFORE,
                passingGrade: Grade.D,
                program: Program.findByName('Computer Science Diploma')
            ),
             new LearningAssessment(
                learning: Learning.findByName('Trees'),
                assessment: Assessment.findByName('Trees Quiz'),
                timeRelation: TimeRelation.BEFORE,
                passingGrade: Grade.B,
                program: Program.findByName('Computer Science Diploma')
            )
               
        ]
        
       
        // save all the learningAssessmentss and create ACLs
        for (learningAssessment in learningAssessments) {     
            log.trace "processing  learningAssessment ${learningAssessment} "
       
            if (!learningAssessment.save(flush:true)) { 
                learningAssessment.errors.allErrors.each {error ->
                    log.warn "An error occured with ${learningAssessment} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(learningAssessment, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created LearningAssessment ${learningAssessment}"
        }
    log.info ("load: loaded ${LearningAssessment.count()} out of ${learningAssessments.size()} learningAssessments")
    
    }
              
}
    