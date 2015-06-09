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
   enum StandardizedTestEnum implements org.springframework.context.MessageSourceResolvable {
 
       SAT, ACT, GRE, SAT_MATH, SAT_CS, SAT_CALCULUS, SAT_PHYSICS
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

