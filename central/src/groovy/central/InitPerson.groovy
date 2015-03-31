/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central
import central.Person
import central.Language
import central.Email

/**
 *
 * @author gopal
 */
class InitPerson {
    
    static void load () {
        def persons = [ 
            new Person(name: 'John Smith', sex:Person.Sex.MALE, race: Person.Race.WHITE, 
                dateOfBirth:Date.parse('dd-MM-yyyy','01-09-1960'), 
                preferredLanguage:Language.ENGLISH,
                homeEmail:new Email(email:'a@b.com', provider:'google')
            ),
           
            new Person(name: 'Mike Johns', sex:Person.Sex.MALE, race: Person.Race.WHITE, 
                dateOfBirth:Date.parse('dd-MM-yyyy','0-09-1970'),preferredLanguage:Language.ENGLISH),
            new Person(name: 'Jane Fields', sex:Person.Sex.FEMALE, race: Person.Race.BLACK, 
                dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1980'),preferredLanguage:Language.ENGLISH)
        ]
       
        for (person in persons) {
            if (!person.save()){ person.errors.allErrors.each {error ->
                    println "An error occured with person: ${error}"

                }
            }
       
        }
	
    }
}