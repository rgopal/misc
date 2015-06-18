/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central


import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import grails.converters.*


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
class InitStudentProgram {
    
    // def studentProgramFactory
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
 
        def studentPrograms = [ 
            new StudentProgram(
                name: 'Computer Science Diploma - jsmith',
                person: Person.findByUserName('jsmith'),
                minEffort :5,
                maxEffort :10,
                perPeriod : AcademicSession.WEEK,
                state :State.STARTED,
     
                totalFee: 2000.0,
                program: Program.
                    findByName('Computer Science Diploma')
            )
            
        ]
        
       
        // save all the studentProgramss and create ACLs
        for (studentProgram in studentPrograms) {     
            log.trace "processing  studentProgram ${studentProgram} "
            
            // first deep clone program
            
           def newProgram = InitSpringSecurity.deepClone(studentProgram.program)
           
         

            JSON.use("deep") 
            def converter = newProgram as JSON
            converter.prettyPrint = true
            def json = converter.toString()

           log.trace "load: cloned program " + json
           
           //studentProgram.program = newProgram
       
            if (!studentProgram.save(flush:true)) { 
                studentProgram.errors.allErrors.each {error ->
                    log.warn "An error occured with ${studentProgram} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(studentProgram, user)
            /*
            
                    for (ranking in studentProgram.rankings) {
                        InitSpringSecurity.grantACL (ranking, user)
                    }
                    log.info "  loaded ${StudentProgram.findById(studentProgram.id).rankings?.size()} rankings" 
                   
                    for (requirement in studentProgram.requirements) {
                        InitSpringSecurity.grantACL (requirement, user)
                        for (standardizedTest in requirement.standardizedTests) {
                            InitSpringSecurity.grantACL (standardizedTest, user)
                        }
                        log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} studentProgram Requirement standardized Tests"
                    }
                    log.info "  loaded ${StudentProgram.findById(studentProgram.id).requirements?.size()} studentProgram Requirements"
                */
                }
            }
            log.debug "created StudentProgram ${studentProgram}"
        }
    
    
        log.info ("load: loaded ${StudentProgram.count()} out of ${studentPrograms.size()} studentPrograms")
       
        
    }
    

}