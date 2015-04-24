/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo


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

import central.Person
import central.Account
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
class InitSpringSecurity {
    
   // def sessionFactory
   // def springSecurityService
   // def aclService  (this is outside grails so not working???)
   //  def aclUtilService
   // def objectIdentityRetrievalStrategy
    
   
     void load (Object aclUtilService, Object aclService, Object objectIdentityRetrievalStrategy) {
     
        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList('ROLE_ADMIN'))
        log.trace "SCH ${SCH.context.authentication}"
        
        def roles = [
            new Authority(authority: 'ROLE_ADMIN'),     
            new Authority(authority: 'ROLE_USER'),
            new Authority(authority: 'ROLE_FINANCE'),
            new Authority(authority: 'ROLE_POWER_USER')]
    
        for (role in roles) {
            log.info "created role ${role.authority}"
            if (!role.save()){ role.errors.allErrors.each {error ->
                    log.debug "An error occured with role: ${role.authority}"

                }
            }
        }

        def users = [ 
            new UserLogin(username: 'jsmith', enabled: true, 
                password: 'jsmith', 
                person:  new Person(name: 'John Smith',
                    userName : 'jsmith',
                    sex:Person.Sex.MALE, race: Race.WHITE,
                    dateOfBirth:Date.parse('dd-MM-yyyy','01-09-1960'), 
                    preferredLanguage:Language.ENGLISH,
                    homeEmail:'john.smith@gmail.com'
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
                )
            ),
            new UserLogin(username: 'jfields',enabled: true, 
                password: 'jfields',  
                person:   new Person(name: 'Jane Fields',
                    userName : 'jfields',
                    sex:Person.Sex.FEMALE, race: Race.BLACK, 
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1980'),
                    preferredLanguage:Language.ENGLISH).
                    addToAccounts (
                    new Account(
                        email:'janefields@twitter.com', 
                        sequence:1, // sequence can't be null
                        name:'Secondary', main:false
                    )
                ).
                    addToAccounts(
                    // note that this is after (not sure if preInsert would work
                    new Account (
                        email:'janefields@yahoo.com',
                        sequence: 2,
                        main:true,
                        name:'Primary'
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
            )
        ]
        
        def userRole = Authority.findByAuthority('ROLE_USER')
        def adminRole = Authority.findByAuthority('ROLE_ADMIN')
        
        def sg = new SecurityGroup (name: 'all_users')
        if (!sg.save()) {
            log.warn "sg not saved ${sg}"
        }
        def sgad = new SecurityGroup(name:'all_admin') 
        if (!sgad.save()) {
            log.warn "sga not saved ${sgad}"
        }
        
        def sa = new SecurityGroupAuthority(securityGroup:sg, authority:userRole)
        if (!sa.save()) {
            log.warn "sa user not saved ${sa}"
        }
        
        def saad = new SecurityGroupAuthority(securityGroup:sgad, authority:adminRole)
        if (!saad.save()) {
            log.warn "sa admin not saved ${saad}"
        }
        
        // this is not used because of Group (Need to fix s2 UI) TODO
       
        for (user in users) {
          
            log.info "user person ${user.person} "
            // create the reverse link (at least for two users)
            // NOT WORKING user.person.userLogin = user
         
       
            
            if (!user.save()){ user.errors.allErrors.each {error ->
                    log.debug "An error occured with user: ${user.username}"

                }
            }
            
            log.trace "load: starting ACL creations for ${user} util ${aclUtilService} aclService ${aclService} ${objectIdentityRetrievalStrategy}"
            
            aclService.createAcl(
                objectIdentityRetrievalStrategy.getObjectIdentity(user))
           
            aclUtilService.addPermission user, 'admin', ADMINISTRATION
            
            aclUtilService.changeOwner user, user.username
               
           
            // not needed
            def u1 = new UserLoginAuthority (userLogin:user, authority:userRole)        
            if (!u1.save()) {
                log.warn "u1 not saved ${u1} for ${user}"
            }
            
            // all get all_users (but in afterInsert of UserLogin to take care
            // of users registering (was resulting in object already exists
            //  def us = new UserLoginSecurityGroup(userLogin:user, securityGroup:sg)
            // if (!us.save()) {
            //     log.warn "us user not saved ${user} for ${sg}"
            // }
        
         
        
            log.info "created user ${user.username}"
        }
        
       
        // admin get admin role
        def admin = UserLogin.findByUsername('admin')
        
        // not needed
        def u2 = new UserLoginAuthority (userLogin:admin, authority:adminRole)
       
        if (!u2.save()) {
            log.warn "u2 not saved ${u2}"
        }
    
       
        // admin2 picked up in preinsert of UserLogin
        def us = new UserLoginSecurityGroup(userLogin:admin, securityGroup:sgad)
        if (!us.save()) {
            log.warn "us admin not saved ${us} for ${sgad}"
        }
        
        processACL()
    }
    static void processACL () {
       
    }
    

}
