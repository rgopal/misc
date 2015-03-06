package central

class Person {

    String name
    String country
    String city
    String state 
   
    Date dateCreated  
    Date dateUpdated
    static hasMany = [accounts: Account]

    // should be constraints and not constraint
    static constraints = {
       state(inList:['MD', 'VA', 'CA'])

    }
}
