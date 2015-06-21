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
class InitClazs {
    
    // def clazsFactory
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
        
        def terms = [
            'Computer Science Diploma - Fall 2015',
            'Computer Science Diploma - Spring 2016',
            'High School Physics Help - Fall 2015',
            'Middle School English - Summer 2015',
             'Middle School English - Summer 2015',
            'लखनऊ विश्वविद्यालय कला संकाय - Summer 2015'
        ]
 
        def clazss = [ 
            new Clazs(
                name: 'Computer Science Diploma - Fall 2015 Class',
                state :State.STARTED,
                course: Course.
                    findByName('Computer Science I'),
                teachingType: TeachingType.ONLINE,   
                recurring : true,
                recurringDays : Recurring.MWF,
                startHour: 10,
                startMinute: 30,
                duration: 60,
                durationUnit: DurationUnit.MINUTES,
                startDate: new Date().parse("MM-dd-yyyy", '01-09-2015'),
                endDate: new Date().parse("MM-dd-yyyy", '12-31-2015'),
                size:30,
                language: Language.ENGLISH
            ),
            new Clazs(
                name: 'Computer Science Diploma - Spring 2016 Class',
                state :State.PLANNED,
                course: Course.
                    findByName('Computer Science I'),
                teachingType: TeachingType.ONLINE,   
                recurring : true,
                recurringDays : Recurring.MWF,
                startHour: 10,
                startMinute: 30,
                duration: 60,
                durationUnit: DurationUnit.MINUTES,
                  startDate: new Date().parse("MM-dd-yyyy", '01-09-2015'),
                endDate: new Date().parse("MM-dd-yyyy", '12-31-2015'),
                size:30,
                language: Language.ENGLISH
               
            ),
            new Clazs(
                name: 'High School Physics Help - Fall 2015 Class',
                    state :State.PLANNED,
                course: Course.
                    findByName('Computer Science I'),
                teachingType: TeachingType.ONLINE,   
                recurring : true,
                recurringDays : Recurring.MWF,
                startHour: 10,
                startMinute: 30,
                duration: 60,
                durationUnit: DurationUnit.MINUTES,
                   startDate: new Date().parse("MM-dd-yyyy", '01-09-2015'),
                endDate: new Date().parse("MM-dd-yyyy", '12-31-2015'),
                size:30,
                language: Language.ENGLISH
            ),
            new Clazs(
                name: 'Middle School English - Summer 2015 Class',
                state :State.PLANNED,
                course: Course.
                    findByName('Computer Science I'),
                teachingType: TeachingType.ONLINE,   
                recurring : true,
                recurringDays : Recurring.MWF,
                startHour: 10,
                startMinute: 30,
                duration: 60,
                durationUnit: DurationUnit.MINUTES,
              startDate: new Date().parse("MM-dd-yyyy", '01-09-2015'),
                endDate: new Date().parse("MM-dd-yyyy", '12-31-2015'),
                size:30,
                language: Language.ENGLISH
               
            ),
            new Clazs(
                name: 'लखनऊ विश्वविद्यालय कला संकाय - Summer 2015 Class',
                 state :State.PLANNED,
                course: Course.
                    findByName('Computer Science I'),
                teachingType: TeachingType.ONLINE,   
                recurring : true,
                recurringDays : Recurring.MWF,
                startHour: 10,
                startMinute: 30,
                duration: 60,
                durationUnit: DurationUnit.MINUTES,
                  startDate: new Date().parse("MM-dd-yyyy", '01-09-2015'),
                endDate: new Date().parse("MM-dd-yyyy", '12-31-2015'),
                size:30,
                language: Language.ENGLISH
              
            )
            
        ]
        
        
       
        // save all the clazsss and create ACLs
        def i = 0
        for (clazs in clazss) {     
                    
            def term = Term.findByName(terms[i])
            if (!term) {
                log.warn "load: could not find program $terms[i]"
                return
            }     
            i++
            term.addToClazss(clazs)
            
            log.trace "processing  clazs ${clazs} "
       
            if (!term.save(flush:true)) { 
                term.errors.allErrors.each {error ->
                    log.warn "An error occured with ${term} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(clazs, user)
                    /*
            
                    for (ranking in clazs.rankings) {
                    InitSpringSecurity.grantACL (ranking, user)
                    }
                    log.info "  loaded ${Clazs.findById(clazs.id).rankings?.size()} rankings" 
                   
                    for (requirement in clazs.requirements) {
                    InitSpringSecurity.grantACL (requirement, user)
                    for (standardizedTest in requirement.standardizedTests) {
                    InitSpringSecurity.grantACL (standardizedTest, user)
                    }
                    log.info "  loaded ${Requirement.findById(requirement.id).standardizedTests?.size()} clazs Requirement standardized Tests"
                    }
                    log.info "  loaded ${Clazs.findById(clazs.id).requirements?.size()} clazs Requirements"
                     */
                }
            }
            log.debug "created Clazs ${clazs}"
        }
    
    
        log.info ("load: loaded ${Clazs.count()} out of ${clazss.size()} clazss")
       
        
    }
    

}