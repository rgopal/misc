/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo

import central.Person
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
            new UserLogin(username: 'user', enabled: true, 
                password: 'user'),
            new UserLogin(username: 'user2',enabled: true, 
                password: 'user2'),
            new UserLogin(username: 'admin', enabled: true, 
                password: 'admin')
        ]
        
        for (user in users) {
            log.info "created user ${user.username}"
            if (!user.save()){ user.errors.allErrors.each {error ->
                    log.debug "An error occured with user: ${user.username}"

                }
            }
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
