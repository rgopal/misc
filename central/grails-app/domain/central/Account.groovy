package central

class Account {
    String email 
    String userName

    String toString(){

    "$email"
    }
    static constraints = {
       
    email email: true, blank: false
    }
}
