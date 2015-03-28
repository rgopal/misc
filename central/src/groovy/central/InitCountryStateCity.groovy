/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central
import central.CountryStateCity
import central.Country

/**
 *
 * @author gopal
 */
class InitCountryStateCity {
       
    static void load () {
        def items = [ 
            new CountryStateCity(country:Country.USA, state:'Maryland', city:'Germantown'),
            new CountryStateCity(country:Country.USA, state:'Maryland', city:'Gaithersburg'),
            new CountryStateCity(country:Country.USA, state:'Maryland', city:'Rockville'),
            new CountryStateCity(country:Country.INDIA, state:'Gujarat', city:'Surat'),
            new CountryStateCity(country:Country.INDIA, state:'बिहार', city:'पटना', language:Language.HINDI),
            new CountryStateCity(country:Country.INDIA, state:'BIHAR', city:'PATNA', language:Language.ENGLISH),
            new CountryStateCity(country:Country.INDIA, state:'Andhra Pradesh', city:'Vishakapattanam')   
            
        ]
       
        for (item in items) {
            if (!item.save()){ item.errors.allErrors.each {error ->
                    println "An error occured with item: ${error}"

                }
            }
       
        }
	
    }
	
}

