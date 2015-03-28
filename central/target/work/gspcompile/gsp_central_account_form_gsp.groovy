import central.Account
import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_account_form_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/account/_form.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
expressionOut.print(hasErrors(bean: accountInstance, field: 'email', 'error'))
printHtmlPart(1)
invokeTag('message','g',7,['code':("account.email.label"),'default':("Email")],-1)
printHtmlPart(2)
invokeTag('field','g',10,['type':("email"),'name':("email"),'required':(""),'value':(accountInstance?.email)],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: accountInstance, field: 'password', 'error'))
printHtmlPart(4)
invokeTag('message','g',16,['code':("account.password.label"),'default':("Password")],-1)
printHtmlPart(5)
invokeTag('textField','g',19,['name':("password"),'value':(accountInstance?.password)],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: accountInstance, field: 'userName', 'error'))
printHtmlPart(6)
invokeTag('message','g',25,['code':("account.userName.label"),'default':("User Name")],-1)
printHtmlPart(2)
invokeTag('textField','g',28,['name':("userName"),'required':(""),'value':(accountInstance?.userName)],-1)
printHtmlPart(7)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1425692213684L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
