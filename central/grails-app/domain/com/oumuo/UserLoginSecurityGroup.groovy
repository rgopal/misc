package com.oumuo
import groovy.util.logging.Log4j

import org.apache.commons.lang.builder.HashCodeBuilder

@Log4j
class UserLoginSecurityGroup implements Serializable {

    private static final long serialVersionUID = 1

    UserLogin userLogin
    SecurityGroup securityGroup

    boolean equals(other) {
        if (!(other instanceof UserLoginSecurityGroup)) {
            return false
        }

        other.userLogin?.id == userLogin?.id &&
        other.securityGroup?.id == securityGroup?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (userLogin) builder.append(userLogin.id)
        if (securityGroup) builder.append(securityGroup.id)
        builder.toHashCode()
    }

    static UserLoginSecurityGroup get(long userLoginId, long securityGroupId) {
        UserLoginSecurityGroup.where {
            userLogin == UserLogin.load(userLoginId) &&
            securityGroup == SecurityGroup.load(securityGroupId)
        }.get()
    }

    static boolean exists(long userLoginId, long securityGroupId) {
        UserLoginSecurityGroup.where {
            userLogin == UserLogin.load(userLoginId) &&
            securityGroup == SecurityGroup.load(securityGroupId)
        }.count() > 0
    }

    // this is perhaps a bug in UI.  Can't figure out so find security group first
    // for specific authority and then save it in the 
    /// line 106 of userController.groovy
    static UserLoginSecurityGroup create(UserLogin userLogin, Authority authority) {
        def securityGroup = SecurityGroupAuthority.findByAuthority(authority)
        if (!securityGroup) {
            println ("create (bug for s2ui): securityGroup ${securityGroup} for authority ${authority}")
        }
        def instance = new UserLoginSecurityGroup(userLogin: userLogin, securityGroup: securityGroup)
        instance.save(flush: true, insert: true)
        instance
    }
    
    
    // this is for the s2ui User Interface to work (for changing roles
    // take username, authority, and true
    // line 212 of grails-app controllers grails plugin springsecurity ui UserController.groovy
    static UserLoginSecurityGroup create(UserLogin userLogin, Authority authority, boolean ignore) {
        create(userLogin, authority)
    }
    static UserLoginSecurityGroup create(UserLogin userLogin, SecurityGroup securityGroup, boolean flush = false) {
        def instance = new UserLoginSecurityGroup(userLogin: userLogin, securityGroup: securityGroup)
        instance.save(flush: flush, insert: true)
        instance
    }

    static findUsersAuthority (role) {
        // there could be multiple securityGroups with the same role
        def securityGroups = SecurityGroupAuthority.findAllByAuthority(role)*.securityGroup
        def users = []
        for (securityGroup in securityGroups) {
            // println "findUsersAuthority finding users for $securityGroup"
            users << UserLoginSecurityGroup.findAllBySecurityGroup(securityGroup)*.userLogin
        }
        return users
    }
    static int countUsersAuthority(role) {
        
        return findUsersAuthority(role).size()
    }
    static boolean remove(UserLogin u, SecurityGroup g, boolean flush = false) {
        if (u == null || g == null) return false

        int rowCount = UserLoginSecurityGroup.where {
            userLogin == UserLogin.load(u.id) &&
            securityGroup == SecurityGroup.load(g.id)
        }.deleteAll()

        if (flush) { UserLoginSecurityGroup.withSession { it.flush() } }

        rowCount > 0
    }

    static void removeAll(UserLogin u, boolean flush = false) {
        if (u == null) return

        UserLoginSecurityGroup.where {
            userLogin == UserLogin.load(u.id)
        }.deleteAll()

        if (flush) { UserLoginSecurityGroup.withSession { it.flush() } }
    }

    static void removeAll(SecurityGroup g, boolean flush = false) {
        if (g == null) return

        UserLoginSecurityGroup.where {
            securityGroup == SecurityGroup.load(g.id)
        }.deleteAll()

        if (flush) { UserLoginSecurityGroup.withSession { it.flush() } }
    }

    static constraints = {
        userLogin validator: { UserLogin u, UserLoginSecurityGroup ug ->
            if (ug.securityGroup == null) return
            boolean existing = false
            UserLoginSecurityGroup.withNewSession {
                existing = UserLoginSecurityGroup.exists(u.id, ug.securityGroup.id)
            }
            if (existing) {
                return 'userGroup.exists'
            }
        }
    }

    static mapping = {
        id composite: ['securityGroup', 'userLogin']
        version false
    }
}
