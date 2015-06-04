package com.oumuo.central

import com.oumuo.central.Catalog

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class CatalogInstanceService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Catalog catalog, String username, int permission) {
        addPermission catalog, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#catalog, admin)")
    @Transactional
    void addPermission(Catalog catalog, String username,
        Permission permission) {
        aclUtilService.addPermission catalog, username, permission
    }

    // A catalog of courses will be created for a program so that will be non
    // null.   A catalog is a hiearchical structure with self references.  When
    // a new instance is created then it gets its sequence based on parent and
    // older siblings and the owner is the person creating it.  Possible that
    // root created by one person and children by others.
    
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Catalog getNew(Map params) {
     
        def catalog = new Catalog()
        
        // first find the person who is authoring the catalog
        catalog.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (not using belongsTo for flexibility)
        if (!catalog.person) {
            catalog.errors.allErrors.each {
                log.warning ("create: error while getting new catalog ${catalog}: ${it}")
            }
        } else
            log.trace "getNew: creating new catalog for $catalog.person"
        
        // check if it was created against an existin catalog (becomes parent)  
        catalog.parentCatalog = Catalog.findById(params.catalog?.id)
        
        if (!catalog.parentCatalog) {
         
     
            catalog.program = Program.findById(params.program?.id)
             // start a new root TODO find the current number for each program       
         
            if (!catalog.program) {
                   catalog.sequence = "1"
            } else
            {
                catalog.sequence = catalog.program.catalogs.size() + 1
            }
            log.trace "getNew: root catalog $catalog is created for a org $catalog.program "
            
        } else {
            // not a root so get the sequence from other siblings and program
            def location = catalog.parentCatalog.subCatalogs ? catalog.parentCatalog.subCatalogs.size()+ 1 : 1
            catalog.sequence = catalog.parentCatalog.sequence + "." + location.toString()
            catalog.program = catalog.parentCatalog.program  // for allCatalogs to work
            log.trace "getNew: $catalog sequence $catalog.sequence is subCatalog of $catalog.parentCatalog"
        }
        catalog
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    Catalog create(Map params) {
        Catalog catalog = new Catalog(params)
        if (!catalog.save(flush:true)) {
            catalog.errors.allErrors.each {
                log.warning ("create: error while saving catalog ${catalog}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission catalog, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission catalog, 'admin',
        BasePermission.READ
        
        catalog
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Catalog', read) or hasPermission(#id, 'com.oumuo.central.Catalog', admin) or hasRole('ROLE_USER')")
    Catalog get(long id) {
        Catalog.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Catalog> list(Map params) {
        Catalog.list()
    }

    int count() {
        Catalog.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#catalog, write) or hasPermission(#catalog, admin)")
    void update(catalog, Map params) {
        
        log.trace "udpate: before binding ${catalog}"    
        catalog.properties = params
        if (!catalog.save(flush:true)) {         
            catalog.errors.allErrors.each {
                log.warning ("create: error while saving catalog ${catalog}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#catalog, delete) or hasPermission(#catalog, admin)")
    void delete(Catalog catalog) {
        catalog.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl catalog
    }

    @Transactional
    @PreAuthorize("hasPermission(#catalog, admin)")
    void deletePermission(Catalog catalog, String username, Permission permission) {
        def acl = aclUtilService.readAcl(catalog)

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