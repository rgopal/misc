package com.oumuo.central
import groovy.transform.CompileStatic


//@CompileStatic
class CentralTagLib {
    // this is s regular service so needs to be inected
    def springSecurityService
    
    // This should be removed since it escapes the output which needs to
    // be renderedable as HTML
    // static defaultEncodeAs = [taglib:'html']
    static namespace = 'ct'
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def loginToggle = {
        out << "<html> <div>"
        if (springSecurityService?.currentUser){
            out << "<span style='float:right'>"
            out << " ${springSecurityService.currentUser.username}."
            out << "</span><span style='float:right;margin-right:10px'>"
            out << "<a href='${createLink(controller:'logout')}'>"
            out << "Logout </a></span>"
        }
        else{
            out << "<span style='float:right;margin-right:10px'>"
            out << "<a href='${createLink(controller:'login')}'>"
            out << "Login </a></span>"
        }
        out << "</div> </html>"
    }
}
