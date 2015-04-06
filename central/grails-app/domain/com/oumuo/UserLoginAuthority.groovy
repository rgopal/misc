package com.oumuo

import org.apache.commons.lang.builder.HashCodeBuilder

class UserLoginAuthority implements Serializable {

	private static final long serialVersionUID = 1

	UserLogin userLogin
	Authority authority

	boolean equals(other) {
		if (!(other instanceof UserLoginAuthority)) {
			return false
		}

		other.userLogin?.id == userLogin?.id &&
		other.authority?.id == authority?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (userLogin) builder.append(userLogin.id)
		if (authority) builder.append(authority.id)
		builder.toHashCode()
	}

	static UserLoginAuthority get(long userLoginId, long authorityId) {
		UserLoginAuthority.where {
			userLogin == UserLogin.load(userLoginId) &&
			authority == Authority.load(authorityId)
		}.get()
	}

	static boolean exists(long userLoginId, long authorityId) {
		UserLoginAuthority.where {
			userLogin == UserLogin.load(userLoginId) &&
			authority == Authority.load(authorityId)
		}.count() > 0
	}

	static UserLoginAuthority create(UserLogin userLogin, Authority authority, boolean flush = false) {
		def instance = new UserLoginAuthority(userLogin: userLogin, authority: authority)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(UserLogin u, Authority r, boolean flush = false) {
		if (u == null || r == null) return false

		int rowCount = UserLoginAuthority.where {
			userLogin == UserLogin.load(u.id) &&
			authority == Authority.load(r.id)
		}.deleteAll()

		if (flush) { UserLoginAuthority.withSession { it.flush() } }

		rowCount > 0
	}

	static void removeAll(UserLogin u, boolean flush = false) {
		if (u == null) return

		UserLoginAuthority.where {
			userLogin == UserLogin.load(u.id)
		}.deleteAll()

		if (flush) { UserLoginAuthority.withSession { it.flush() } }
	}

	static void removeAll(Authority r, boolean flush = false) {
		if (r == null) return

		UserLoginAuthority.where {
			authority == Authority.load(r.id)
		}.deleteAll()

		if (flush) { UserLoginAuthority.withSession { it.flush() } }
	}

	static constraints = {
		authority validator: { Authority r, UserLoginAuthority ur ->
			if (ur.userLogin == null) return
			boolean existing = false
			UserLoginAuthority.withNewSession {
				existing = UserLoginAuthority.exists(ur.userLogin.id, r.id)
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		id composite: ['authority', 'userLogin']
		version false
	}
}
