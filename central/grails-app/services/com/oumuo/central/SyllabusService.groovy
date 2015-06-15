package com.oumuo.central

import com.oumuo.central.Syllabus

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class SyllabusService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Syllabus syllabus, String username, int permission) {
        addPermission syllabus, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#syllabus, admin)")
    @Transactional
    void addPermission(Syllabus syllabus, String username,
        Permission permission) {
        aclUtilService.addPermission syllabus, username, permission
    }

    // A syllabus of courses will be created for a program so that will be non
    // null.   A syllabus is a hiearchical structure with self references.  When
    // a new instance is created then it gets its sequence based on parent and
    // older siblings and the owner is the person creating it.  Possible that
    // root created by one person and children by others.
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Syllabus getNew(Map params) {
     
        def syllabus = new Syllabus()
        // check if it was created against an existin syllabus (becomes parent)  
        syllabus.parentSyllabus = Syllabus.findById(params.syllabus?.id)
      
        Syllabus.findById(params.syllabus?.id).addToSyllabuss(syllabus)
    
        if (!syllabus.parentSyllabus) {
             
            if (params.course)
            Course.findById(params.course?.id).addToSyllabuss(syllabus)
            // start a new root TODO find the current number for each program       
         
            if (!syllabus.course) {
                syllabus.sequence = "1"
            } else
            {
                syllabus.sequence = syllabus.course.syllabuss.size() + 1
            }
            log.trace "getNew: root syllabus $syllabus is created for a course $syllabus.course "
 
        } 
      
        else {
          
            // not a root so get the sequence from other siblings and program
            def location = syllabus.parentSyllabus.subSyllabuss ? syllabus.parentSyllabus.subSyllabuss.size()+ 1 : 1
            syllabus.sequence = syllabus.parentSyllabus.sequence + "." + location.toString()
            
            log.trace "getNew: syllabus sequence $syllabus.sequence is subSyllabus of $syllabus.parentSyllabus"
        }
        syllabus
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Syllabus create(Map params) {
        Syllabus syllabus = new Syllabus(params)
        if (!syllabus.save(flush:true)) {
            syllabus.errors.allErrors.each {
                log.warning ("create: error while saving syllabus ${syllabus}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission syllabus, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission syllabus, 'admin',
        BasePermission.READ
        
        syllabus
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Syllabus', read) or hasPermission(#id, 'com.oumuo.central.Syllabus', admin) or hasRole('ROLE_USER')")
    Syllabus get(long id) {
        Syllabus.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Syllabus> list(Map params) {
        Syllabus.list()
    }

    int count() {
        Syllabus.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#syllabus, write) or hasPermission(#syllabus, admin)")
    void update(syllabus, Map params) {
        
        log.trace "udpate: before binding ${syllabus}"    
        syllabus.properties = params
        if (!syllabus.save(flush:true)) {         
            syllabus.errors.allErrors.each {
                log.warning ("create: error while saving syllabus ${syllabus}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#syllabus, delete) or hasPermission(#syllabus, admin)")
    void delete(Syllabus syllabus) {
        syllabus.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl syllabus
    }

    @Transactional
    @PreAuthorize("hasPermission(#syllabus, admin)")
    void deletePermission(Syllabus syllabus, String username, Permission permission) {
        def acl = aclUtilService.readAcl(syllabus)

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