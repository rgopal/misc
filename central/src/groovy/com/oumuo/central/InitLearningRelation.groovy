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
class InitLearningRelation {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def programs = [
            'Computer Science Diploma'
        ]
        def learningRelations = [ 
            new LearningRelation(
                firstLearning: Learning.findByName('Introduction'),
                secondLearning: Learning.findByName('Data Structures'),
                relation: Relation.PRE_REQ
              
                )
               
        ]
        
       def i = 0
        // save all the learningRelationss and create ACLs
        for (learningRelation in learningRelations) {     
            
             def program  = Program.findByName(programs[i])
            if (!program) {
                log.warn "load: could not find program $programs[i] for i = $i"
                return 
            } else 
            program.addToLearningRelations(learningRelation)
            i++
            log.trace "processing  learningRelation ${learningRelation} "
       
            if (!program.save(flush:true)) { 
                program.errors.allErrors.each {error ->
                    log.warn "An error occured with ${program} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(learningRelation, user)   
              
                }
                
            }
             
            log.debug "created LearningRelation ${learningRelation}"
        }
    log.info ("load: loaded ${LearningRelation.count()} out of ${learningRelations.size()} learningRelations")
    
    }
              
}
    