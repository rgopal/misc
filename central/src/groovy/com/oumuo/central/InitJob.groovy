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
class InitJob {
  
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def jobs = [ 
            new Job(
                name: 'Programming',
                year : 2015,
                source: 'Industry Alliance',
                averageSalary: 60000.0f,
                jobType: JobType.FULL_TIME,
                regionType: RegionType.URBAN,
                positionType: PositionType.PROGRAMMER,
                medianSalary: 55000.0f
            ).addToRankings (
                new Ranking (
                    sequence: 1,            // Has to be here, beforeInsert can't deal automated
                    name:'July 2015 Ranking',
                    person:cronRanking
                )
            ),
             new Job(
                name: 'Architect',
                year : 2016,
                source: 'Industry Alliance',
                averageSalary: 60000.0f,
                jobType: JobType.PART_TIME,
                regionType: RegionType.RURAL,
                positionType: PositionType.ARCHITECT,
                medianSalary: 50000.0f
            ).addToRankings (
                new Ranking (
                    sequence: 1,            // Has to be here, beforeInsert can't deal automated
                    name:'June 2015 Ranking',
                    person:cronRanking
                )
            )
      
         
        ]
        
       
        for (job in jobs) {
          
        
            log.trace "processing  job ${job} "
       
            // call flush (ACL may give transient object not saved error)
            if (!job.save(flush:true)) { 
                job.errors.allErrors.each {error ->
                    log.debug "An error occured with ${job} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(job, user)
      
           
                    for (ranking in job.rankings) {
                        InitSpringSecurity.grantACL (ranking, user)
                    }
                }
      
                log.info "  loaded ${Job.findById(job.id).rankings?.size()} ranking"     
                log.debug "created Job ${job}"
            }
        }
    
        log.info ("load: loaded ${Job.count()} out of ${jobs.size()} jobs")
       
        
    }
    

}