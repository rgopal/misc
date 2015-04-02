package central
import central.LoginCommand

class PersonController {

    def index () {}
    def register() {
        if(request.method == 'POST') {
            def u = new Person()
            u.properties['login', 'password', 'name'] = params
            if(u.password != params.confirm) {
                u.errors.rejectValue("password", "person.password.dontmatch")
                return [person:u]
            }
        } else if(u.save()) {
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
                render view:'/person/index', model:[loginCmd:cmd]
            }
        } else {
            render view:'/person/index'
        }
    }
    def scaffold = Person
} 
