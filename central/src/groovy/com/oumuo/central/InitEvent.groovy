/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central


import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils


import org.springframework.security.core.context.SecurityContextHolder as SCH

import com.oumuo.central.Organization
import com.oumuo.central.Staffing
import com.oumuo.lookup.*
import com.oumuo.lookup.UserRole as ROLE


import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitEvent {
    
    // def sessionFactory
    // def springSecurityService
    def aclService  
    def aclUtilService
    def objectIdentityRetrievalStrategy
    
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')
 
    
        def events = [ 
              new Event(
                name: 'Class Event 1',
               
                    isRecurring : true,
                    recurType : Recurring.MWF,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                  new Event(
                name: 'Class Event 2',
               
                    isRecurring : true,
                    recurType : Recurring.TTH,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
               
             new Event(
                name: 'Class Event 3',
               
                    isRecurring : false,
                    
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                 new Event(
                name: 'Class Event 4',
               
                    isRecurring : true,
                    recurType : Recurring.DAILY,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                 new Event(
                name: 'Class Event 5',
               
                    isRecurring : true,
                    recurType : Recurring.WEEKEND,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
               
               
            new Event(
                name: 'Class Session Event 1',
               
                    isRecurring : true,
                    recurType : Recurring.MWF,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                  new Event(
                name: 'Class Session Event 2',
               
                    isRecurring : true,
                    recurType : Recurring.TTH,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
               
             new Event(
                name: 'Class Session Event 3',
               
                    isRecurring : false,
                    
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                 new Event(
                name: 'Class Session Event 4',
               
                    isRecurring : true,
                    recurType : Recurring.DAILY,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                ),
                 new Event(
                name: 'Class Session Event 5',
               
                    isRecurring : true,
                    recurType : Recurring.WEEKEND,
               
                    startTime: new Date().parse("MM-dd-yyyy", '09-01-2015'),
                    endTime: new Date().parse("MM-dd-yyyy", '12-31-2015')
                )
               
               
        
        ]
        
     
        // save all the eventss and create ACLs
        for (event in events) {    
         
            
            log.trace "processing  event ${event} "
       
            if (!event.save(flush:true)) { 
                event.errors.allErrors.each {error ->
                    log.warn "An error occured with ${event} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(event, user)
            
            
                }
            }
            log.debug "created Event ${event}"
        }
    
    
        log.info ("load: loaded ${Event.count()} out of ${events.size()} events")
       
        
    }
    

}