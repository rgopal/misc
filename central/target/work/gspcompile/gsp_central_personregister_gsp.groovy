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
invokeTag('captureMeta','sitemesh',4,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("layout"),'content':("main")],-1)
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
invokeTag('renderErrors','g',14,['bean':(person)],2)
printHtmlPart(7)
})
invokeTag('hasErrors','g',16,['bean':(person)],1)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(8)
createClosureForHtmlPart(6, 2)
invokeTag('textField','g',21,['name':("login"),'value':(person?.login)],2)
printHtmlPart(9)
invokeTag('passwordField','g',25,['name':("password"),'value':(person?.password)],-1)
printHtmlPart(10)
invokeTag('passwordField','g',29,['name':("confirm"),'value':(params?.confirm)],-1)
printHtmlPart(11)
invokeTag('textField','g',33,['name':("name"),'value':(person?.name)],-1)
printHtmlPart(12)
createClosureForHtmlPart(13, 2)
invokeTag('submitButton','g',37,['class':("formButton"),'name':("register"),'value':("Register")],2)
printHtmlPart(1)
})
invokeTag('form','g',38,['action':("register"),'name':("registerForm")],1)
printHtmlPart(14)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1428094496509L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
