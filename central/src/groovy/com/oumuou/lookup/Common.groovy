/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuou.lookup

/**
 *
 * @author rgopal-lt
 */
class Common {
    static int compareSequence(String s1, String s2) {
        if (s1.size() == 0 && s2.size() == 0)
        return 0
        else if (s1.size() == 0)
        return -1
        else if (s2.size() == 0)
        return 1
            
        // now find the first (before .) and remaing parts
        def s1Remaining = s1.split(/\.(.+)?/)[1]
        def s1First = s1.split(/\.(.+)?/)[0]
        println "$s1 $s2 $s1First $s1Remaining"
        
        def s2Remaining = s2.split(/\.(.+)?/)[1]
        def s2First = s2.split(/\.(.+)?/)[0]
        
        if (s1First.toInteger() > s2First.toInteger())
            return 1
            else if (s1First.toInteger () < s2First.toInteger()) 
            return -1
            else return compareSequence(s1Remaining, s2Remaining)
        
    }
	
}

