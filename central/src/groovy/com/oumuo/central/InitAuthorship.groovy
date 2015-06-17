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
class InitAuthorship {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def authorships = [ 
            new Authorship(
                person:Person.findByUserName('jfields'),
                learning: Learning.findByName('Introduction'),
             
                role: Role.PRIMARY
              
            ),
             new Authorship(
                person:Person.findByUserName('jfields'),
                course: Course.findByName('Computer Science I'),
             
                role: Role.JOINT
              
            ),
              new Authorship(
              person:Person.findByUserName('jfields'),
                assessment: Assessment.findByName('Data Structures Test'),
                role: Role.SECONDARY
             
              
            ),
             new Authorship(
               person:Person.findByUserName('jfields'),
                role: Role.PRIMARY,
                program: Program.findByName('Computer Science Diploma')
            )
               
        ]
        
       
        // save all the authorshipss and create ACLs
        for (authorship in authorships) {     
            log.trace "processing  authorship ${authorship} "
       
            if (!authorship.save(flush:true)) { 
                authorship.errors.allErrors.each {error ->
                    log.warn "An error occured with ${authorship} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(authorship, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created Authorship ${authorship}"
        }
    log.info ("load: loaded ${Authorship.count()} out of ${authorships.size()} authorships")
}
    
    
              
}
    