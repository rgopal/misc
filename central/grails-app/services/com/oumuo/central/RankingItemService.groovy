package com.oumuo.central

import com.oumuo.central.RankingItem

import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional

class RankingItemService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(RankingItem rankingItem, String username, int permission) {
        addPermission rankingItem, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#rankingItem, admin)")
    @Transactional
    void addPermission(RankingItem rankingItem, String username,
        Permission permission) {
        aclUtilService.addPermission rankingItem, username, permission
    }
    
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    RankingItem getNew(Map params) {
     
        // this serves the purpose to drive a form but save again gets a new
        // after the form returns
        def rankingItem = new RankingItem()
        
        // first find the person who is authoring the rankingItem (has to be admin)
        rankingItem.person = Person.findByUserName (
            springSecurityService.authentication.name
        )
        
        // this has to be non null (not using belongsTo for flexibility)
        if (!rankingItem.person) {
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while getting new rankingItem ${rankingItem}: ${error}")
            }
        } else
        log.trace "getNew: creating new rankingItem for person $rankingItem.person"
        
        // do with Organization, Program, etc.
        
        rankingItem.organizationRanking = params.organization ? 
            Ranking.findById(params.ranking.id):null
        if (rankingItem.organizationRanking)
        log.trace ("getNew: found organizatoin $rankingItem.organization")
        
        rankingItem.programRanking = params.program ? 
            Ranking.findById(params.ranking.id):null
        if (rankingItem.programRanking)
        log.trace ("getNew: found program $rankingItem.program")
        
        rankingItem
      
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    RankingItem create(Map params) {
        RankingItem rankingItem = new RankingItem(params)
        if (!rankingItem.save(flush:true)) {
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while saving rankingItem ${rankingItem}: ${error}")
            }
        }
      
        // Grant the current principal administrative permission
        addPermission rankingItem, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission rankingItem, 'admin',
        BasePermission.READ
        
        rankingItem
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.RankingItem', read) or hasPermission(#id, 'com.oumuo.central.RankingItem', admin) or hasRole('ROLE_READ_ALL')")
    RankingItem get(long id) {
        RankingItem.get id
    }

    @PreAuthorize("hasRole('ROLE_READ_ALL')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin)  or hasRole('ROLE_READ_ALL')")
    List<RankingItem> list(Map params) {
        RankingItem.list()
    }

    int count() {
        RankingItem.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, write) or hasPermission(#rankingItem, admin)")
    void update(rankingItem, Map params) {
        
        log.trace "udpate: before binding ${rankingItem}"    
        rankingItem.properties = params
        if (!rankingItem.save(flush:true)) {         
            rankingItem.errors.allErrors.each {
                log.warning ("create: error while saving rankingItem ${rankingItem}: ${error}")
            }
        }
            
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, delete) or hasPermission(#rankingItem, admin)")
    void delete(RankingItem rankingItem) {
        rankingItem.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl rankingItem
    }

    @Transactional
    @PreAuthorize("hasPermission(#rankingItem, admin)")
    void deletePermission(RankingItem rankingItem, String username, Permission permission) {
        def acl = aclUtilService.readAcl(rankingItem)

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