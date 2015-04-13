package com.oumuo
import central.Person
import groovy.util.logging.Log4j
@Log4j
class UserLogin {

    transient springSecurityService

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    // custom added 4/10/2015
    String email
        
    // bidrectional by using belongsTo in Person (which takes care of cascade)   
    // Person person leads to transient object (cascase explicitly needed)

    // static hasOne = [person:Person]
    Person person
    
    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        password blank: false
        email(nullable:true, email:true)
        person(nullable:true)
        // person(nullable:true)
        // this creates true one to one
    }

    static mapping = {
        password column: '`password`'
    }

    Set<SecurityGroup> getAuthorities() {
        UserLoginSecurityGroup.findAllByUserLogin(this).collect { it.securityGroup }
    }

    String toString(){

         "$username"
    }
    def beforeInsert() {
        encodePassword()
        log.trace ("before new: person ${person} username ${username}")
        // default s2 UI does not have person
        if (!person) {
            person = new Person (userName:username, name:username)
            person.userLogin = this
           if (!person.save()) {
               person.errors.allErrors.each {log.warn it}
           }
            // explicit save does not help (otherwise transient error)
            // person.save()
        }
        log.trace ("after new: person ${person} username ${username}")
                
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
        log.trace ("before update new: person ${person} username ${username}")
        // default s2 UI does not have person
        if (!person) {
            person = new Person (userName:username, name:username)
            person.userLogin = this
            person.save()
            // explicit save does not help (otherwise transient error)
            // person.save()
        }
        log.trace ("after update new: person ${person} username ${username}")
               
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
