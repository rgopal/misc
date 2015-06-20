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


        def programs = [
            'Computer Science Diploma',
            'Computer Science Diploma',
            'Computer Science Diploma',
            'Computer Science Diploma',
            'Middle School English',
            'लखनऊ विश्वविद्यालय कला संकाय'
            
        ]
        
        def catalogs = [ 
            new Catalog(name: 'Computer Science Diploma Catalog',
                sequence: "1",         
                // null for top item course:Course.findByName('Computer Science I'),
                courseType:CourseType.REQUIRED
                
            ).addToSubCatalogs(
                new Catalog(name: 'Computer Science Diploma Catalog 1.1',
                    sequence: "1.1",
               
                    course:Course.findByName('Computer Science I'),
                    courseType:CourseType.REQUIRED
                
                )
            ).addToSubCatalogs(
                new Catalog(name: 'Computer Science Diploma Catalog 1.2',
                    sequence: "1.2",
               
                    course:Course.findByName('Computer Science II'),
                    courseType:CourseType.REQUIRED
                      
                )
            ),
          
            new Catalog(name: 'High School Physics Help Catalog',
                sequence: "2",
             
                course:Course.findByName('High School Physics'),
                courseType:CourseType.REQUIRED
              
            ),
            new Catalog(name: 'Middle School English Catalog',
                sequence: "3",
               
                course:Course.findByName('Middle School English Drama'),
                courseType:CourseType.REQUIRED
               
            ),
            new Catalog(name: 'लखनऊ विश्वविद्यालय कला संकाय Catalog',
                sequence: "4",
            
                course:Course.findByName('लखनऊ विश्वविद्यालय कला 1'),
                courseType:CourseType.REQUIRED
               
            )
        ]
        
       // assert (catalogs.size() - 2  == programs.size())
       
        // save all the catalogs and create ACLs
        def i = 0
        for (catalog in catalogs) {     
            log.trace "processing  catalog ${catalog} "
       
            log.trace catalog.properties.collect{it}.join('\n')
            def program = Program.findByName(programs[i])
            if (!program) {
                log.warn "load: could not find program $programs[i]"
                return
            }
                
            // now add catalog (so that GORM is happy.  Just back pointer from
            // catalog does not get retainend probably in this session (gsp was
            // working
            
            program.addToCatalogs(catalog)
            
            if (!program.save(flush:true)) { 
                catalog.errors.allErrors.each {error ->
                    log.warn "An error occured with $program ${catalog} $error"
                }
            } else {     
                // give permissions to one user
                for (user in ['jfields']) {
                    log.trace "   starting ACL creations for $user}"
                    InitSpringSecurity.grantACL(catalog, user)
                }
             
                for (subCatalog in catalog.subCatalogs) {
                    if (!subCatalog.save(flush:true)) { 
                        subCatalog.errors.allErrors.each {error ->
                            log.warn "An error occured with ${subCatalog} $error"
                        }
                    } else {     
                        // give permissions to two users
                        for (user in ['jfields']) {
                            log.trace "   starting ACL creations for $user"
                            InitSpringSecurity.grantACL(subCatalog, user)
                    
                        } 
                    }
                }
                
                log.debug "created Catalog ${catalog}"
            }
        }
    
        log.info ("load: loaded ${Catalog.count()} out of ${catalogs.size()} catalogs")
       
       i++ 
    }
    

}