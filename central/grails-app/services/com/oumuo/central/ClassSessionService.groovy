package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class ClassSessionService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(ClassSession classSession, String username, int permission) {
        addPermission classSession, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#classSession, admin)")
    @Transactional
    void addPermission(ClassSession classSession, String username,
        Permission permission) {
        aclUtilService.addPermission classSession, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    ClassSession getNew(Map params) {
        def classSession = new ClassSession()
        
     
        // could reach it by any one of the following
        
        if (params.clazs)
        Clazs.findById(params.clazs?.id).addToClassSessions(classSession)
        else 
        log.warn "getNew: clasz is null in $params\
"
        log.trace "getNew: new classSession $classSession instance created"
        classSession
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    ClassSession create(Map params) {
        ClassSession classSession = new ClassSession(params)
        if (!classSession.save(flush:true)) {
            classSession.errors.allErrors.each {
                log.warning ("create: error while saving classSession ${classSession}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission classSession, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission classSession, 'admin',
        BasePermission.READ
        
        classSession
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.ClassSession', read) or hasPermission(#id, 'com.oumuo.central.ClassSession', admin) or hasRole('ROLE_USER')")
    ClassSession get(long id) {
        ClassSession.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<ClassSession> list(Map params) {
        ClassSession.list()
    }

    int count() {
        ClassSession.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#classSession, write) or hasPermission(#classSession, admin)")
    void update(classSession, Map params) {
        
        log.trace "udpate: before binding ${classSession}"    
        classSession.properties = params
        if (!classSession.save(flush:true)) {         
            classSession.errors.allErrors.each {
                log.warning ("create: error while saving classSession ${classSession}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#classSession, delete) or hasPermission(#classSession, admin)")
    void delete(ClassSession classSession) {
        classSession.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl classSession
    }

    @Transactional
    @PreAuthorize("hasPermission(#classSession, admin)")
    void deletePermission(ClassSession classSession, String username, Permission permission) {
        def acl = aclUtilService.readAcl(classSession)

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