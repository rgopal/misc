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
class InitJobAvailability {
  
   
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

        def jobAvailabilitys = [ 
            new JobAvailability(
                year : 2015,
                source: 'BLOS',
                State: 'Maryland',
                country: Country.USA,
                dataType: DataType.MEASURED,
                dataDate: new Date().parse("d/M/yyyy","28/09/2015")
            ),
            new JobAvailability(
                year : 2016,
                source: 'BLOS',
                State: 'Maryland',
                
                country: Country.USA,
                dataType: DataType.ESTIMATED,
                dataDate: new Date().parse("d/M/yyyy","28/09/2015")
            )
                
                
        ]
        
        def i = 0
        for (jobAvailability in jobAvailabilitys) {
            def job = Job.findByName(jobs[i])
            if (!job) {
                log.warn "load: could not find job job[i]"
                return
            }     
            i++
            job.addToJobAvailabilitys(jobAvailability)
        
            log.trace "processing  jobAvailability ${jobAvailability} "
       
            // call flush (ACL may give transient object not saved error)
            if (!job.save(flush:true)) { 
                job.errors.allErrors.each {error ->
                    log.debug "An error occured with ${job} $error"
                }
            } else {     
                // give permissions to two users (managers)
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(jobAvailability, user)
      
                }
      
                 
                log.debug "created JobAvailability ${jobAvailability}"
            }
        }
    
        log.info ("load: loaded ${JobAvailability.count()} out of ${jobAvailabilitys.size()} jobAvailabilitys")
       
        
    }
    

}