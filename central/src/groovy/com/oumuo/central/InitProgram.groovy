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
class InitProgram {
    
    // def sessionFactory
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
 
        def organizations = [
            'Montgomery County Community College',
            'Quince Orchard High School',
            'RidgeView Middle School',
            'लखनऊ विश्वविद्यालय'
        ]
        def programs = [ 
            new Program(name: 'Computer Science Diploma',
        
                academicSession:AcademicSession.SEMESTER,
                academicStratum:AcademicStratum.ASSOCIATE,
                academicMajor:AcademicMajor.CS,
                ranking: 600,
                minimumGPA: Grade.D,
                credential:Credential.CERTIFICATE,
                sessionFee: 2000.0
             
            ).addToRankings (
                new Ranking (
                    sequence: 1,
                    name:'June 2015 Ranking',
                    person:cronRanking
                )  
            ).addToRequirements (
                new Requirement (
                    openness: 820,
                    sequence:1,
                    reference: "http://www.coua.com"
                ).addToStandardizedTests (
                    new StandardizedTest(
                        sequence:1,
                        score:620.0f        
                    )
                )
            ),
       
          
            new Program(name: 'High School Physics Help',
             
                academicSession:AcademicSession.MONTH,
                academicStratum:AcademicStratum.HIGH,
                academicMajor:AcademicMajor.SCIENCES,
                ranking: 400,
                minimumGPA : Grade.D,
                credential:Credential.NONE,
                sessionFee : 0.0
                  
            ),
            new Program(name: 'Middle School English',
       
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.MIDDLE,
                academicMajor:AcademicMajor.GENERAL,
                ranking : 800,
                minimumGPA : Grade.F,
                credential:Credential.NONE,
                sessionFee : 0.0
         
            ),
            new Program(name: 'लखनऊ विश्वविद्यालय कला संकाय',
          
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.UNIVERSITY,
                academicMajor:AcademicMajor.ARTS,
                ranking : 950,
                minimumGPA : Grade.NONE,
                minimumPercentage : 55.0,
                credential:Credential.DEGREE,
                sessionFee : 20.0
              
            )
        ]
        
       def i = 0
       
        // save all the programss and create ACLs
        for (program in programs) {    
            def organization  = Organization.findByName(organizations[i])
            if (!organization) {
                log.warn "load: could not find organization $organizations[i] for i = $i"
                return 
            } else 
            organization.addToPrograms(program)
            i++
            
            log.trace "processing  program ${program} "
       
            if (!organization.save(flush:true)) { 
                organization.errors.allErrors.each {error ->
                    log.warn "An error occured with ${organization} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(program, user)
            
            
                    for (ranking in program.rankings) {
                        InitSpringSecurity.grantACL (ranking, user)
                    }
                    log.info "  loaded ${Program.findById(program.id).rankings?.size()} rankings" 
                    for (requirement in program.requirements) {
                        InitSpringSecurity.grantACL (requirement, user)
                        for (standardizedTest in requirement.standardizedTests) {
                            InitSpringSecurity.grantACL (standardizedTest, user)
                        }
                        log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} program Requirement standardized Tests"
                    }
                    log.info "  loaded ${Program.findById(program.id).requirements?.size()} program Requirements"
                
                }
            }
            log.debug "created Program ${program}"
        }
    
    
        log.info ("load: loaded ${Program.count()} out of ${programs.size()} programs")
       
        
    }
    

}