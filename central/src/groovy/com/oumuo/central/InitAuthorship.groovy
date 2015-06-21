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
        
        // use author as the parent, even though there are two.
        // hopefully once they are stored in the database, new
        // sessions would be fine with other parents
        
        def persons = [
            'jfields',
            'jfields',
            'jfields',
            'jfields'
        ]

        def authorships = [ 
            new Authorship(
               
                learning: Learning.findByName('Introduction'),
             
                role: Role.PRIMARY
              
            ),
             new Authorship(
                
                course: Course.findByName('Computer Science I'),
             
                role: Role.JOINT
              
            ),
              new Authorship(
             
                assessment: Assessment.findByName('Data Structures Test'),
                role: Role.SECONDARY
             
              
            ),
             new Authorship(
             
                role: Role.PRIMARY,
                program: Program.findByName('Computer Science Diploma')
            )
               
        ]
        
       
        // save all the authorshipss and create ACLs
         def i = 0
        for (authorship in authorships) {     
                    
            def person = Person.findByUserName(persons[i])
            if (!person) {
                log.warn "load: could not find person $persons[i]"
                return
            }     
            i++
            person.addToAuthorships(authorship)
            
            log.trace "processing  authorship ${authorship} "
       
            if (!person.save(flush:true)) { 
                person.errors.allErrors.each {error ->
                    log.warn "An error occured with ${person} $error"
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
    