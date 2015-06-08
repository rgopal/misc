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
class InitRankingItem {
    
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

  
        def rankingItems = [ 
            new RankingItem(
                name: 'My May 2015 Ranking',
                person:Person.findByUserName('rpandey'),
                organizationRanking:
                Organization.findByName('Montgomery County Community College').
                    rankings.find{ it.name == 'May 2015 Ranking'}
                    
            ),
            new RankingItem(
                name: 'My July 2015 Ranking',
                person:Person.findByUserName('rpandey'),
                courseRanking:
                Course.findByName('Computer Science I').
                    rankings.find{ it.name == 'July 2015 Ranking'}
                    
            ),
            new RankingItem(
                name: 'My June 2015 Ranking',
                person:Person.findByUserName('rpandey'),
                programRanking:
                Program.findByName('Computer Science Diploma').
                    rankings.find{ it.name == 'June 2015 Ranking'}
                    
            )
     
     
        ]
        
       
        for (rankingItem in rankingItems) {   
        
            log.trace "processing  rankingItem ${rankingItem} "
       
            // call flush (ACL may give transient object not saved error)
            if (!rankingItem.save(flush:true)) { 
                rankingItem.errors.allErrors.each {error ->
                    log.debug "An error occured with ${rankingItem} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['rpandey']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(rankingItem, user)
      
                }
             
                log.debug "created RankingItem ${rankingItem}"
            }
        }
    
        log.info ("load: loaded ${RankingItem.count()} out of ${rankingItems.size()} rankingItems")
       
        
    }
    

}