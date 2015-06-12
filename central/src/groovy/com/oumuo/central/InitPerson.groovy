/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central
import com.oumuo.lookup.UserRole as ROLE


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

import com.oumuo.lookup.*
import com.oumuo.*



import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitPerson {
    

    void load () {
  
        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def users = [ 
            new UserLogin(username: 'jsmith', enabled: true, 
                password: 'jsmith', 
                person:  new Person(name: 'John Smith',
                    userName : 'jsmith',
                    sex:Person.Sex.MALE, race: Race.WHITE,
                    dateOfBirth:Date.parse('dd-MM-yyyy','01-09-1960'), 
                    preferredLanguage:Language.ENGLISH,
                    homeEmail:'john.smith@gmail.com'
                ).addToCapabilitys (
                    new Requirement (
                        openness: 900,
                       // sequence:1,
                        reference: "http://www.mit.edu"
                    ).addToStandardizedTests (
                        new StandardizedTest(
                            sequence:1,
                            score:750.0f        
                        )
                    )
                )
            ),
          
            // this also creates a 1 account record
            new UserLogin(username: 'mjohns',enabled: true, 
                password: 'mjohns', 
                person: new Person(name: 'Mike Johns', 
                    userName : 'mjohns',
                    sex:Person.Sex.MALE, race: Race.WHITE, 
                    dateOfBirth:Date.parse('dd-MM-yyyy','0-09-1970'),
                    preferredLanguage:Language.ENGLISH,
                    homeEmail:'Mike.Johns@gmail.com' ).addToAccounts( 
                    new Account(
                        email:'mikejohns@facebook.com', 
                        sequence:1, name:'Primary', main:true
                    )
                ).addToComments (
                    new Comment (
                        comment: "first comment bigger than 20",
                        detailedComment: "http://www.google.com",
                        commentType: ItemType.URL
                    )
                )
            ),
            new UserLogin(username: 'jfields',enabled: true, 
                password: 'jfields',  
                person:   new Person(name: 'Jane Fields',
                    userName : 'jfields',
                    sex:Person.Sex.FEMALE, race: Race.BLACK, 
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1980'),
                    preferredPersonRole:ROLE.ROLE_STUDENT,
                    preferredLanguage:Language.ENGLISH).
                    addToAccounts (
                    new Account(
                        email:'janefields@twitter.com', 
                        sequence:1, // sequence can't be null
                        name:'Secondary', main:false
                    )
                ).addToAccounts(
                    // note that this is after (not sure if preInsert would work
                    new Account (
                        email:'janefields@yahoo.com',
                        sequence: 2,
                        main:true,
                        name:'Primary'
                    )
                ).addToPersonRoles (
                    new PersonRole (
                        language:Language.ENGLISH,
                        sequence: 1,
                        current:true,
                        userRole:ROLE.ROLE_STUDENT
                    )
                ).addToPersonRoles (
                    new PersonRole (
                        language:Language.SPANISH,
                        sequence: 2,
                        current:false,
                        userRole:ROLE.ROLE_TEACHER
                    )
                ).addToPersonRoles (
                    new PersonRole (
                        language:Language.SPANISH,
                        sequence: 3,
                        current:false,
                        userRole:ROLE.ROLE_COUNSELOR
                    )
                )
            ),
           
            new UserLogin(username: 'rpandey',enabled: true, 
                password: 'rpandey', 
                person: new Person(name: 'Ram Pandey', 
                    userName : 'rpandey',
                    sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                    country:Country.INDIA,
                    zip:'160031', city:'Ahmedabad', state: 'Gujarat',
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1986'),preferredLanguage:Language.ENGLISH)
            ),
            new UserLogin(username: 'राहुल',enabled: true, 
                password: 'राहुल', 
                person: new Person(name: 'राहुल नंदा', 
                    userName : 'राहुल',
                    sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                    country:Country.INDIA,
                    zip:'260014', city:'लखनऊ', state: 'उत्तर प्रदेश',
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1992'),
                    preferredLanguage:Language.HINDI)
            ),
            
            new UserLogin(username: 'admin', enabled: true, 
                password: 'admin', 
                person: new Person (name: 'Administrator',
                    userName: 'admin')),
            
            new UserLogin(username: 'admin2', enabled: true, 
                password: 'admin2'
                // this tests UserLogin pre-insert to copy username into Person
                // , person: new Person (name: 'Administrator')
            ),
            new UserLogin(username: 'cronRanking', enabled: false, 
                password: 'cronRanking'
                // This cron job processes individual user rankings to create aggregate
                // ranking items for Organization, Course, etc.  
            )
        ]
               
     
        for (user in users) {
          
            log.trace "processing user person ${user.person} "
            // create the reverse link (at least for two users)
            // NOT WORKING user.person.userLogin = user
             
            if (!user.save()) { 
                user.errors.allErrors.each {error ->
                    log.debug "An error occured with ${user.username} $error"
                }
            } else {
            
                log.trace "load: starting ACL creations for ${user}"
            
                InitSpringSecurity.grantACL(user.person, user.username)
      
                for (account in user.person.accounts) {
                    InitSpringSecurity.grantACL (account, user.username)
                }
                log.info "  loaded ${UserLogin.findByUsername(user.username).person.accounts?.size()} accounts" 
                
                for (personRole in user.person.personRoles) {
                    InitSpringSecurity.grantACL (personRole, user.username)
                }
                log.info "  loaded ${UserLogin.findByUsername(user.username).person.personRoles?.size()} personRoles"

                for (comment in user.person.comments) {
                    InitSpringSecurity.grantACL (comment, user.username)
                }
                
                for (capability in user.person.capabilitys) {
                    InitSpringSecurity.grantACL (capability, user.username)
                    for (standardizedTest in capability.standardizedTests) {
                        InitSpringSecurity.grantACL (standardizedTest, user.username)
                    }
                    log.info "  loaded ${Requirement.findById(capability.id).standardizedTests?.size()} person capability standardized Tests"
                }
                log.info "  loaded ${UserLogin.findByUsername(user.username).person.comments?.size()} comments" 
                log.info "  loaded ${UserLogin.findByUsername(user.username).person.capabilitys?.size()} person capabilitys"
                log.debug "created user ${user.username}"
            }
        }
    
        log.info ("load: loaded ${UserLogin.count()} out of ${users.size()} users")
        
        // give separate ROLES to some users for testing
 
        // admin get admin role
        def admin = UserLogin.findByUsername('admin')
    
        // admin2 picked up in preinsert of UserLogin
     
        if (!(new UserLoginSecurityGroup(userLogin:admin, securityGroup:
                    SecurityGroup.findByName('all_admin')).save(flush:true))) {
            log.warn "ULSG admin not saved"
        }
        
        def jfields = UserLogin.findByUsername('jfields')
        for (name in ['rpandey']) {
           
            if (!(new UserLoginSecurityGroup(userLogin:
                        UserLogin.findByUsername(name), securityGroup:
                        SecurityGroup.findByName('all_power_users')).save(flush:true))) {
                log.warn "us $user not saved for all_power_users"
            } else
            log.trace "enrolled $name for all_power_users"
        }
        
        for (name in ['jfields', 'mjohns', 'jsmith']) {
           
            if (!(new UserLoginSecurityGroup(userLogin:
                        UserLogin.findByUsername(name), securityGroup:
                        SecurityGroup.findByName('all_content_creators')).save(flush:true))) {
                log.warn "us $user not saved for all_content_creators"
            } else
            log.trace "enrolled $name for all_content_creators"
        }

        for (name in ['jfields', 'mjohns']) {
            if (!(new UserLoginSecurityGroup(userLogin:
                        UserLogin.findByUsername(name),securityGroup:
                        SecurityGroup.findByName('all_managers')).save(flush:true))) {
                log.warn "user $name not saved in ULSG for all_managers"
            } else
            log.trace "enrolled $name for all_managers"
            
        }
        
        for (name in ['jfields', 'cronRanking']) {
            if (!(new UserLoginSecurityGroup(userLogin:
                        UserLogin.findByUsername(name),securityGroup:
                        SecurityGroup.findByName('all_read_all')).save(flush:true))) {
                log.warn "user $name not saved in ULSG for all_read_all"
            } else
            log.trace "enrolled $name for all_read_all"
            
        }
       
                  
    }
        

    // will be used from other places
    
 
}