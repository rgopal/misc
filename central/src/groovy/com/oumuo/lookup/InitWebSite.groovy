/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oumuo.lookup

import groovy.util.logging.Log4j

/**
 *
 * @author gopal
 */
@Log4j
class InitWebSite {
       
    static void load () {
        def items = [ 
            new WebSite(name: "Google", loginSupported: true, siteUrl: "https://www.google.com"),
            new WebSite(name: "facebook", loginSupported: true, siteUrl: "http://www.facebook.com"),
            new WebSite(name: "twitter", loginSupported: false, siteUrl: "http://www.twitter.com"),
            new WebSite(name: "Coursera", loginSupported: true, siteUrl: "http://www.coursera.com"),
            new WebSite(name: "edX", loginSupported: true, siteUrl: "http://www.edx.com"),
            new WebSite(name: "Paypal", loginSupported: true, siteUrl: "http://www.paypal.com"),
            
        ]
       
        for (item in items) {
            log.trace "load: loading $item"
            if (!item.save(flush:true)){ item.errors.allErrors.each {error ->
                    log.warn "An error occured with item: ${error}"

                }
            }
       
        }
        log.info ("load: loaded ${WebSite.count()} out of $items.size() items")
	
    }
	
}



