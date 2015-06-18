package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class TermService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Term term, String username, int permission) {
        addPermission term, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#term, admin)")
    @Transactional
    void addPermission(Term term, String username,
        Permission permission) {
        aclUtilService.addPermission term, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Term getNew(Map params) {
        def term = new Term()
        
       /* // first find the person who is authoring the comment
        term.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!term.person) {
            term.errors.allErrors.each {
                log.warning ("create: error while getting new term ${term}: ${error}")
            }
        } else
        log.trace "getNew: creating new term for $term.person"
         
        */
       
        // don't care if it is null
        term.program = Program.findById(params.program?.id)
       
        
        log.trace "getNew: new term $term instance created"
        term
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Term create(Map params) {
        Term term = new Term(params)
        if (!term.save(flush:true)) {
            term.errors.allErrors.each {
                log.warning ("create: error while saving term ${term}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission term, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission term, 'admin',
        BasePermission.READ
        
        term
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Term', read) or hasPermission(#id, 'com.oumuo.central.Term', admin) or hasRole('ROLE_USER')")
    Term get(long id) {
        Term.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Term> list(Map params) {
        Term.list()
    }

    int count() {
        Term.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#term, write) or hasPermission(#term, admin)")
    void update(term, Map params) {
        
        log.trace "udpate: before binding ${term}"    
        term.properties = params
        if (!term.save(flush:true)) {         
            term.errors.allErrors.each {
                log.warning ("create: error while saving term ${term}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#term, delete) or hasPermission(#term, admin)")
    void delete(Term term) {
        term.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl term
    }

    @Transactional
    @PreAuthorize("hasPermission(#term, admin)")
    void deletePermission(Term term, String username, Permission permission) {
        def acl = aclUtilService.readAcl(term)

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