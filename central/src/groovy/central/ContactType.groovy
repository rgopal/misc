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
public enum ContactType implements org.springframework.context.MessageSourceResolvable {
 
    WORK, HOME, SOCIAL, WORK2, FAX, CHARITY, PROFESSIONAL, BUSINESS, DEFAULT
    public Object[] getArguments() { [] as Object[] }
    public String[] getCodes() { [ name() ] }
    public String getDefaultMessage() { name() }    // default is name itself
}

