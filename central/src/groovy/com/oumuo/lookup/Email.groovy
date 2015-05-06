/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.lookup
import com.oumuo.lookup.Status
import com.oumuo.lookup.ContactType

/**
 *
 * @author gopal
 */
class Email {
   
    String email
    String provider
    // can't use primary as it clashes with SQL
    Boolean main = Boolean.FALSE
    ContactType contactType = ContactType.DEFAULT
 
    Status status = Status.ACTIVE

    String toString () {
        "$email"
   }
    static constraints = {
        email (email:true)
        provider (nullable:true)
        main()
        contactType ()
        status ()
      
    }
}