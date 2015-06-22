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
class InitEnrollment {
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')
        
        // use author as the parent, even though there are two.
        // hopefully once they are stored in the database, new
        // sessions would be fine with other parents
        
        def persons = [
            'jsmith',
            'jsmith',
            'jsmith',
            'jsmith',
            'jsmith'
        ]
        
        // need to have addTo for each of the following
       

        def enrollments = [ 
            new Enrollment(
          
                enrollmentType: EnrollmentType.CREDIT
              
            ),
            new Enrollment(
                
              
                enrollmentType: EnrollmentType.AUDIT
              
            ),
            new Enrollment(
             
             
                enrollmentType: EnrollmentType.PLANNED
             
              
            ),
            new Enrollment(
             
                enrollmentType: EnrollmentType.PLANNED,
              
            ),
             new Enrollment(
             
                enrollmentType: EnrollmentType.PLANNED,     // for Instruction
              
            )
               
        ]
        
       
        // save all the enrollmentss and create ACLs
        def i = 0
        for (enrollment in enrollments) {     
                    
            def person = Person.findByUserName(persons[i])
            if (!person) {
                log.warn "load: could not find person $persons[i]"
                return
            }     
            
            person.addToEnrollments(enrollment)
            
            log.trace "processing  enrollment ${enrollment} "
       
            if (!person.save(flush:true)) { 
                person.errors.allErrors.each {error ->
                    log.warn "An error occured with ${person} $error"
                }
            } else {            
                log.trace "   starting ACL creations for $person"
                InitSpringSecurity.grantACL(enrollment, person.userName)
                      
            }
             
            // now other O2M association
            if (i==0)
                ClassSession.findByName('Computer Science I - Fall 2015 Class Session').
                    addToEnrollments(enrollment).save(flush:true)
            else if (i==1)
            Clazs.findByName('Computer Science I - Fall 2015 Class').
                addToEnrollments(enrollment).save(flush:true)
            else if (i==2)
            Term.findByName('Computer Science Diploma - Fall 2015').
                addToEnrollments(enrollment).save(flush:true)
            else if (i==3)
            Program.findByName('Computer Science Diploma').
                addToEnrollments(enrollment).save(flush:true)
            else if (i==4)
            ClassSession.findByName('Computer Science I - Fall 2015 Class Session').
                instructions[0].
                addToEnrollments(enrollment).save(flush:true)
            
            i++
            log.debug "created Enrollment ${enrollment}"
        }
        //
        
       
        log.info ("load: loaded ${Enrollment.count()} out of ${enrollments.size()} enrollments")
    }
    
    
              
}
    