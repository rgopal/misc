/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package central

/**
 *
 * @author gopal
 */
public enum Country implements org.springframework.context.MessageSourceResolvable {
 
    USA, INDIA, MEXICO
    public Object[] getArguments() { [] as Object[] }
    public String[] getCodes() { [ name() ] }
    public String getDefaultMessage() { name() }    // default is name itself
}