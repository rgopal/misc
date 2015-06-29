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
class InitPersonResource {
    
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
 
        def jsmith = Person.findByUserName('jsmith')
     
        def personResources = [ 
            new PersonResource(
        
                minWeeklyHours: 9,
                maxWeeklyHours: 20,
                totalMoney: 2000.0
             
            ).addToOrganizationPreferences (
                new Preference (
                    sequence: 1,
                    name:'June 2015 Ranking',
                    person:cronRanking
                )  
           
            ).addToProgramPreferences (
                new Preference (
                    sequence: 1,
                    name:'June 2015 Ranking',
                    person:cronRanking
                ) ,
       
         ).addToCoursePreferences (
                new Preference (
                    sequence: 1,
                    name:'June 2015 Ranking',
                    person:cronRanking
                ) ,
       
         )
        ]
 
        for (personResource in personResources) {    
            
            jsmith.addToPersonResources(personResource)
            
            log.trace "processing  personResource ${personResource} "
       
            if (!jsmith.save(flush:true)) { 
                jsmith.errors.allErrors.each {error ->
                    log.warn "An error occured with ${jsmith} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jsmith']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(personResource, user)
            
            
                    for (preference in personResource.organizationPreferences) {
                        InitSpringSecurity.grantACL (preference, user)
                    }
                    log.info "  loaded ${PersonResource.findById(personResource.id).organizationPreferences?.size()} org preferences" 
                    
                     for (preference in personResource.programPreferences) {
                        InitSpringSecurity.grantACL (preference, user)
                    }
                    log.info "  loaded ${PersonResource.findById(personResource.id).programPreferences?.size()} program preferences"
                    
                     for (preference in personResource.coursePreferences) {
                        InitSpringSecurity.grantACL (preference, user)
                    }
                    log.info "  loaded ${PersonResource.findById(personResource.id).coursePreferences?.size()} course preferences"
           
                }
            }
            log.debug "created PersonResource ${personResource}"
        }
    
    
        log.info ("load: loaded ${PersonResource.count()} out of ${personResources.size()} personResources")
       
        
    }
    

}