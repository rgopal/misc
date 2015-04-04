package central

import central.LoginCommand
import groovy.util.logging.Log4j

@Log4j
class PersonController {

    def logout = {
        // println ("loging out ${person}")
        session.person = null
        
        redirect(uri:'/')
    }
  
    def register() {
        log.debug "request ${request}"
        if(request.method == 'POST') {
            def u = new Person()
            def v
            u.properties['login', 'password', 'name'] = params
            // check if the passwords match
            if(u.password != params.confirm) {
                u.errors.rejectValue("password", "person.password.dontmatch")
                // three options (redirect, render, or return a model current view
                return [person:u]
            }
            
            // check if the login name already exists
            v = Person.findByLogin(u.login)
            if (v) {
                u.errors.rejectValue("login", "person.login.exists")
                return [person:u]
            }
           
             // a new user ready to be saved 
            if (!u.save()){ 
                u.errors.allErrors.each {error ->
                    log.debug "An error occured with u: ${error}"

                }
                u.errors.rejectValue("login", "person.save.error")     
                return [person:u]
            }
        
          
            session.person = u
            redirect controller:"person"
            
        } else if (request.method == 'GET') {
            // nothing exists yet return [person:u] 
            // this is when register is called for HTTP click, starts view register.gsp now
        }
        else if(u.save()) {
            log.debug "request $request} for u $u} apparently saved"
            session.person = u
            redirect controller:"person"
        } else {
            return [person:u]
        }
    }
    def login(LoginCommand cmd) {
        if(request.method == 'POST') {
            if(!cmd.hasErrors()) {
                session.person = cmd.getPerson()
                redirect controller:'person'
            } else {
                redirect(uri:'/')
            }
        } else {
            redirect(uri:'/')
        }
    }
    def scaffold = Person
} 
