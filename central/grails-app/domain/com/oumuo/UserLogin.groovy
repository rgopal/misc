package com.oumuo
import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import central.Person
import groovy.util.logging.Log4j
@Log4j
class UserLogin {

   
    def aclUtilService
    def aclService
    def objectIdentityRetrievalStrategy
    def springSecurityService

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
   
            // call the service so that ACL is correctly done
            if (!person.save()) {
                person.errors.allErrors.each {log.warn it}
            }
            // explicit save does not help (otherwise transient error)
            // person.save()
        }
        log.trace ("after new: person ${person} username ${username}")
                
    }
    // every user added to all_users (registration).  Can't be done in preInsert
    // because id does not exist at that time (.exists(null,2) failed)
    def afterInsert() {
        log.trace ("afterInsert: Creating ULSG for username ${username}")
    
        def sg = SecurityGroup.findByName('all_users')
        if (sg) {
            log.trace "afterInsert: checking UserLogin $this} Security Group ${sg}"
            if (!UserLoginSecurityGroup.findByUserLoginAndSecurityGroup(this, sg)) {
                def us = new UserLoginSecurityGroup(userLogin:this, securityGroup:sg)
                   
                if (!us.save()) {
                    log.warn "afterInsert: UserLoginSecurityGroup not saved ${us}"
                }
                log.trace ("afterInsert: Created ULSG all_users for username ${username}")
            } else {
                log.trace("afterInsert: ULSG already exists for $this and $sg")
                
            }
            
        }
        else {
            log.warn "afterInsert: all_users not found in SecurityGroup for ${username}"
            
        }  
        // now store the ACL (but need to check if it already exists)
        // there is no user logged on when user registers
        if (!springSecurityService?.currentUser) {
            // note that when pre-creating users, SCH is used as admin
            SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
                AuthorityUtils.createAuthorityList('ROLE_ADMIN'))
        
            log.trace ("afterInsert: cu.u $springSecurityService.currentUser.username person $person username $username")
            
            aclService.createAcl(
                objectIdentityRetrievalStrategy.getObjectIdentity(person))
            
            aclUtilService.addPermission person, username, ADMINISTRATION
            aclUtilService.changeOwner person, username
             
        }
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
