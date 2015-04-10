package com.oumuo

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

	static transients = ['springSecurityService']

	static constraints = {
		username blank: false, unique: true
		password blank: false
                email(nullable:true, email:true)
	}

	static mapping = {
		password column: '`password`'
	}

	Set<SecurityGroup> getAuthorities() {
		UserLoginSecurityGroup.findAllByUserLogin(this).collect { it.securityGroup }
	}

	def beforeInsert() {
		encodePassword()
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
