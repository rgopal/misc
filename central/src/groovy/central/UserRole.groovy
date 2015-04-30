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
  enum UserRole implements org.springframework.context.MessageSourceResolvable {
      
    // these names have to have ROLE_ because the plugin expects it (not spring security)
 
        ROLE_ADMIN, ROLE_USER, ROLE_POWER_USER, ROLE_GUEST, ROLE_STUDENT, ROLE_TEACHER, ROLE_WRITER, ROLE_MANAGER
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

