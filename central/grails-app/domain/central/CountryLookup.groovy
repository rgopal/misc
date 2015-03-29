package central

class CountryLookup {
    String countryCode
    String countryName
    String capital
    String currencyCode
    String currency
    
    String comment
    
    Date dateCreated
    Date lastUpdated

    String toString () {
        "$name"
    }
    static constraints = {
        countryCode (size:2..2)
        countryName (size:1..50)
        capital(nullable:true)
        currencyCode(size:3..3, nullable:true)
        currency(nullable:true)
        comment (maxSize:1000, nullable:true)
        dateCreated()
        lastUpdated()
    }
}
