package central

import central.Staffing

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class StaffingService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Staffing staffing, String username, int permission) {
        addPermission staffing, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#staffing, admin)")
    @Transactional
    void addPermission(Staffing staffing, String username,
        Permission permission) {
        aclUtilService.addPermission staffing, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    Staffing getNew(Map params) {
        def staffing = new Staffing()
        staffing.organization = Organization.findById(params.organization.id)
        if (!staffing.organization) {
            staffing.errors.allErrors.each {
                log.warning ("create: error while getting new staffing ${staffing}: ${error}")
            }
        }
        staffing
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    Staffing create(Map params) {
        Staffing staffing = new Staffing(params)
        if (!staffing.save(flush:true)) {
            staffing.errors.allErrors.each {
                log.warning ("create: error while saving staffing ${staffing}: ${error}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission staffing, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION

        staffing
    }

    @PreAuthorize("hasPermission(#id, 'central.Staffing', read) or hasPermission(#id, 'central.Staffing', admin)")
    Staffing get(long id) {
        Staffing.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)")
    List<Staffing> list(Map params) {
        Staffing.list()
    }

    int count() {
        Staffing.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#staffing, write) or hasPermission(#staffing, admin)")
    void update(staffing, Map params) {
        
        log.trace "udpate: before binding ${staffing}"    
        staffing.properties = params
        if (!staffing.save(flush:true)) {         
            staffing.errors.allErrors.each {
                log.warning ("create: error while saving staffing ${staffing}: ${error}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#staffing, delete) or hasPermission(#staffing, admin)")
    void delete(Staffing staffing) {
        staffing.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl staffing
    }

    @Transactional
    @PreAuthorize("hasPermission(#staffing, admin)")
    void deletePermission(Staffing staffing, String username, Permission permission) {
        def acl = aclUtilService.readAcl(staffing)

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