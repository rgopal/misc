package central

import central.Person

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Person person, String username, int permission) {
        addPermission person, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#person, admin)")
    @Transactional
    void addPermission(Person person, String username,
        Permission permission) {
        aclUtilService.addPermission person, username, permission
    }

    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Person create(Map params) {
        Person person = new Person(params)
        if (!person.save()) {
            person.errors.allErrors.each {
                log.warning ("create: error while saving person ${person}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission person, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION

        person
    }

    @PreAuthorize("hasPermission(#id, 'central.Person', read) or hasPermission(#id, 'central.Person', admin)")
    Person get(long id) {
        Person.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)")
    List<Person> list(Map params) {
        Person.list()
    }

    int count() {
        Person.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#person, write) or hasPermission(#person, admin)")
    void update(person, Map params) {
        
        log.trace "udpate: before binding ${person}"    
        person.properties = params
        if (!person.save()) {         
            person.errors.allErrors.each {
                log.warning ("create: error while saving person ${person}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#person, delete) or hasPermission(#person, admin)")
    void delete(Person person) {
        person.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl person
    }

    @Transactional
    @PreAuthorize("hasPermission(#person, admin)")
    void deletePermission(Person person, String username, Permission permission) {
        def acl = aclUtilService.readAcl(person)

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