package central

class CountryLookup {
    String code
    String name
    String comment
    
    Date dateCreated
    Date lastUpdated

    String toString () {
        "$name"
    }
    static constraints = {
        code (size:2..2)
        name (size:1..50)
        comment (maxSize:1000, nullable:true)
        dateCreated()
        lastUpdated()
    }
}
