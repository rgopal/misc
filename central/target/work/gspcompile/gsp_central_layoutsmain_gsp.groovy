import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_layoutsmain_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/layouts/main.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',9,['gsp_sm_xmlClosingForEmptyTag':(""),'http-equiv':("Content-Type"),'content':("text/html; charset=UTF-8")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',10,['gsp_sm_xmlClosingForEmptyTag':(""),'http-equiv':("X-UA-Compatible"),'content':("IE=edge,chrome=1")],-1)
printHtmlPart(1)
createTagBody(2, {->
createTagBody(3, {->
invokeTag('layoutTitle','g',11,['default':("Oumuo")],-1)
})
invokeTag('captureTitle','sitemesh',11,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',11,[:],2)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',12,['gsp_sm_xmlClosingForEmptyTag':(""),'name':("viewport"),'content':("width=device-width, initial-scale=1.0")],-1)
printHtmlPart(2)
expressionOut.print(assetPath(src: 'favicon.ico'))
printHtmlPart(3)
expressionOut.print(assetPath(src: 'apple-touch-icon.png'))
printHtmlPart(4)
expressionOut.print(assetPath(src: 'apple-touch-icon-retina.png'))
printHtmlPart(5)
invokeTag('stylesheet','asset',16,['src':("application.css")],-1)
printHtmlPart(6)
invokeTag('javascript','asset',17,['src':("application.js")],-1)
printHtmlPart(6)
invokeTag('layoutHead','g',18,[:],-1)
printHtmlPart(7)
})
invokeTag('captureHead','sitemesh',19,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
invokeTag('image','asset',21,['src':("grails_logo.png"),'alt':("Oumuo")],-1)
printHtmlPart(9)
invokeTag('layoutBody','g',22,[:],-1)
printHtmlPart(10)
invokeTag('message','g',24,['code':("spinner.alt"),'default':("Loading&hellip;")],-1)
printHtmlPart(11)
if(true && (session?.person)) {
printHtmlPart(12)
createClosureForHtmlPart(13, 3)
invokeTag('link','g',31,['controller':("person"),'action':("logout")],3)
printHtmlPart(14)
expressionOut.print(session?.person?.name)
printHtmlPart(15)
expressionOut.print(session.person.country)
printHtmlPart(16)
}
else {
printHtmlPart(17)
createTagBody(3, {->
printHtmlPart(18)
createClosureForHtmlPart(19, 4)
invokeTag('textField','g',49,['name':("login"),'value':(fieldValue(bean:loginCmd, field:'login'))],4)
printHtmlPart(20)
invokeTag('passwordField','g',51,['name':("password")],-1)
printHtmlPart(21)
expressionOut.print(createLinkTo(dir:'images', file:'login-button.gif'))
printHtmlPart(22)
})
invokeTag('form','g',56,['name':("loginForm"),'url':([controller:'person',action:'login'])],3)
printHtmlPart(17)
invokeTag('renderErrors','g',57,['bean':(loginCmd)],-1)
printHtmlPart(1)
}
printHtmlPart(23)
if(true && (session.person)) {
printHtmlPart(24)
createClosureForHtmlPart(25, 3)
invokeTag('link','g',64,['controller':("person"),'action':("show")],3)
printHtmlPart(26)
createClosureForHtmlPart(25, 3)
invokeTag('link','g',66,['controller':("person"),'action':("show")],3)
printHtmlPart(27)
}
else {
printHtmlPart(28)
createClosureForHtmlPart(29, 3)
invokeTag('link','g',73,['controller':("person"),'action':("register")],3)
printHtmlPart(30)
}
printHtmlPart(31)
})
invokeTag('captureBody','sitemesh',78,[:],1)
printHtmlPart(32)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1428073032861L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
