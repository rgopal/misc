/*
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central
import central.Account
/**
 *
 * @author gopal
 */
class InitAccount {
    static void load () {
        for (int i=0; i<100; i++) {
            def a1 = new Account(email: "aim${i}@b.com", userName: "${i} Harry")

            if (!a1.save()){ a1.errors.allErrors.each {error ->
                    println "An error occured with a1: ${error}"

                }
            }
        } 
    }
	
}
