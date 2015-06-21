/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.lookup

/**
 *
 * @author gopal
 */
public enum State implements org.springframework.context.MessageSourceResolvable {
 
    PLANNED, STARTED, IN_USE, PAUSED, ENDED, ENROLLING, CLOSED
    public Object[] getArguments() { [] as Object[] }
    public String[] getCodes() { [ name() ] }
    public String getDefaultMessage() { name() }    // default is name itself
}
	