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
   enum AcademicLevel implements org.springframework.context.MessageSourceResolvable {
 
       K1, K2, K3, K4, K5, K6, K7, K8, K9, K10, K11, K12, AA1, AA2, COL1, COL2, COL3, COL4,
       GRAD1, GRAD2, GRAD3, GRAD4
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

