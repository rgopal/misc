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
class InitPositionProgram {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def positionPrograms = [ 
            new PositionProgram(
                position: Position.findByName('Software Engineer'),
                role: Role.PRIMARY,
                program: Program.findByName('Computer Science Diploma')
            ),
              new PositionProgram(
                position: Position.findByName('Software Engineer'),
                role: Role.SECONDARY,
                program: Program.findByName('Computer Science Diploma')
            ),
             new PositionProgram(
            position: Position.findByName('Chief Architect'),
                role: Role.PRIMARY,
                program: Program.findByName('Computer Science Diploma')
            )
               
        ]
        
       
        // save all the positionProgramss and create ACLs
        for (positionProgram in positionPrograms) {     
            log.trace "processing  positionProgram ${positionProgram} "
       
            if (!positionProgram.save(flush:true)) { 
                positionProgram.errors.allErrors.each {error ->
                    log.warn "An error occured with ${positionProgram} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(positionProgram, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created PositionProgram ${positionProgram}"
        }
    log.info ("load: loaded ${PositionProgram.count()} out of ${positionPrograms.size()} positionPrograms")
    
    }
              
}
    