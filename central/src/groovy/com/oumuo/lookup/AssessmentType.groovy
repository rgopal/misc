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
   enum AssessmentType implements org.springframework.context.MessageSourceResolvable {
 
       EXAM, MID_TERM, FINAL_EXAM, TEST, QUIZ, ASSIGNMENT, PROJECT, PRESENTATION, PAPER, PARTICIPATION,  
        public Object[] getArguments() { [] as Object[] }
        public String[] getCodes() { [ name() ] }
        public String getDefaultMessage() { name() }    // default is name itself
    }

