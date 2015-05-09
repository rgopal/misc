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
class InitCourse {
    
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


        def courses = [ 
            new Course(name: 'Computer Science I',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.SEMESTER,
                academicStratum:AcademicStratum.ASSOCIATE,
                academicMajor:AcademicMajor.CS,
                ranking: 600,
                passingGPA: GPA.C,
                credential:Credential.CERTIFICATE,
                fee: 2000.0,
                organization: Organization.
                    findByName('Montgomery County Community College')   
            ),
          
            new Course(name: 'High School Physics',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.MONTH,
                academicStratum:AcademicStratum.HIGH,
                academicMajor:AcademicMajor.SCIENCES,
                ranking: 400,
                passingGPA : GPA.D,
                credential:Credential.NONE,
                fee : 0.0,
                organization: Organization.
                    findByName('Quince Orchard High School')   
            ),
            new Course(name: 'Middle School English Drama',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.MIDDLE,
                academicMajor:AcademicMajor.GENERAL,
                ranking : 800,
                passingGPA : GPA.F,
                credential:Credential.NONE,
                fee : 0.0,
                organization: Organization.
                    findByName('RidgeView Middle School')   
            ),
            new Course(name: 'लखनऊ विश्वविद्यालय कला 1',
                person:Person.findByUserName('jfields'),
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.UNIVERSITY,
                academicMajor:AcademicMajor.ARTS,
                ranking : 950,
                passingGPA : GPA.NONE,
                passingPercentage : 55.0,
                credential:Credential.DEGREE,
                fee : 20.0,
                organization: Organization.
                    findByName('लखनऊ विश्वविद्यालय')   
            )
        ]
        
       
        // save all the coursess and create ACLs
        for (course in courses) {     
            log.trace "processing  course ${course} "
       
            if (!course.save()) { 
                course.errors.allErrors.each {error ->
                    log.warn "An error occured with ${course} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(course, user)
                }
             
                log.debug "created Course ${course}"
            }
        }
    
        log.info ("load: loaded ${Course.count()} out of ${courses.size()} courses")
       
        
    }
    

}