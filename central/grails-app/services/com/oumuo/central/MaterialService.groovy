package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class MaterialService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Material material, String username, int permission) {
        addPermission material, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#material, admin)")
    @Transactional
    void addPermission(Material material, String username,
        Permission permission) {
        aclUtilService.addPermission material, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Material getNew(Map params) {
        def material = new Material()
        
      /*  // first find the person who is authoring the comment
        material.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        
        // this has to be non null (since comment belongsTo Person
        if (!material.person) {
            material.errors.allErrors.each {
                log.warning ("create: error while getting new material ${material}: ${it}")
            }
        } else
        log.trace "getNew: creating new material for $material.person"
        
        */
        Organization.findById(params.organization?.id).addToMaterials(material)
       
        
        log.trace "getNew: new material $material instance created"
        material
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Material create(Map params) {
        Material material = new Material(params)
        if (!material.save(flush:true)) {
            material.errors.allErrors.each {
                log.warning ("create: error while saving material ${material}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission material, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission material, 'admin',
        BasePermission.READ
        
        material
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Material', read) or hasPermission(#id, 'com.oumuo.central.Material', admin) or hasRole('ROLE_USER')")
    Material get(long id) {
        Material.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Material> list(Map params) {
        Material.list()
    }

    int count() {
        Material.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#material, write) or hasPermission(#material, admin)")
    void update(material, Map params) {
        
        log.trace "udpate: before binding ${material}"    
        material.properties = params
        if (!material.save(flush:true)) {         
            material.errors.allErrors.each {
                log.warning ("create: error while saving material ${material}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#material, delete) or hasPermission(#material, admin)")
    void delete(Material material) {
        material.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl material
    }

    @Transactional
    @PreAuthorize("hasPermission(#material, admin)")
    void deletePermission(Material material, String username, Permission permission) {
        def acl = aclUtilService.readAcl(material)

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