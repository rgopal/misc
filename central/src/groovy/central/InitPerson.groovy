/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central
import central.Person
import central.Language
import central.Email
import central.Race

/**
 *
 * @author gopal
 */
class InitPerson {
    
    static void load () {
        def persons = [ 
            // this has Email embedded object which works here but GUI cannot save
            // The List GUI can display value saved here (but GUI destroys it)
            // TODO: fix the GUI for embedded
            new Person(name: 'John Smith', sex:Person.Sex.MALE, race: Race.WHITE, 
                dateOfBirth:Date.parse('dd-MM-yyyy','01-09-1960'), 
                preferredLanguage:Language.ENGLISH,
                // embedded does not work 
                // homeEmail:new Email(email:'John.Smith@gmail.com', provider:'google')
                homeEmail:'john.smith@gmail.com'
            ),
           
            new Person(name: 'Mike Johns', sex:Person.Sex.MALE, race: Race.WHITE, 
                dateOfBirth:Date.parse('dd-MM-yyyy','0-09-1970'),
                preferredLanguage:Language.ENGLISH,
                homeEmail:'Mike.Johns@gmail.com' ).addToAccounts( new Account(
                    email:'johnsmith@facebook.com', userName : 'John Smith again')
            ), 
            
            new Person(name: 'Jane Fields', sex:Person.Sex.FEMALE, race: Race.BLACK, 
                dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1980'),preferredLanguage:Language.ENGLISH),
            
            new Person(name: 'Ram Pandey', sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                country:Country.INDIA,
                zip:'160031', city:'Ahmedabad', state: 'Gujarat',
                dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1986'),preferredLanguage:Language.ENGLISH),
            
            new Person(name: 'राहुल नंदा', sex:Person.Sex.MALE, race: Race.ASIAN_INDIAN, 
                country:Country.INDIA,
                zip:'260014', city:'लखनऊ', state: 'उत्तर प्रदेश',
                dateOfBirth:Date.parse('dd-MM-yyyy','02-09-1992'),preferredLanguage:Language.HINDI)
        ]
       
        for (person in persons) {
            if (!person.save()){ person.errors.allErrors.each {error ->
                    println "An error occured with person: ${error}"

                }
            }
       
        }
	
    }
}