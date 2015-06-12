package com.oumuo.central


import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission
import org.springframework.transaction.annotation.Transactional
import org.codehaus.groovy.grails.web.util.WebUtils
class RequirementService {

    def aclPermissionFactory
    def aclService
    def aclUtilService
    def springSecurityService

    void addPermission(Requirement requirement, String username, int permission) {
        addPermission requirement, username,
        aclPermissionFactory.buildFromMask(permission)
    }

    @PreAuthorize("hasPermission(#requirement, admin)")
    @Transactional
    void addPermission(Requirement requirement, String username,
        Permission permission) {
        aclUtilService.addPermission requirement, username, permission
    }

    // to handle edited many-to-one relation in renderTemplate.  Note here
    // organization is sort of hardcoded and needs to be handled for any such association
    
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Requirement getNew(Map params) {
        def requirement = new Requirement()
        
      

        def grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        // request is the HttpServletRequest
        def flash = grailsWebRequest.attributes.getFlashScope()
        // println Course.findById(params.cou.id)
        println "Requirements Service " + params.collect{it}.join('\n')
        println "Requirements Service " + flash.collect{it}.join('\n')
        println Course.findById(params.course.id).properties.collect{it}.join('\n')
        
        // used as requirement for course/program etc.
        // and capability for student/teacher
        
        //resolve teaching by changing GSP file and then set teaching
        // as the case may be (renderEditor had to be changed to add 
        // new OneToManyProperty to disambiguate 
        if (params.course) {
            if (params.oneToManyPropertyName == "teachingRequirements") {
                // requirement.teaching = Course.findById(params.course.id)
                Course.findById(params.course.id).addToTeachingRequirements(requirement)
            }
            else if (params.oneToManyPropertyName == "requirements") {
                // requirement.learning = Course.findById(params.course.id)
                Course.findById(params.course.id).addToRequirements(requirement)
            }
            else
            log.warn "getNew: $oneToManyPropertyName has to be either requirements or teachingRequirements"
            
            
            log.trace "getNew: creating new requirement for $params.oneToManyPropertyName"
        } else if (params.person) {
            requirement.person = Person.findById(params.person.id)
            log.trace "getNew: creating new requirement for $requirement.person"
        } else if (params.program) {
            requirement.program = Program.findById(params.program.id)
            log.trace "getNew: creating new requirement for $requirement.program"
        } 
        else {
            requirement.errors.allErrors.each {
                log.warning ("create: error while getting new requirement ${requirement}: ${it}")
            }
        }
        
        log.trace "getNew: new requirement $requirement instance created"
        requirement
    }
    // called from save of controller (with params returned from form)
    @Transactional
    @PreAuthorize("hasRole('ROLE_CONTENT_CREATOR')")
    Requirement create(Map params) {
        Requirement requirement = new Requirement(params)
        if (!requirement.save(flush:true)) {
            requirement.errors.allErrors.each {
                log.warning ("create: error while saving requirement ${requirement}: ${it}")
            }
        }
      

        // Grant the current principal administrative permission
        addPermission requirement, springSecurityService.authentication.name,
        BasePermission.ADMINISTRATION
        
        // also give permission to ADMIN

        addPermission requirement, 'admin',
        BasePermission.READ
        
        requirement
    }

    @PreAuthorize("hasPermission(#id, 'com.oumuo.central.Requirement', read) or hasPermission(#id, 'com.oumuo.central.Requirement', admin) or hasRole('ROLE_USER')")
    Requirement get(long id) {
        // TODO: for capability this should only be accessible to the user 
        // may be check for person and if it is non null then check for user or read_all
        Requirement.get id
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostFilter("hasPermission(filterObject, read) or hasPermission(filterObject, admin) or hasRole('ROLE_USER')")
    List<Requirement> list(Map params) {
        Requirement.list()
    }

    int count() {
        Requirement.count()
    }

    @Transactional
    @PreAuthorize("hasPermission(#requirement, write) or hasPermission(#requirement, admin)")
    void update(requirement, Map params) {
        
        log.trace "udpate: before binding ${requirement}"    
        requirement.properties = params
        if (!requirement.save(flush:true)) {         
            requirement.errors.allErrors.each {
                log.warning ("create: error while saving requirement ${requirement}: ${it}")
            }
        }
       
      
    }

    @Transactional
    @PreAuthorize("hasPermission(#requirement, delete) or hasPermission(#requirement, admin)")
    void delete(Requirement requirement) {
        requirement.delete()

        // Delete the ACL information as well
        aclUtilService.deleteAcl requirement
    }

    @Transactional
    @PreAuthorize("hasPermission(#requirement, admin)")
    void deletePermission(Requirement requirement, String username, Permission permission) {
        def acl = aclUtilService.readAcl(requirement)

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