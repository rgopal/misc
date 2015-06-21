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
class InitClassSession {
    
    // def classSessionFactory
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
        
        def clazss = [
            'Computer Science Diploma - Fall 2015 Class',
            'Computer Science Diploma - Spring 2016 Class',
            'High School Physics Help - Fall 2015 Class',
            'Middle School English - Summer 2015 Class',
            'लखनऊ विश्वविद्यालय कला संकाय - Summer 2015 Class'
        ]
 
        def classSessions = [ 
            new ClassSession(
                sequence:'1',
                name: 'Computer Science Diploma - Fall 2015 Class Session',
                state :State.STARTED
             
            ),
            new ClassSession(
                sequence:'1',
                name: 'Computer Science Diploma - Spring 2016 Class Session',
                state :State.PLANNED
            ),
            new ClassSession(
                sequence:'1', 
                name: 'High School Physics Help - Fall 2015 Class Session',
                state :State.PLANNED
           
            ),
            new ClassSession(
                sequence:'1',
                name: 'Middle School English - Summer 2015 Class Sesssion',
                state :State.PLANNED
         
               
            ),
            new ClassSession(
                sequence:'1',
                name: 'लखनऊ विश्वविद्यालय कला संकाय - Summer 2015 Class Session',
                state :State.PLANNED
               
              
            )
            
        ]
        
        
       
        // save all the classSessionss and create ACLs
        def i = 0
        for (classSession in classSessions) {     
                    
            def clazs = Clazs.findByName(clazss[i])
            if (!clazs) {
                log.warn "load: could not find program $clazss[i]"
                return
            }     
            i++
            clazs.addToClassSessions(classSession)
            
            log.trace "processing  classSession ${classSession} "
       
            if (!clazs.save(flush:true)) { 
                clazs.errors.allErrors.each {error ->
                    log.warn "An error occured with ${clazs} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(classSession, user)
              
                }
            }
            log.debug "created ClassSession ${classSession}"
        }
    
    
        log.info ("load: loaded ${ClassSession.count()} out of ${classSessions.size()} classSessions")
       
        
    }
    

}