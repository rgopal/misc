import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_personregister_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/person/register.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(2)
invokeTag('captureMeta','sitemesh',11,['gsp_sm_xmlClosingForEmptyTag':(""),'http-equiv':("Content-Type"),'content':("text/html; charset=UTF-8")],-1)
printHtmlPart(2)
createTagBody(2, {->
createClosureForHtmlPart(3, 3)
invokeTag('captureTitle','sitemesh',12,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',12,[:],2)
printHtmlPart(4)
})
invokeTag('captureHead','sitemesh',13,[:],1)
printHtmlPart(5)
createTagBody(1, {->
printHtmlPart(6)
invokeTag('renderErrors','g',19,['bean':(person)],-1)
printHtmlPart(7)
})
invokeTag('hasErrors','g',21,['bean':(person)],1)
printHtmlPart(2)
createTagBody(1, {->
printHtmlPart(8)
invokeTag('textField','g',25,['name':("login"),'value':(person?.login)],-1)
printHtmlPart(9)
invokeTag('passwordField','g',29,['name':("password"),'value':(person?.password)],-1)
printHtmlPart(10)
invokeTag('passwordField','g',33,['name':("confirm"),'value':(params?.confirm)],-1)
printHtmlPart(11)
invokeTag('textField','g',37,['name':("name"),'value':(person?.name)],-1)
printHtmlPart(12)
invokeTag('submitButton','g',40,['class':("formButton"),'name':("register"),'value':("Register")],-1)
printHtmlPart(2)
})
invokeTag('form','g',41,['action':("register"),'name':("registerForm")],1)
printHtmlPart(13)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1428004123539L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
