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
class InitCourseRelation {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def programs = [
            'Computer Science Diploma'
        ]
        def courseRelations = [ 
            new CourseRelation(
                firstCourse: Course.findByName('Computer Science I'),
                secondCourse: Course.findByName('High School Physics'),
                relation: Relation.PRE_REQ,
                passingGrade: Grade.C
           
                )
               
        ]
        
       def i = 0
        // save all the courseRelationss and create ACLs
        for (courseRelation in courseRelations) {     
            
             def program  = Program.findByName(programs[i])
            if (!program) {
                log.warn "load: could not find program $programs[i] for i = $i"
                return 
            } else 
            program.addToCourseRelations(courseRelation)
            i++
            log.trace "processing  courseRelation ${courseRelation} "
       
            if (!program.save(flush:true)) { 
                program.errors.allErrors.each {error ->
                    log.warn "An error occured with ${program} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(courseRelation, user)
                    
               
                    
                   
                }
                
            }
             
            log.debug "created CourseRelation ${courseRelation}"
        }
    log.info ("load: loaded ${CourseRelation.count()} out of ${courseRelations.size()} courseRelations")
    
    }
              
}
    