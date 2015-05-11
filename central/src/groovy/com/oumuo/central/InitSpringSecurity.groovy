/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central
import com.oumuo.lookup.UserRole as ROLE


import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION
import static org.springframework.security.acls.domain.BasePermission.DELETE
import static org.springframework.security.acls.domain.BasePermission.READ
import static org.springframework.security.acls.domain.BasePermission.WRITE

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils


import org.springframework.security.core.context.SecurityContextHolder as SCH

import com.oumuo.lookup.*
import com.oumuo.*



import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitSpringSecurity {
    
    // def sessionFactory
    // def springSecurityService
    static aclService  
    static aclUtilService
    static objectIdentityRetrievalStrategy
    
   
    void load (Object aclUtilSer, Object aclSer, Object objectIdentityRetrievalStr) {
     
        // they can't be injected in a groovy class so get them from bootstrap
        aclService = aclSer
        aclUtilService = aclUtilSer
        objectIdentityRetrievalStrategy = objectIdentityRetrievalStr
        
        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"

        def roles = []
        for (ROLE r: ROLE.values()) {
            roles << new Authority(authority: r.name())
        
        }
            
        for (role in roles) {
            
            if (!role.save(flush:true)){ role.errors.allErrors.each {error ->
                    log.warn "An error occured with role: ${role.authority}"

                }
            } else
            log.info "created role ${role.authority}"
        }
        
        def userRole = Authority.findByAuthority(ROLE.ROLE_USER.name())
        def adminRole = Authority.findByAuthority(ROLE.ROLE_ADMIN.name())
        def powerUserRole =  Authority.findByAuthority(ROLE.ROLE_POWER_USER.name())
        def managerRole =  Authority.findByAuthority(ROLE.ROLE_MANAGER.name())
        def contentCreatorRole =  Authority.findByAuthority(ROLE.ROLE_CONTENT_CREATOR.name())
        def readAllRole =  Authority.findByAuthority(ROLE.ROLE_READ_ALL.name())
       
        if (!(new SecurityGroup (name: 'all_admin').save())) {
            log.warn "load: all_admin securityGroup not saved"
        }
        
        if (!(new SecurityGroup (name: 'all_users').save())) {
            log.warn "load: all_users securityGroup not saved"
        }
       
        if (!(new SecurityGroup (name: 'all_power_users').save())) {
            log.warn "load: all_power_users securityGroup not saved"
        }
        if (!(new SecurityGroup (name: 'all_managers').save())) {
            log.warn "load: all_managers securityGroup not saved"
        }
        if (!(new SecurityGroup (name: 'all_content_creators').save())) {
            log.warn "load: all_content_creators securityGroup not saved"
        }
        if (!(new SecurityGroup (name: 'all_read_all').save())) {
            log.warn "load: all_read_all securityGroup not saved"
        }
     
        // load all security group authority records
        
        if (!(new SecurityGroupAuthority(securityGroup:
                        SecurityGroup.findByName('all_admin'), authority:adminRole).save())) {
            log.warn "SecurityGroupAuthority adminRole not saved"
        }  
        if (!(new SecurityGroupAuthority(securityGroup:
                       SecurityGroup.findByName('all_users'), authority:userRole).save())) {
            log.warn "SecurityGroupAuthority userRole not saved"
        }
        
        if (!(new SecurityGroupAuthority(securityGroup:
                    SecurityGroup.findByName('all_power_users'), authority:powerUserRole).save())) {
            log.warn "SecurityGroupAuthority powerUserRole not saved"
        }
      
        
        if (!(new SecurityGroupAuthority(securityGroup:
                        SecurityGroup.findByName('all_managers'), authority:managerRole).save())) {
            log.warn "SecurityGroupAuthority managerRole not saved"
        }
        
  
        if (!(new SecurityGroupAuthority(securityGroup:
                        SecurityGroup.findByName('all_content_creators'), authority:contentCreatorRole).save())) {
            log.warn "SecurityGroupAuthority contentCreatorRole not saved"
        }
        
        if (!(new SecurityGroupAuthority(securityGroup:
                    SecurityGroup.findByName('all_read_all'), authority:readAllRole).save())) {
            log.warn "SecurityGroupAuthority readAllRole not saved"
        }
        
              
    }
        

    // will be used from other places
    
    static void grantACL (item, username) {
        // create for user.person
        log.trace "grantACL: for object $item and username $username"
        
        if (!objectIdentityRetrievalStrategy.getObjectIdentity(item)) {
            aclService.createAcl(
                objectIdentityRetrievalStrategy.getObjectIdentity(item))
        }  else
           "grantACL: objectIdentity already exists for $item"
        
        // with ADMIN, all read, delete, update witll be granted
        aclUtilService.addPermission item, username, ADMINISTRATION
           
        // admin should be able to read everything (but not accidentally delete)
        aclUtilService.addPermission item, 'admin', READ
            
        // onwer can give privileges to others??
        aclUtilService.changeOwner item, username
           

    }
    }