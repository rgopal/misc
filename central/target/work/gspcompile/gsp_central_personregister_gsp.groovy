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
createTagBody(1, {->
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',5,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',5,[:],2)
printHtmlPart(3)
})
invokeTag('captureHead','sitemesh',6,[:],1)
printHtmlPart(4)
createTagBody(1, {->
printHtmlPart(5)
createClosureForHtmlPart(6, 2)
invokeTag('renderErrors','g',13,['bean':(person)],2)
printHtmlPart(7)
})
invokeTag('hasErrors','g',15,['bean':(person)],1)
printHtmlPart(8)
createTagBody(1, {->
printHtmlPart(9)
createClosureForHtmlPart(6, 2)
invokeTag('textField','g',20,['name':("login"),'value':(person?.login)],2)
printHtmlPart(10)
invokeTag('passwordField','g',24,['name':("password"),'value':(person?.password)],-1)
printHtmlPart(11)
invokeTag('passwordField','g',28,['name':("confirm"),'value':(params?.confirm)],-1)
printHtmlPart(12)
invokeTag('textField','g',32,['name':("name"),'value':(person?.name)],-1)
printHtmlPart(13)
createClosureForHtmlPart(14, 2)
invokeTag('submitButton','g',36,['class':("formButton"),'name':("register"),'value':("Register")],2)
printHtmlPart(8)
})
invokeTag('form','g',37,['action':("register"),'name':("registerForm")],1)
printHtmlPart(15)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1428088460681L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
