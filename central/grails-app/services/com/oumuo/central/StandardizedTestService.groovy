package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class StandardizedTestService {
 
    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(StandardizedTest standardizedTest, String username, int permission) {
        addPermission standardizedTest, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#standardizedTest, admin)")
    @Transactional
    void addPermission(StandardizedTest standardizedTest, String username,
        Permission permission) {
        aclUtilService.addPermission standardizedTest, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    StandardizedTest getNew(Map params) {
        def standardizedTest = new StandardizedTest()
        
       if (!params.courseRequirement) {
            standardizedTest.errors.allErrors.each {
                log.warning ("create: error while getting new StandardizedTest ${standardizedTest}: ${it}")
            }
        } else {
            standardizedTest.courseRequirement = CourseRequirement.findById(params.courseRequirement.id)
            log.trace "getNew: creating new standardizedTest for $standardizedTest"
        }
        
        
        log.trace "getNew: new standardizedTest $standardizedTest instance created"
        standardizedTest
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    StandardizedTest create(Map params) {
        StandardizedTest standardizedTest = new StandardizedTest(params)
        if (!standardizedTest.save(flush:true)) {
            standardizedTest.errors.allErrors.each {
                log.warning ("create: error while saving standardizedTest ${standardizedTest}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission standardizedTest, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission standardizedTest, 'admin',
        BasePermission.READ
        
        standardizedTest
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.StandardizedTest', read) or hasPermission(#id, 'com.oumuo.central.StandardizedTest', admin) or hasRole('ROLE_USER')")
    StandardizedTest get(long id) {
        StandardizedTest.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<StandardizedTest> list(Map params) {
        StandardizedTest.list()
    }

    int count() {
        StandardizedTest.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#standardizedTest, write) or hasPermission(#standardizedTest, admin)")
    void update(standardizedTest, Map params) {
        
        log.trace "udpate: before binding ${standardizedTest}"    
        standardizedTest.properties = params
        if (!standardizedTest.save(flush:true)) {         
            standardizedTest.errors.allErrors.each {
                log.warning ("create: error while saving standardizedTest ${standardizedTest}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#standardizedTest, delete) or hasPermission(#standardizedTest, admin)")
    void delete(StandardizedTest standardizedTest) {
        standardizedTest.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl standardizedTest
    }

    @Transactional
    @PreAuthorize("hasPermission(#standardizedTest, admin)")
    void deletePermission(StandardizedTest standardizedTest, String username, Permission permission) {
        def acl = aclUtilService.readAcl(standardizedTest)

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