/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central
import com.oumuo.central.Person

import grails.validation.Validateable

/**
 *
 * @author rgopal-lt
 */
@Validateable
class LoginCommand {
 
    String login
    String password
    private u
    Person getPerson() {
        if(!u && login) {
            // u = Person.findByLogin(login, [fetch:[purchasedSongs:'join']])
            u = Person.findByLogin(login)
        }
        return u
    }
    
 
    static constraints = {
        login blank:false, validator:{ val, obj ->
            if(!obj.person)
            return "person.not.found"
        }
        password blank:false, validator:{ val, obj ->
            if(obj.person && obj.person.password != val)
            return "user.password.invalid"
        }
    }
}

