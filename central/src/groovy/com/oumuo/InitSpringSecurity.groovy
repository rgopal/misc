/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo

import central.Person
import central.Account
import central.Language
import central.Email
import central.Race
import central.Country
import com.oumuo.UserLogin
import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitSpringSecurity {
    static void load () {
        def springSecurityService
        def roles = [
            new Authority(authority: 'ROLE_ADMIN'),     
            new Authority(authority: 'ROLE_USER')]
    
        for (role in roles) {
            log.info "created role ${role.authority}"
            if (!role.save()){ role.errors.allErrors.each {error ->
                    log.debug "An error occured with role: ${role.authority}"

                }
            }
        }

        def users = [ 
            new UserLogin(username: 'jsmith', enabled: true, 
                password: 'mjohns', person:  new Person(name: 'John Smith', 
                    sex:Person.Sex.MALE, race: Race.WHITE,
                    dateOfBirth:Date.parse('dd-MM-yyyy','01-09-1960'), 
                    preferredLanguage:Language.ENGLISH,
                    homeEmail:'john.smith@gmail.com'
                )
            ),
            new UserLogin(username: 'mjohnd',enabled: true, 
                password: 'mjohns'),
            new UserLogin(username: 'mjohns',enabled: true, 
                password: 'mjohns', person: new Person(name: 'Mike Johns', sex:Person.Sex.MALE, race: Race.WHITE, 
                    dateOfBirth:Date.parse('dd-MM-yyyy','0-09-1970'),
                    preferredLanguage:Language.ENGLISH,
                    homeEmail:'Mike.Johns@gmail.com' ).addToAccounts( new Account(
                        email:'johnsmith@facebook.com', userName : 'John Smith again',
                        sequence:1, name:'Primary', main:true
                    )
                )
            ),
            new UserLogin(username: 'jfields',enabled: true, 
                password: 'jfields',  person:   new Person(name: 'Jane Fields', 
                    sex:Person.Sex.FEMALE, race: Race.BLACK, 
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1980'),preferredLanguage:Language.ENGLISH)),
           
            new UserLogin(username: 'rpandey',enabled: true, 
                password: 'rpandey', person: new Person(name: 'Ram Pandey', sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                    country:Country.INDIA,
                    zip:'160031', city:'Ahmedabad', state: 'Gujarat',
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1986'),preferredLanguage:Language.ENGLISH)
            ),
            new UserLogin(username: 'राहुल',enabled: true, 
                password: 'राहुल', 
                person: new Person(name: 'राहुल नंदा', sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                    country:Country.INDIA,
                    login:'राहुल', password:'राहुल',
                    zip:'260014', city:'लखनऊ', state: 'उत्तर प्रदेश',
                    dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1992'),preferredLanguage:Language.HINDI)
            ),
            
            new UserLogin(username: 'admin', enabled: true, 
                password: 'admin', person: new Person (name: 'Administrator'))
        ]
        
        for (user in users) {
          
            log.info "user person ${user.person} "
            // create the reverse link (at least for two users)
           // NOT WORKING user.person.userLogin = user
         
            
            
            if (!user.save()){ user.errors.allErrors.each {error ->
                    log.debug "An error occured with user: ${user.username}"

                }
            }
            log.info "created user ${user.username}"
        }
        
        def user = UserLogin.findByUsername('user')
        def user2 = UserLogin.findByUsername('user2')
        
        def admin = UserLogin.findByUsername('admin')
        def userRole = Authority.findByAuthority('ROLE_USER')
        def adminRole = Authority.findByAuthority('ROLE_ADMIN')
        
        def u1 = new UserLoginAuthority (userLogin:user, authority:userRole)
        def u2 = new UserLoginAuthority (userLogin:admin, authority:adminRole)
        u1.save()
        u2.save()
     
        def sg = new SecurityGroup (name: 'all_users')
        sg.save()
        
        def sga = new SecurityGroup(name:'all_admin') 
        sga.save()
        
        def sa = new SecurityGroupAuthority(securityGroup:sg, authority:userRole)
        sa.save()
        
        sa = new SecurityGroupAuthority(securityGroup:sga, authority:adminRole)
        sa.save()
        
        def us = new UserLoginSecurityGroup(userLogin:user, securityGroup:sg)
        us.save()
        us = new UserLoginSecurityGroup(userLogin:user2, securityGroup:sg)
        us.save()
        
        us = new UserLoginSecurityGroup(userLogin:admin, securityGroup:sga)
        us.save()
        
        
    }

}
