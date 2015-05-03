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
   enum AcademicStratum implements org.springframework.context.MessageSourceResolvable {
 
        UNIVERSITY, COLLEGE, ASSOCIATE, HIGH, MIDDLE, ELEMENTARY, JUNIOR_HIGH, SECONDARY, HIGHER_SECONDARY, OTHER
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

