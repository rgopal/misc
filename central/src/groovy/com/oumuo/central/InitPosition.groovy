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
class InitPosition {
  
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')
        
        def jobs = [
            'Programming',
            'Architect'
        ]

        def positions = [ 
            new Position(
                name: "Software Engineer",
                source: 'Washington Post',
                State: 'Maryland',
                country: Country.USA,
                salary: 50000.0f,
              
                startDate: new Date().parse("d/M/yyyy","28/09/2015")
            ),
            new Position(
               
                name: "Chief Architect",
                source: 'LinkedIn',
                State: 'Maryland',
                salary: 60000.0f,
                country: Country.USA,
           
                startDate: new Date().parse("d/M/yyyy","28/09/2015")
            )
                
                
        ]
        
        def i = 0
        for (position in positions) {
            def job = Job.findByName(jobs[i])
            if (!job) {
                log.warn "load: could not find job job[i]"
                return
            }     
            i++
            job.addToPositions(position)
        
            log.trace "processing  position ${position} "
       
            // call flush (ACL may give transient object not saved error)
            if (!job.save(flush:true)) { 
                job.errors.allErrors.each {error ->
                    log.debug "An error occured with ${job} $error"
                }
            } else {     
                // give permissions to two users (managers)
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(position, user)
      
                }
      
                 
                log.debug "created Position ${position}"
            }
        }
    
        log.info ("load: loaded ${Position.count()} out of ${positions.size()} positions")
       
        
    }
    

}