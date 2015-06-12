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
class InitLearning {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def learnings = [ 
            new Learning(
                name: 'Introduction',
        
                hierarchy: Hierarchy.AREA,
                Organization: Organization.findByName('Montgomery County Community College')
                ),
                 new Learning(
                name: 'Data Structures',
              
                hierarchy: Hierarchy.AREA,
                Organization: Organization.findByName('Montgomery County Community College')
                ),
                  new Learning(
                name: 'Trees',
              
                hierarchy: Hierarchy.TOPIC,
                Organization: Organization.findByName('Montgomery County Community College')
                )
               
        ]
        
       
        // save all the learningss and create ACLs
        for (learning in learnings) {     
            log.trace "processing  learning ${learning} "
       
            if (!learning.save(flush:true)) { 
                learning.errors.allErrors.each {error ->
                    log.warn "An error occured with ${learning} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(learning, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created Learning ${learning}"
        }
    }
    
    // log.info ("load: loaded ${Learning.count()} out of ${learnings.size()} learnings")
              
}
    