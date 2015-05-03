/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo
import central.UserRole as ROLE


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

import central.Organization
import central.Staffing
import central.Language
import central.Email
import central.Race
import central.Country
import com.oumuo.UserLogin
import com.oumuo.UserLoginSecurityGroup
import com.oumuo.Authority
import com.oumuo.SecurityGroupAuthority


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
    
   
    void load (Object aclUtilSer, Object aclSer, Object objectIdentityRetrievalStr) {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"


        def organizations = [ 
            new Organization(name: 'Montgomery County Community College',
                preferredLanguage:Language.ENGLISH,
                workEmail:'admin@mccc.edu'
                
            ),
          
            new Organization(name: 'Quince Orchard High School', 
       
                preferredLanguage:Language.ENGLISH,
                workEmail:'admin@mcps.edu' ).addToStaffings( 
                new Staffing (
                    email:'mikejohns@facebook.com', 
                    sequence:1, name:'Primary', main:true
                )
                
            ),
            new Organization(name: 'RidgeView Middle School',
  
                preferredLanguage:Language.ENGLISH).
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
                preferredLanguage:Language.ENGLISH
            ),
            new Organization(name: 'राहुल नंदा', 
                country:Country.INDIA,
                zip:'260014', city:'लखनऊ', state: 'उत्तर प्रदेश',
                preferredLanguage:Language.HINDI
            )
        ]
        
       
        for (org in organizations) {
          
            log.trace "processing  organization ${org.organization} "
       
            if (!org.save()) { 
                org.errors.allErrors.each {error ->
                    log.debug "An error occured with ${org} $error"
                }
            } else {
            
                log.trace "load: starting ACL creations for ${user} util ${aclUtilService} aclService ${aclService} ${objectIdentityRetrievalStrategy}"
            
                InitSpringSecurity.grantACL(org, 'jfields')
      
                for (staffing in org.staffings) {
                    InitSpringSecurity.grantACL (staffing, 'jfields')
                }
                
                log.info "  loaded ${Organization.findById(org.id).organization.staffings?.size()} staffing"

                log.debug "created Organization ${org}"
            }
        }
    
        log.info ("load: loaded ${Organization.count()} out of ${organizations.size()} organizations")
       
        
    }
    

}