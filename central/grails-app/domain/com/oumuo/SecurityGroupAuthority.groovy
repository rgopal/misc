package com.oumuo

import org.apache.commons.lang.builder.HashCodeBuilder

class SecurityGroupAuthority implements Serializable {

	private static final long serialVersionUID = 1

	SecurityGroup securityGroup
	Authority authority

	boolean equals(other) {
		if (!(other instanceof SecurityGroupAuthority)) {
			return false
		}

		other.authority?.id == authority?.id &&
		other.securityGroup?.id == securityGroup?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (securityGroup) builder.append(securityGroup.id)
		if (authority) builder.append(authority.id)
		builder.toHashCode()
	}

	static SecurityGroupAuthority get(long securityGroupId, long authorityId) {
		SecurityGroupAuthority.where {
			securityGroup == SecurityGroup.load(securityGroupId) &&
			authority == Authority.load(authorityId)
		}.get()
	}

	static boolean exists(long securityGroupId, long authorityId) {
		SecurityGroupAuthority.where {
			securityGroup == SecurityGroup.load(securityGroupId) &&
			authority == Authority.load(authorityId)
		}.count() > 0
	}

	static SecurityGroupAuthority create(SecurityGroup securityGroup, Authority authority, boolean flush = false) {
		def instance = new SecurityGroupAuthority(securityGroup: securityGroup, authority: authority)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(SecurityGroup rg, Authority r, boolean flush = false) {
		if (rg == null || r == null) return false

		int rowCount = SecurityGroupAuthority.where {
			securityGroup == SecurityGroup.load(rg.id) &&
			authority == Authority.load(r.id)
		}.deleteAll()

		if (flush) { SecurityGroupAuthority.withSession { it.flush() } }

		rowCount > 0
	}

	static void removeAll(Authority r, boolean flush = false) {
		if (r == null) return

		SecurityGroupAuthority.where {
			authority == Authority.load(r.id)
		}.deleteAll()

		if (flush) { SecurityGroupAuthority.withSession { it.flush() } }
	}

	static void removeAll(SecurityGroup rg, boolean flush = false) {
		if (rg == null) return

		SecurityGroupAuthority.where {
			securityGroup == SecurityGroup.load(rg.id)
		}.deleteAll()

		if (flush) { SecurityGroupAuthority.withSession { it.flush() } }
	}

	static constraints = {
		authority validator: { Authority r, SecurityGroupAuthority rg ->
			if (rg.securityGroup == null) return
			boolean existing = false
			SecurityGroupAuthority.withNewSession {
				existing = SecurityGroupAuthority.exists(rg.securityGroup.id, r.id)
			}
			if (existing) {
				return 'roleGroup.exists'
			}
		}
	}

	static mapping = {
		id composite: ['securityGroup', 'authority']
		version false
	}
}
