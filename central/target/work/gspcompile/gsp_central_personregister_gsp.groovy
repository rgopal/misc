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
invokeTag('captureMeta','sitemesh',9,['gsp_sm_xmlClosingForEmptyTag':(""),'http-equiv':("Content-Type"),'content':("text/html; charset=UTF-8")],-1)
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',10,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',10,[:],2)
printHtmlPart(3)
})
invokeTag('captureHead','sitemesh',11,[:],1)
printHtmlPart(4)
createTagBody(1, {->
printHtmlPart(5)
invokeTag('renderErrors','g',17,['bean':(person)],-1)
printHtmlPart(6)
})
invokeTag('hasErrors','g',19,['bean':(person)],1)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(7)
invokeTag('textField','g',23,['name':("login"),'value':(person?.login)],-1)
printHtmlPart(8)
invokeTag('passwordField','g',27,['name':("password"),'value':(person?.password)],-1)
printHtmlPart(9)
invokeTag('passwordField','g',31,['name':("confirm"),'value':(params?.confirm)],-1)
printHtmlPart(10)
invokeTag('textField','g',35,['name':("name"),'value':(person?.name)],-1)
printHtmlPart(11)
invokeTag('submitButton','g',38,['class':("formButton"),'name':("register"),'value':("Register")],-1)
printHtmlPart(1)
})
invokeTag('form','g',39,['action':("register"),'name':("registerForm")],1)
printHtmlPart(12)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1428074532191L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
