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
        
        def roles = [
            new Authority(authority: 'ROLE_ADMIN'),     
            new Authority(authority: 'ROLE_USER')]
    
        for (role in roles) {
            log.info "created role ${role}"
            if (!role.save()){ role.errors.allErrors.each {error ->
                    log.debug "An error occured with role: ${role}"

                }
            }
        }

        def users = [ 
            new UserLogin(username: 'user', password: 'admin'),
             new UserLogin(username: 'user2', password: 'admin'),
            new UserLogin(username: 'admin', password: 'admin')
        ]
        
        for (user in users) {
            log.info "created user ${user.username}"
            if (!user.save()){ user.errors.allErrors.each {error ->
                    log.debug "An error occured with user: ${user.username}"

                }
            }
        }
        
        def user = UserLogin.findByUsername('user')
        def admin = UserLogin.findByUsername('admin')
        def userRole = Authority.findByAuthority('ROLE_USER')
        def adminRole = Authority.findByAuthority('ROLE_ADMIN')
        
        UserLoginAuthority.create user, userRole, true
        UserLoginAuthority.create admin, adminRole, true
    
    }

}
