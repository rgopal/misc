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
 
        def programs = [ 
            new Program(name: 'Computer Science Diploma',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.SEMESTER,
                academicStratum:AcademicStratum.ASSOCIATE,
                academicMajor:AcademicMajor.CS,
                ranking: 600,
                minimumGPA: GPA.D,
                credential:Credential.CERTIFICATE,
                sessionFee: 2000.0,
                organization: Organization.
                    findByName('Montgomery County Community College').addToRankings (
                    new Ranking (
                        sequence: 1,
                        name:'June 2015 Ranking',
                        person:cronRanking
                    )  
                )
            ),
          
            new Program(name: 'High School Physics Help',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.MONTH,
                academicStratum:AcademicStratum.HIGH,
                academicMajor:AcademicMajor.SCIENCES,
                ranking: 400,
                minimumGPA : GPA.D,
                credential:Credential.NONE,
                sessionFee : 0.0,
                organization: Organization.
                    findByName('Quince Orchard High School')   
            ),
            new Program(name: 'Middle School English',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.MIDDLE,
                academicMajor:AcademicMajor.GENERAL,
                ranking : 800,
                minimumGPA : GPA.F,
                credential:Credential.NONE,
                sessionFee : 0.0,
                organization: Organization.
                    findByName('RidgeView Middle School')   
            ),
            new Program(name: 'लखनऊ विश्वविद्यालय कला संकाय',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.UNIVERSITY,
                academicMajor:AcademicMajor.ARTS,
                ranking : 950,
                minimumGPA : GPA.NONE,
                minimumPercentage : 55.0,
                credential:Credential.DEGREE,
                sessionFee : 20.0,
                organization: Organization.
                    findByName('लखनऊ विश्वविद्यालय')   
            )
        ]
        
       
        // save all the programss and create ACLs
        for (program in programs) {     
            log.trace "processing  program ${program} "
       
            if (!program.save(flush:true)) { 
                program.errors.allErrors.each {error ->
                    log.warn "An error occured with ${program} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(program, user)
                }
             
                log.debug "created Program ${program}"
            }
        }
    
        log.info ("load: loaded ${Program.count()} out of ${programs.size()} programs")
       
        
    }
    

}