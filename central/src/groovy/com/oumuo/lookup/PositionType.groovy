/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.lookup

/**
 *
 * @author rgopal-lt
 */
public enum PositionType implements org.springframework.context.MessageSourceResolvable {
 
    ENGINEER, INTERN, PROGRAMMER, ARCHITECT, RESEARCHER, TEACHER, ASSISTANT, MANAGER, STATISTICIAN, OTHER
    public Object[] getArguments() { [] as Object[] }
    public String[] getCodes() { [ name() ] }
    public String getDefaultMessage() { name() }    // default is name itself
}

