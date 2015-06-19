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
import org.codehaus.groovy.grails.commons.*


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
    
    // add this to individual domains as needed static notCloneable = ['quoteFlows','services']
 

    static Object deepClone(domainInstanceToClone) {

        //TODO: PRECISA ENTENDER ISSO! MB-249 no youtrack
        //Algumas classes chegam aqui com nome da classe + _$$_javassist_XX
        log.trace "deepClone for instance $domainInstanceToClone"
        if (domainInstanceToClone.getClass().name.contains("_javassist"))
        return null

        //Our target instance for the instance we want to clone
        // recursion
        def newDomainInstance = domainInstanceToClone.getClass().newInstance()

        //Returns a DefaultGrailsDomainClass (as interface GrailsDomainClass) for inspecting properties
        GrailsClass domainClass = domainInstanceToClone.domainClass.grailsApplication.getDomainClass(newDomainInstance.getClass().name)

        def notCloneable = domainClass.getPropertyValue("notCloneable")

        for(DefaultGrailsDomainClassProperty prop in domainClass?.getPersistentProperties()) {
            if (notCloneable && prop.name in notCloneable)
            continue

            log.trace "deepClone: property $prop.name association $prop.association"
            if (prop.association) {

                if (prop.owningSide) {
                    
                    //we have to deep clone owned associations
                    if (prop.oneToOne) {
                        log.trace "grantDeepACL: 0wningSide     oneToOne"
                        def newAssociationInstance = deepClone(domainInstanceToClone?."${prop.name}")
                        newDomainInstance."${prop.name}" = newAssociationInstance
                    } else {

                        domainInstanceToClone."${prop.name}".each { associationInstance ->
                            def newAssociationInstance = deepClone(associationInstance)

                            if (newAssociationInstance)
                            newDomainInstance."addTo${prop.name.capitalize()}"(newAssociationInstance)
                        }
                    }
                } else {

                    if (!prop.bidirectional) {

                        //If the association isn't owned or the owner, then we can just do a  shallow copy of the reference.
                        newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
                    }
                    // @@JR
                    // Yes bidirectional and not owning. E.g. clone Report, belongsTo Organisation which hasMany
                    // manyToOne. Just add to the owning objects collection.
                    else {
                        //println "${prop.owningSide} - ${prop.name} - ${prop.oneToMany}"
                        //return
                        if (prop.manyToOne) {
                            log.trace "deepClone: NOT 0wningSide Bidirectional"
                            newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
                            def owningInstance = domainInstanceToClone."${prop.name}"
                            // Need to find the collection.
                            String otherSide = prop.otherSide.name.capitalize()
                            //println otherSide
                            //owningInstance."addTo${otherSide}"(newDomainInstance)
                        }
                        else if (prop.manyToMany) {
                            //newDomainInstance."${prop.name}" = [] as Set

                            domainInstanceToClone."${prop.name}".each {

                                //newDomainInstance."${prop.name}".add(it)
                            }
                        }

                        else if (prop.oneToMany) {
                            log.trace "deepClone: NOT 0wningSide Bidirectional O2M"
                            domainInstanceToClone."${prop.name}".each { associationInstance ->
                                log.trace"deepClone:             $associationInstance"
                                def newAssociationInstance = deepClone(associationInstance)
                                newDomainInstance."addTo${prop.name.capitalize()}"(newAssociationInstance)
                            }
                        }
                    }
                }
            } else {
                //If the property isn't an association then simply copy the value
                newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"

                if (prop.name == "dateCreated" || prop.name == "lastUpdated") {
                    newDomainInstance."${prop.name}" = null
                }
            }
        }

        return newDomainInstance
    }
    static void grantDeepACL (domainInstanceToClone, user) {

        //TODO: PRECISA ENTENDER ISSO! MB-249 no youtrack
        //Algumas classes chegam aqui com nome da classe + _$$_javassist_XX
        log.trace "grantDeepACL for instance $domainInstanceToClone"
        
        if (domainInstanceToClone.getClass().name.contains("_javassist"))
        return

        grantACL (domainInstanceToClone, user)
        
        //Our target instance for the instance we want to deep ACL
        // recursion (newDomainInstance is not used
        def newDomainInstance = domainInstanceToClone.getClass().newInstance()

        //Returns a DefaultGrailsDomainClass (as interface GrailsDomainClass) for inspecting properties
        GrailsClass domainClass = domainInstanceToClone.domainClass.grailsApplication.getDomainClass(newDomainInstance.getClass().name)

        def notCloneable = domainClass.getPropertyValue("notCloneable")

        for(DefaultGrailsDomainClassProperty prop in domainClass?.getPersistentProperties()) {
            if (notCloneable && prop.name in notCloneable)
            continue

            log.trace "grantDeepACL: property $prop.name " 
            if (prop.association) {

                log.trace "grantDeepACL:      ${prop}"
                if (prop.owningSide) {
                    //we have to deep clone owned associations
                    if (prop.oneToOne) {
                        log.trace "grantDeepACL: 0wningSide     oneToOne"
                        grantDeepACL(domainInstanceToClone?."${prop.name}", user)
                       
                    } else {
                        log.trace "grantDeepACL:    owningSide NOT  oneToOne"
                        domainInstanceToClone."${prop.name}".each { associationInstance ->
                            grantDeepACL(associationInstance, user)

                        }
                    }
                } else {

                    if (!prop.bidirectional) {

                            log.trace "grantDeepACL: NOT 0wningSide NOT Bidirectional"
                        //If the association isn't owned or the owner, then can grant ACL.
                        grantACL(domainInstanceToClone."${prop.name}", user)
                    }
                    // @@JR
                    // Yes bidirectional and not owning. E.g. clone Report, belongsTo Organisation which hasMany
                    // manyToOne. Just add to the owning objects collection.
                    else {
                        //println "${prop.owningSide} - ${prop.name} - ${prop.oneToMany}"
                        //return
                        log.trace "grantDeepACL: NOT 0wningSide Bidirectional"
                        if (prop.manyToOne) {
                            
                                log.warn "grantDeepACL: NOT 0wningSide Bidirectional M21 NOOP"
                            newDomainInstance."${prop.name}" = domainInstanceToClone."${prop.name}"
                            def owningInstance = domainInstanceToClone."${prop.name}"
                            // Need to find the collection.
                            String otherSide = prop.otherSide.name.capitalize()
                            //println otherSide
                            //owningInstance."addTo${otherSide}"(newDomainInstance)
                        }
                        else if (prop.manyToMany) {
                            log.trace "grantDeepACL: NOT 0wningSide Bidirectional M2M"
                            //newDomainInstance."${prop.name}" = [] as Set

                            domainInstanceToClone."${prop.name}".each {

                                //newDomainInstance."${prop.name}".add(it)
                                log.warn ("deepACL: in manyToMany NOOP for $it")
                            }
                        }

                        else if (prop.oneToMany) {
                            log.trace "grantDeepACL: NOT 0wningSide Bidirectional O2M size " 
                           
                            domainInstanceToClone."${prop.name}".each { associationInstance ->
                                log.trace "grantDeepACL:              $associationInstance"
                                grantDeepACL(associationInstance, user)
                                
                            }
                        }
                    }
                }
            } 
        }

       
    }
}