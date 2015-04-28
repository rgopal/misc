package central

import central.Account

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class AccountService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Account account, String username, int permission) {
        addPermission account, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#account, admin)")
    @Transactional
    void addPermission(Account account, String username,
        Permission permission) {
        aclUtilService.addPermission account, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // person is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    Account getNew(Map params) {
        def account = new Account()
        account.person = Person.findById(params.person.id)
        if (!account.person) {
            account.errors.allErrors.each {
                log.warning ("create: error while getting new account ${account}: ${error}")
            }
        }
        account
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Account create(Map params) {
        Account account = new Account(params)
        if (!account.save()) {
            account.errors.allErrors.each {
                log.warning ("create: error while saving account ${account}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission account, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION

        account
    }

    @PreAuthorize("hasPermission(#id, 'central.Account', read) or hasPermission(#id, 'central.Account', admin)")
    Account get(long id) {
        Account.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)")
    List<Account> list(Map params) {
        Account.list()
    }

    int count() {
        Account.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#account, write) or hasPermission(#account, admin)")
    void update(account, Map params) {
        
        log.trace "udpate: before binding ${account}"    
        account.properties = params
        if (!account.save()) {         
            account.errors.allErrors.each {
                log.warning ("create: error while saving account ${account}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#account, delete) or hasPermission(#account, admin)")
    void delete(Account account) {
        account.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl account
    }

    @Transactional
    @PreAuthorize("hasPermission(#account, admin)")
    void deletePermission(Account account, String username, Permission permission) {
        def acl = aclUtilService.readAcl(account)

        // Remove all permissions associated with this particular
        // recipient (string equality to KISS)
        acl.entries.eachWithIndex { entry, i ->
            if (entry.sid.equals(recipient) &&
                entry.permission.equals(permission)) {
                acl.deleteAce i
            }
        }

        aclService.updateAcl acl
    }
}