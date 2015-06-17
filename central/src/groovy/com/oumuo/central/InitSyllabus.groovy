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
class InitSyllabus {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def syllabuss = [ 
            new Syllabus(name: 'CS 1',
                sequence: "1",
                course:Course.findByName('Computer Science I'),
                learning: Learning.findByName('Introduction')
                
            ).addToSubSyllabuss( 
                new Syllabus(name: 'CS 1.1',
                sequence: "1.1",
                course:Course.findByName('Computer Science I'),
                learning: Learning.findByName('Data Structures')
                )
                
            ).addToSubSyllabuss(
                new Syllabus(name: 'CS 1.1.l',
                    sequence: "1.1.1",
                    course:Course.findByName('Computer Science I'),
                    learning: Learning.findByName('Trees')
                
                )
            )
        ]
        
       
        // save all the syllabusss and create ACLs
        for (syllabus in syllabuss) {     
            log.trace "processing  syllabus ${syllabus} "
       
            if (!syllabus.save(flush:true)) { 
                syllabus.errors.allErrors.each {error ->
                    log.warn "An error occured with ${syllabus} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(syllabus, user)
                    
                }
                for (subSyllabus in syllabus.subSyllabuss) {
                    if (!subSyllabus.save(flush:true)) { 
                        subSyllabus.errors.allErrors.each {error ->
                            log.warn "An error occured with ${subSyllabus} $error"
                        }
                    } else {     
                        // give permissions to two users
                        for (user in ['jfields', 'mjohns']) {
                            log.trace "   starting ACL creations for $user"
                            InitSpringSecurity.grantACL(subSyllabus, user)
                    
                        } 
                    }
                }
            }
            log.debug "created Syllabus ${syllabus}"
        }
    log.info ("load: loaded " + Syllabus.count() + " out of " + syllabuss.size() + " syllabuss")
    }
             
}
    