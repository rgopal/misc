package central

import central.LoginCommand
import groovy.util.logging.Log4j

@Log4j
class PersonController {

    def logout = {
        // println ("loging out ${person}")
        session.person = null
        
        redirect(url:resource(dir:'' ))
    }

  
    def register() {
       // does not work println "request ${request}"
        if(request.method == 'POST') {
            def u = new Person()
            u.properties['login', 'password', 'name'] = params
            if(u.password != params.confirm) {
                u.errors.rejectValue("password", "person.password.dontmatch")
                return [person:u]
            }
            u.save()
            session.person = u
            redirect controller:"person", action:"show", id:u.id
            
        } else if (request.method == 'GET') {
           // nothing exists yet return [person:u] 
           // this is when register is called for HTTP click, starts view register.gsp now
        }
        else if(u.save()) {
            session.person = u
            redirect controller:"person", action:"show", id:u.id
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
                render view:'/index', model:[loginCmd:cmd]
            }
        } else {
            render view:'/index'
        }
    }
    def scaffold = Person
} 
