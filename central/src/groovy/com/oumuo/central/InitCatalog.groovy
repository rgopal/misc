/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.central




import org.springframework.security.authentication. UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils


import org.springframework.security.core.context.SecurityContextHolder as SCH

import com.oumuo.central.Organization
import com.oumuo.central.Staffing
import com.oumuo.lookup.*
import com.oumuo.lookup.UserRole as ROLE


import org.apache.commons.logging.LogFactory
import groovy.util.logging.Log4j

/**
 *
 * @author rgopal-lt
 */
@Log4j
class InitCatalog {
    

   
    void load () {
     

        // have to be authenticated as an admin to create ACLs
        SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            'admin', 'admin',
            AuthorityUtils.createAuthorityList(ROLE.ROLE_ADMIN.name()))
        log.trace "SCH ${SCH.context.authentication}"


        def catalogs = [ 
            new Catalog(name: 'Computer Science Diploma Catalog',
                sequence: "1",
                person:Person.findByUserName('jfields'),
                course:Course.findByName('Computer Science I'),
                courseType:CourseType.REQUIRED,
                program: Program.
                    findByName('Computer Science Diploma')   
            ),
          
            new Catalog(name: 'High School Physics Help Catalog',
                sequence: "2",
                person:Person.findByUserName('jfields'),
                course:Course.findByName('High School Physics'),
                courseType:CourseType.REQUIRED,
                program: Program.
                    findByName('High School Physics Help')   
            ),
            new Catalog(name: 'Middle School English Catalog',
                sequence: "3",
                person:Person.findByUserName('jfields'),
                course:Course.findByName('Middle School English Drama'),
                courseType:CourseType.REQUIRED,
                program: Program.
                    findByName('Middle School English')   
            ),
            new Catalog(name: 'लखनऊ विश्वविद्यालय कला संकाय Catalog',
                sequence: "4",
                person:Person.findByUserName('jfields'),
                course:Course.findByName('लखनऊ विश्वविद्यालय कला 1'),
                courseType:CourseType.REQUIRED,
                program: Program.
                    findByName('लखनऊ विश्वविद्यालय कला संकाय')   
            )
        ]
        
       
        // save all the catalogs and create ACLs
        for (catalog in catalogs) {     
            log.trace "processing  catalog ${catalog} "
       
            log.trace catalog.properties.collect{it}.join('\n')
            
            if (!catalog.save()) { 
                catalog.errors.allErrors.each {error ->
                    log.warn "An error occured with ${catalog} $error"
                }
            } else {     
                // give permissions to one user
                for (user in ['jfields']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(catalog, user)
                }
             
                log.debug "created Catalog ${catalog}"
            }
        }
    
        log.info ("load: loaded ${Catalog.count()} out of ${catalogs.size()} catalogs")
       
        
    }
    

}