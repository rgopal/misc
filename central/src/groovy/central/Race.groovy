/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central

/**
 *
 * @author rgopal-lt
 */
   enum Race implements org.springframework.context.MessageSourceResolvable {
 
        WHITE, BLACK, HISPANIC, ASIAN, NATIVE_INDIAN, ASIAN_INDIAN
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

