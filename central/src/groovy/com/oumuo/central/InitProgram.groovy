/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central


import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import static org.springframework.security.acls.domain.BasePermission.DELETE
import static org.springframework.security.acls.domain.BasePermission.READ
import static org.springframework.security.acls.domain.BasePermission.WRITE

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

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


        def programs = [ 
            new Program(name: 'Computer Science Diploma',
                person:Person.getByUsername('jfieds'),
                academicSession:AcademicSession.SEMESTER,
                academicStratum:AcademicStratum.ASSOCIATE,
                academicMajor:AcademicMajor.CS,
                ranking = 600,
                minimumGPA = GPA.D,
                credential:Credential.CERTIFICATE,
                sessionFee = 2000.0,
                organization: Organization.
                    getByName('Montgomery County Community College')   
            ),
          
            new Program(name: 'High School Physics Help',
                person:Person.getByUsername('jfieds'),
                academicSession:AcademicSession.MONTH,
                academicStratum:AcademicStratum.HIGH,
                academicMajor:AcademicMajor.SCIENCES,
                ranking = 400,
                minimumGPA = GPA.D,
                credential:Credential.NONE,
                sessionFee = 0.0,
                organization: Organization.
                    getByName('Quince Orchard High School')   
            ),
            new Program(name: 'Middle School English',
                person:Person.getByUsername('jfieds'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.MIDDLE,
                academicMajor:AcademicMajor.GENERAL,
                ranking = 800,
                minimumGPA = GPA.F,
                credential:Credential.NONE,
                sessionFee = 0.0,
                organization: Organization.
                    getByName('RidgeView Middle School')   
            ),
            new Program(name: 'लखनऊ विश्वविद्यालय कला संकाय',
                person:Person.getByUsername('jfieds'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.UNIVERSITY,
                academicMajor:AcademicMajor.ARTS,
                ranking = 950,
                minimumGPA = GPA.NONE,
                minimumPercentage = 55.0,
                credential:Credential.DEGREE,
                sessionFee = 20.0,
                organization: Organization.
                    getByName('लखनऊ विश्वविद्यालय')   
            )
        ]
        
       
        for (program in programs) {
          
        
            log.trace "processing  program ${program} "
       
            if (!program.save()) { 
                program.errors.allErrors.each {error ->
                    log.warn "An error occured with ${program} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(program, user)
      
               
                }
             
                log.debug "created Organization ${org}"
            }
        }
    
        log.info ("load: loaded ${Program.count()} out of ${programs.size()} organizations")
       
        
    }
    

}