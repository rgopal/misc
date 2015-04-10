package com.oumuo
import central.Person

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
        
    // bidrectional by using belongsTo in Person
        
    Person person

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        password blank: false
        email(nullable:true, email:true)
        person(unique:true)
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
        // default s2 UI does not have person
        if (!person) {
            person = new Person (name:username)
        }
                
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
