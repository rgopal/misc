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
class InitTerm {
    
    // def termFactory
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
        
        def programs = [
            'Computer Science Diploma',
            'Computer Science Diploma',
            'High School Physics Help',
            'Middle School English',
            'लखनऊ विश्वविद्यालय कला संकाय'
        ]
 
        def terms = [ 
            new Term(
                name: 'Computer Science Diploma - Fall 2015',
                duration :5,
                durationUnit : DurationUnit.MONTHS,
                state :State.STARTED,
                academicSession:AcademicSession.SEMESTER,      
                fee: 2000.0,
                program: Program.
                    findByName('Computer Science Diploma')
            ),
            new Term(
                name: 'Computer Science Diploma - Spring 2016',
                duration : 5,
                durationUnit : DurationUnit.MONTHS,
                state : State.PLANNED,
                academicSession:AcademicSession.SEMESTER,      
                fee: 20.0
               
            ),
            new Term(
                name: 'High School Physics Help - Fall 2015',
                duration :5,
                durationUnit : DurationUnit.MONTHS,
                state : State.STARTED,
                academicSession:AcademicSession.SEMESTER,      
                fee: 600.0
            ),
            new Term(
                name: 'Middle School English - Summer 2015',
                duration : 5,
                durationUnit : DurationUnit.MONTHS,
                state : State.IN_USE,
                academicSession:AcademicSession.SEMESTER,      
                fee: 1000.0
               
            ),
            new Term(
                name: 'लखनऊ विश्वविद्यालय कला संकाय - Summer 2015',
                duration : 5,
                durationUnit :DurationUnit.MONTHS,
                state : State.PAUSED,
                academicSession:AcademicSession.SEMESTER,      
                fee: 200.0
              
            )
            
        ]
        
        
       
        // save all the termss and create ACLs
        def i = 0
        for (term in terms) {     
                    
            def program = Program.findByName(programs[i])
            if (!program) {
                log.warn "load: could not find program $programs[i]"
                return
            }     
            i++
            program.addToTerms(term)
            
            log.trace "processing  term ${term} "
       
            if (!program.save(flush:true)) { 
                program.errors.allErrors.each {error ->
                    log.warn "An error occured with ${program} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(term, user)
                    /*
            
                    for (ranking in term.rankings) {
                    InitSpringSecurity.grantACL (ranking, user)
                    }
                    log.info "  loaded ${Term.findById(term.id).rankings?.size()} rankings" 
                   
                    for (requirement in term.requirements) {
                    InitSpringSecurity.grantACL (requirement, user)
                    for (standardizedTest in requirement.standardizedTests) {
                    InitSpringSecurity.grantACL (standardizedTest, user)
                    }
                    log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} term Requirement standardized Tests"
                    }
                    log.info "  loaded ${Term.findById(term.id).requirements?.size()} term Requirements"
                     */
                }
            }
            log.debug "created Term ${term}"
        }
    
    
        log.info ("load: loaded ${Term.count()} out of ${terms.size()} terms")
       
        
    }
    

}