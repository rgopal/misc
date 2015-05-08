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
class InitOrganization {
    
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


        def organizations = [ 
            new Organization(name: 'Montgomery County Community College',
                preferredLanguage:Language.ENGLISH,
                academicStratum:AcademicStratum.ASSOCIATE,
                workEmail:'admin@mccc.edu'
                
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
       
            if (!org.save()) { 
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
                }
                log.info "  loaded ${Organization.findById(org.id).staffings?.size()} staffing"

                log.debug "created Organization ${org}"
            }
        }
    
        log.info ("load: loaded ${Organization.count()} out of ${organizations.size()} organizations")
       
        
    }
    

}