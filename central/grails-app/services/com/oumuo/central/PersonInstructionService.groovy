package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class PersonInstructionService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(PersonInstruction personInstruction, String username, int permission) {
        addPermission personInstruction, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#personInstruction, admin)")
    @Transactional
    void addPermission(PersonInstruction personInstruction, String username,
        Permission permission) {
        aclUtilService.addPermission personInstruction, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonInstruction getNew(Map params) {
        def personInstruction = new PersonInstruction()
        
     
        // use addTo for person so that hibernate works fine.  Find  the logged
        // user
        
        def person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        if (!person) {
            person.errors.allErrors.each {
                log.warning ("create: error while getting new  ${personInstruction}: ${error}")
            }
        } else
        person.addToPersonInstructions(personInstruction)
        
        log.trace "getNew: creating new personInstruction for $person"
      
        
        if (params.instruction)
        Instruction.findById(params.instruction?.id).addToPersonInstructions(personInstruction)
        else
        log.warn "getNew: instruction should be non null"
    
        log.trace "getNew: new personInstruction $personInstruction instance created"
        personInstruction
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_USER')")
    PersonInstruction create(Map params) {
        PersonInstruction personInstruction = new PersonInstruction(params)
        if (!personInstruction.save(flush:true)) {
            personInstruction.errors.allErrors.each {
                log.warning ("create: error while saving personInstruction ${personInstruction}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission personInstruction, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission personInstruction, 'admin',
        BasePermission.READ
        
        personInstruction
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.PersonInstruction', read) or hasPermission(#id, 'com.oumuo.central.PersonInstruction', admin) or hasRole('ROLE_USER')")
    PersonInstruction get(long id) {
        PersonInstruction.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<PersonInstruction> list(Map params) {
        PersonInstruction.list()
    }

    int count() {
        PersonInstruction.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#personInstruction, write) or hasPermission(#personInstruction, admin)")
    void update(personInstruction, Map params) {
        
        log.trace "udpate: before binding ${personInstruction}"    
        personInstruction.properties = params
        if (!personInstruction.save(flush:true)) {         
            personInstruction.errors.allErrors.each {
                log.warning ("create: error while saving personInstruction ${personInstruction}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#personInstruction, delete) or hasPermission(#personInstruction, admin)")
    void delete(PersonInstruction personInstruction) {
        personInstruction.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl personInstruction
    }

    @Transactional
    @PreAuthorize("hasPermission(#personInstruction, admin)")
    void deletePermission(PersonInstruction personInstruction, String username, Permission permission) {
        def acl = aclUtilService.readAcl(personInstruction)

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