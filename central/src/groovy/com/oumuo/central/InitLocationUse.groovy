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
class InitLocationUse {
    
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
 
        // each location is like a person and can be booked
        // because of a class or class session.   A person
        // would have similar binding for teaching or learning
        // probably at classSession level
        
        def locations = [
            'Rockville Campus',
            'QO Campus',
            'QO Valley Campus',
            'ONLINE'
        ]
        def locationUses = [ 
            
            new LocationUse(
                event: Event.findByName('Location Use Event 1'),
                state: State.PLANNED
               ),
               new LocationUse(
                event: Event.findByName('Location Use Event 2'),
                state: State.PLANNED
               ),
               new LocationUse(
                event: Event.findByName('Location Use Event 3'),
                state: State.PLANNED
          
       
            ),
        ]
        
       def i = 0
       
        // save all the locationUsess and create ACLs
        for (locationUse in locationUses) {    
            def location  = Location.findByName(locations[i])
            if (!location) {
                log.warn "load: could not find location $locations[i] for i = $i"
                return 
            } else 
            location.addToLocationUses(locationUse)
            i++
            
            log.trace "processing  locationUse ${locationUse} "
       
            if (!location.save(flush:true)) { 
                location.errors.allErrors.each {error ->
                    log.warn "An error occured with ${location} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(locationUse, user)
            
            
                }
            }
            log.debug "created LocationUse ${locationUse}"
        }
    
    
        log.info ("load: loaded ${LocationUse.count()} out of ${locationUses.size()} locationUses")
       
        
    }
    

}