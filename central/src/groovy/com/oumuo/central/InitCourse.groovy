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
    
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def courses = [ 
            new Course(name: 'Computer Science I',
              
                academicSession:AcademicSession.SEMESTER,
                academicStratum:AcademicStratum.ASSOCIATE,
                academicMajor:AcademicMajor.CS,
                ranking: 600,
                passingGrade: Grade.C,
                credential:Credential.CERTIFICATE,
                fee: 2000.0,
                organization: Organization.
                    findByName('Montgomery County Community College')   
            ).addToComments (
                new Comment (
                    comment: "course comment",
                    person:Person.findByUserName('jfields'),
                    detailedComment: "http://www.edx.com",
                    commentType: ItemType.URL
                )
            ).addToObjectives (
                new Objective (
                    learningType: LearningType.FACTS,
                    sequence:1,
                    reference: "http://www.edx.com"
                )
            ).addToRankings (
                new Ranking (
                    sequence: 1,            // Has to be here, beforeInsert can't deal automated
                    name:'July 2015 Ranking',
                    person:cronRanking
                )
            ).addToRequirements (
                new Requirement (
                    openness: 800,
                    sequence:1,
                    reference: "http://www.coursera.com"
                ).addToStandardizedTests (
                    new StandardizedTest(
                        sequence:1,
                        score:700.0f        
                    )
                )
            ).addToTeachingRequirements (
                new Requirement (
                    openness: 855,
                    sequence:1,
                    reference: "http://www.coursera.com"
                ).addToStandardizedTests (
                    new StandardizedTest(
                        sequence:1,
                        score:780.0f        
                    )
                )
            ),
          
            new Course(name: 'High School Physics',
                
                academicSession:AcademicSession.MONTH,
                academicStratum:AcademicStratum.HIGH,
                academicMajor:AcademicMajor.SCIENCES,
                ranking: 400,
                passingGrade : Grade.D,
                credential:Credential.NONE,
                fee : 0.0,
                organization: Organization.
                    findByName('Quince Orchard High School')   
            ),
            new Course(name: 'Middle School English Drama',
        
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.MIDDLE,
                academicMajor:AcademicMajor.GENERAL,
                ranking : 800,
                passingGrade : Grade.F,
                credential:Credential.NONE,
                fee : 0.0,
                organization: Organization.
                    findByName('RidgeView Middle School')   
            ),
            new Course(name: 'लखनऊ विश्वविद्यालय कला 1',
           
                academicSession:AcademicSession.YEAR,
                academicStratum:AcademicStratum.UNIVERSITY,
                academicMajor:AcademicMajor.ARTS,
                ranking : 950,
                passingGrade : Grade.NONE,
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
       
            if (!course.save(flush:true)) { 
                course.errors.allErrors.each {error ->
                    log.warn "An error occured with ${course} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user"
                    InitSpringSecurity.grantACL(course, user)
                    
               
                    for (comment in course.comments) {
                        InitSpringSecurity.grantACL (comment, user)
                    }
                    
                    for (objective in course.objectives) {
                        InitSpringSecurity.grantACL (objective, user)
                    }
                    for (requirement in course.requirements) {
                        InitSpringSecurity.grantACL (requirement, user)
                        for (standardizedTest in requirement.standardizedTests) {
                            InitSpringSecurity.grantACL (standardizedTest, user)
                        }
                        log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} course Requirement standardized Tests"
                    }
                     for (requirement in course.teachingRequirements) {
                        InitSpringSecurity.grantACL (requirement, user)
                        for (standardizedTest in requirement.standardizedTests) {
                            InitSpringSecurity.grantACL (standardizedTest, user)
                        }
                        log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} course teaching Requirement standardized Tests"
                    }
                    for (ranking in course.rankings) {
                        InitSpringSecurity.grantACL (ranking, user)
                    }
                   
                }
                log.info "  loaded ${Course.findById(course.id).comments?.size()} comment"
                log.info "  loaded ${Course.findById(course.id).objectives?.size()} course Objectives"
                log.info "  loaded ${Course.findById(course.id).requirements?.size()} course Requirements"
                 log.info "  loaded ${Course.findById(course.id).teachingRequirements?.size()} course teaching Requirements"
                log.info "  loaded ${Course.findById(course.id).rankings?.size()}  Rankings"
            }
             
            log.debug "created Course ${course}"
        }
    }
    
    // log.info ("load: loaded ${Course.count()} out of ${courses.size()} courses")
              
}
    