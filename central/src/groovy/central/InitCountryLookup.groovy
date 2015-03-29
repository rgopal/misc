
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central
import central.Person
import central.Language
import org.apache.commons.logging.LogFactory

import grails.compiler.GrailsCompileStatic
// 

class InitCountryLookup {
    // in domain and controller this is not needed

    
    static void load () {
        def grailsApplication = new Person().domainClass.grailsApplication
        def log = LogFactory.getLog("InitCountryLookup")
        log.debug ("InitCountryLookup.load started")
        
        // just .file probably would not work
        def filecsv = grailsApplication.mainContext.getResource('/WEB-INF/country.txt').getFile()
        // had to create inputStream (just inputStream did not work
        def inputStream = new FileInputStream(filecsv)
        inputStream.toCsvReader(['skipLines':1,   'charset':'UTF-8',    
                           'separatorChar':'|']).eachLine { tokens ->    
            //parse the csv columns
      

            CountryLookup c = new CountryLookup()
            c.capital = tokens[6].trim()
            c.currencyCode = tokens[7].trim()
            c.currency = tokens[8].trim()
            c.countryCode = tokens[10].trim()
            c.countryName = tokens[1].trim()

            if(!c.save(validate: true)){
                c.errors.allErrors.each {
                     println "An error occured with initCountryLookup: ${error}"
                }
            }
  
	
        }
    }
}