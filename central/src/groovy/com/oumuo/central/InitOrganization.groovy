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
class InitOrganization {
  
   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def cronRanking = Person.findByUserName('cronRanking')

        def organizations = [ 
            new Organization(name: 'Montgomery County Community College',
                preferredLanguage:Language.ENGLISH,
                academicStratum:AcademicStratum.ASSOCIATE,
                workEmail:'admin@mccc.edu'  
            ).addToRankings (
                new Ranking (
                    sequence: 1,
                    name:'May 2015 Ranking',
                    person:cronRanking
                )
            ),
          
            new Organization(name: 'Quince Orchard High School', 
       
                preferredLanguage:Language.ENGLISH,
                academicStratum:AcademicStratum.HIGH,
                workEmail:'admin@mcps.edu' ).addToStaffings( 
                new Staffing (
                    email:'mikejohns@facebook.com', 
                    sequence:1, name:'Primary', main:true
                )
                
            ),
            new Organization(name: 'RidgeView Middle School',
                preferredLanguage:Language.ENGLISH,
                academicStratum:AcademicStratum.MIDDLE,
                workEmail:'admin@mcps.edu').
                addToStaffings (
                new Staffing( 
                    sequence:1, // sequence can't be null
                    name:'Manager', main:false
                )
            ).addToStaffings(
                // note that this is after (not sure if preInsert would work
                new Staffing (
                    sequence: 2,
                    name:'Counselor'
                )
            ),
           
            new Organization(name: 'R K Acakdemy', 
                academicStratum:AcademicStratum.HIGH,
                country:Country.INDIA,
                zip:'250001', city: 'Meerut', state: 'Uttar Pradesh',
                preferredLanguage:Language.ENGLISH
            ),
            new Organization(name: 'लखनऊ विश्वविद्यालय', 
                academicStratum:AcademicStratum.UNIVERSITY,
                country:Country.INDIA,
                zip:'260001', city:'लखनऊ', state: 'उत्तर प्रदेश',
                preferredLanguage:Language.HINDI
            )
        ]
        
       
        for (org in organizations) {
          
        
            log.trace "processing  organization ${org} "
       
            // call flush (ACL may give transient object not saved error)
            if (!org.save(flush:true)) { 
                org.errors.allErrors.each {error ->
                    log.debug "An error occured with ${org} $error"
                }
            } else {     
                // give permissions to two users
                for (user in ['jfields', 'mjohns']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(org, user)
      
                    for (staffing in org.staffings) {
                        InitSpringSecurity.grantACL (staffing, user)
                    }
                    
                    for (ranking in org.rankings) {
                        InitSpringSecurity.grantACL (ranking, user)
                    }
                }
                log.info "  loaded ${Organization.findById(org.id).staffings?.size()} staffing"
                log.info "  loaded ${Organization.findById(org.id).rankings?.size()} ranking"     
                log.debug "created Organization ${org}"
            }
        }
    
        log.info ("load: loaded ${Organization.count()} out of ${organizations.size()} organizations")
       
        
    }
    

}