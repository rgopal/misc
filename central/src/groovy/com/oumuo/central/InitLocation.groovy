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
class InitLocation {
    
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
 
        def organizations = [
            'Montgomery County Community College',
            'Quince Orchard High School',
            'RidgeView Middle School',
            'लखनऊ विश्वविद्यालय'
        ]
        def locations = [ 
            new Location(
                name: 'Rockville Campus',
                locationType: LocationType.CAMPUS,
                state: State.IN_USE,
                capacity:50,
                startHour:6,
                startMinute:0,
                building:'Science',
                floor: 'II',
                room: '2-234',
                endHour:23,
                endMinute:30
               
            ),
       new Location(
                name: 'QO Campus',
                locationType: LocationType.CAMPUS,
                state: State.IN_USE,
                capacity:30,
                startHour:7,
                startMinute:0,
                building:'Main',
                floor: 'I',
                room: '1-23',
                endHour:16,
                endMinute:30
               
            ),
              new Location(
                name: 'QO Valley Campus',
                locationType: LocationType.CAMPUS,
                state: State.IN_USE,
                capacity:25,
                startHour:7,
                startMinute:0,
                building:'Gymnasium',
                floor: 'I',
                room: '0',
                endHour:15,
                endMinute:30
               
            ),
          
          
            new Location(
                name: 'ONLINE',
                locationType: LocationType.ANYWHERE,
                state: State.IN_USE,
                startHour:0,
                startMinute:0,
                endHour:24,
                endMinute:0
               
            )
        ]
        
       def i = 0
       
        // save all the locationss and create ACLs
        for (location in locations) {    
            def organization  = Organization.findByName(organizations[i])
            if (!organization) {
                log.warn "load: could not find organization $organizations[i] for i = $i"
                return 
            } else 
            organization.addToLocations(location)
            i++
            
            log.trace "processing  location ${location} "
       
            if (!organization.save(flush:true)) { 
                organization.errors.allErrors.each {error ->
                    log.warn "An error occured with ${organization} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(location, user)
            
            
                }
            }
            log.debug "created Location ${location}"
        }
    
    
        log.info ("load: loaded ${Location.count()} out of ${locations.size()} locations")
       
        
    }
    

}