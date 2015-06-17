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
class InitContent {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def contents = [ 
            new Content(
                name: 'Introduction Video',
                contentType: ContentType.VIDEO,
                contentRole: ContentRole.PRIMARY,
                learning:Learning.findByName('Introduction'),
                contentUrl: 'https://www.khanacademy.org/computing/computer-programming/programming/intro-to-programming/v/programming-intro',
                effort: 100,
                duration:200,
                organization: Organization.findByName('Montgomery County Community College')
            ),
            new Content(
                name: 'Data Structures',
                contentType: ContentType.VIDEO,
                contentRole: ContentRole.PRIMARY,
                learning:Learning.findByName('Data Structures'),
                contentUrl: 'https://www.khanacademy.org/computing/computer-science/algorithms/intro-to-algorithms/v/what-are-algorithms',
                effort: 100,
                duration:200,
         
                organization: Organization.findByName('Montgomery County Community College')
            ),
            new Content(
                name: 'Trees Text',
                contentType: ContentType.TEXT,
                contentRole: ContentRole.SECONDARY,
                learning:Learning.findByName('Trees'),
                content: 'Trees are really good ',
                effort: 100,
                duration:200,
          
                organization: Organization.findByName('Montgomery County Community College')
            )
               
        ]
        
       
        // save all the contentss and create ACLs
        for (content in contents) {     
            log.trace "processing  content ${content} "
       
            if (!content.save(flush:true)) { 
                content.errors.allErrors.each {error ->
                    log.warn "An error occured with ${content} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(content, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created Content ${content}"
        }
    log.info ("load: loaded ${Content.count()} out of ${contents.size()} contents")
    
    }
              
}
    