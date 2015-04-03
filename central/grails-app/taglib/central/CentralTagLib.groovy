package central

class CentralTagLib {
    // This should be removed since it escapes the output which needs to
    // be renderedable as HTML
    // static defaultEncodeAs = [taglib:'html']
    static namespace = 'ct'
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def loginToggle = {
        out << "<html> <div>"
        if (session.person){
            out << "<span style='float:left'>"
            out << "Welcome ${session.person}."
            out << "</span><span style='float:right;margin-right:10px'>"
            out << "<a href='${createLink(controller:'person', action:'logout')}'>"
            out << "Logout </a></span>"
        }
        else{
            out << "<span style='float:right;margin-right:10px'>"
            out << "<a href='${createLink(controller:'person', action:'login')}'>"
            out << "Login </a></span>"
        }
        out << "</div> </html>"
    }
}
