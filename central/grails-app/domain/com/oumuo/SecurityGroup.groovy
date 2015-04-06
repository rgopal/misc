package com.oumuo

class SecurityGroup {

	String name

	static mapping = {
		cache true
	}

	Set<Authority> getAuthorities() {
		SecurityGroupAuthority.findAllBySecurityGroup(this).collect { it.authority }
	}

	static constraints = {
		name blank: false, unique: true
	}
}
