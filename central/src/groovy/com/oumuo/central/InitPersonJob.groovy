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
class InitPersonJob {
    
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
        
    

        def personJobs = [ 
            new PersonJob(
               
                person: Person.findByUserName('jsmith'),
                job: Job.findByName('Programming'),
                choiceLevel: 500,
                matchLevel: 500,
                role: Role.PRIMARY
              
            ),
            new PersonJob(
               
                person: Person.findByUserName('jsmith'),
                job: Job.findByName('Architect'),
                choiceLevel: 500,
                matchLevel: 500,
                role: Role.SECONDARY
              
            )
               
        ]
        
       
        // save all the personJobss and create ACLs

        for (personJob in personJobs) {     
                    
      

            
            log.trace "processing  personJob ${personJob} "
       
            if (!personJob.save(flush:true)) { 
                personJob.errors.allErrors.each {error ->
                    log.warn "An error occured with ${personJob} $error"
                }
            } else {     
                // give permissions to the actual user
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(personJob, user)
                                    
                }
                
            }
             
            log.debug "created PersonJob ${personJob}"
        }
        log.info ("load: loaded ${PersonJob.count()} out of ${personJobs.size()} personJobs")
    }
    
    
              
}
    