import com.oumuo.central.Program
import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_programshow_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/program/show.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(2)
invokeTag('captureMeta','sitemesh',6,['gsp_sm_xmlClosingForEmptyTag':(""),'name':("layout"),'content':("main")],-1)
printHtmlPart(2)
invokeTag('set','g',7,['var':("entityName"),'value':(message(code: 'program.label', default: 'Program'))],-1)
printHtmlPart(2)
createTagBody(2, {->
createTagBody(3, {->
invokeTag('message','g',8,['code':("default.show.label"),'args':([entityName])],-1)
})
invokeTag('captureTitle','sitemesh',8,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',8,[:],2)
printHtmlPart(3)
})
invokeTag('captureHead','sitemesh',9,[:],1)
printHtmlPart(3)
createTagBody(1, {->
printHtmlPart(4)
invokeTag('message','g',11,['code':("default.link.skip.label"),'default':("Skip to content&hellip;")],-1)
printHtmlPart(5)
expressionOut.print(createLink(uri: '/'))
printHtmlPart(6)
invokeTag('message','g',14,['code':("default.home.label")],-1)
printHtmlPart(7)
createTagBody(2, {->
invokeTag('message','g',15,['code':("default.list.label"),'args':([entityName])],-1)
})
invokeTag('link','g',15,['class':("list"),'action':("index")],2)
printHtmlPart(8)
createTagBody(2, {->
invokeTag('message','g',16,['code':("default.new.label"),'args':([entityName])],-1)
})
invokeTag('link','g',16,['class':("create"),'action':("create")],2)
printHtmlPart(9)
invokeTag('message','g',20,['code':("default.show.label"),'args':([entityName])],-1)
printHtmlPart(10)
if(true && (flash.message)) {
printHtmlPart(11)
expressionOut.print(flash.message)
printHtmlPart(12)
}
printHtmlPart(13)
if(true && (programInstance?.name)) {
printHtmlPart(14)
invokeTag('message','g',28,['code':("program.name.label"),'default':("Name")],-1)
printHtmlPart(15)
invokeTag('fieldValue','g',30,['bean':(programInstance),'field':("name")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.person)) {
printHtmlPart(18)
invokeTag('message','g',37,['code':("program.person.label"),'default':("Person")],-1)
printHtmlPart(19)
createTagBody(3, {->
expressionOut.print(programInstance?.person?.encodeAsHTML())
})
invokeTag('link','g',39,['controller':("person"),'action':("show"),'id':(programInstance?.person?.id)],3)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.organization)) {
printHtmlPart(20)
invokeTag('message','g',46,['code':("program.organization.label"),'default':("Organization")],-1)
printHtmlPart(21)
createTagBody(3, {->
expressionOut.print(programInstance?.organization?.encodeAsHTML())
})
invokeTag('link','g',48,['controller':("organization"),'action':("show"),'id':(programInstance?.organization?.id)],3)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.catalogs)) {
printHtmlPart(22)
invokeTag('message','g',55,['code':("program.catalogs.label"),'default':("Catalogs")],-1)
printHtmlPart(23)
for( c in (programInstance.catalogs) ) {
printHtmlPart(24)
createTagBody(4, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',58,['controller':("catalog"),'action':("show"),'id':(c.id)],4)
printHtmlPart(25)
}
printHtmlPart(26)
}
printHtmlPart(17)
if(true && (programInstance?.rankings)) {
printHtmlPart(27)
invokeTag('message','g',66,['code':("program.rankings.label"),'default':("Rankings")],-1)
printHtmlPart(23)
for( r in (programInstance.rankings) ) {
printHtmlPart(28)
createTagBody(4, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',69,['controller':("ranking"),'action':("show"),'id':(r.id)],4)
printHtmlPart(25)
}
printHtmlPart(26)
}
printHtmlPart(17)
if(true && (programInstance?.credential)) {
printHtmlPart(29)
invokeTag('message','g',77,['code':("program.credential.label"),'default':("Credential")],-1)
printHtmlPart(30)
invokeTag('fieldValue','g',79,['bean':(programInstance),'field':("credential")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicStratum)) {
printHtmlPart(31)
invokeTag('message','g',86,['code':("program.academicStratum.label"),'default':("Academic Stratum")],-1)
printHtmlPart(32)
invokeTag('fieldValue','g',88,['bean':(programInstance),'field':("academicStratum")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicMajor)) {
printHtmlPart(33)
invokeTag('message','g',95,['code':("program.academicMajor.label"),'default':("Academic Major")],-1)
printHtmlPart(34)
invokeTag('fieldValue','g',97,['bean':(programInstance),'field':("academicMajor")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.minimumGrade)) {
printHtmlPart(35)
invokeTag('message','g',104,['code':("program.minimumGrade.label"),'default':("Minimum Grade")],-1)
printHtmlPart(36)
invokeTag('fieldValue','g',106,['bean':(programInstance),'field':("minimumGrade")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.minimumPercentage)) {
printHtmlPart(37)
invokeTag('message','g',113,['code':("program.minimumPercentage.label"),'default':("Minimum Percentage")],-1)
printHtmlPart(38)
invokeTag('fieldValue','g',115,['bean':(programInstance),'field':("minimumPercentage")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicSession)) {
printHtmlPart(39)
invokeTag('message','g',122,['code':("program.academicSession.label"),'default':("Academic Session")],-1)
printHtmlPart(40)
invokeTag('fieldValue','g',124,['bean':(programInstance),'field':("academicSession")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.sessionFee)) {
printHtmlPart(41)
invokeTag('message','g',131,['code':("program.sessionFee.label"),'default':("Session Fee")],-1)
printHtmlPart(42)
invokeTag('fieldValue','g',133,['bean':(programInstance),'field':("sessionFee")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.ranking)) {
printHtmlPart(43)
invokeTag('message','g',140,['code':("program.ranking.label"),'default':("Ranking")],-1)
printHtmlPart(44)
invokeTag('fieldValue','g',142,['bean':(programInstance),'field':("ranking")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.startDate)) {
printHtmlPart(45)
invokeTag('message','g',149,['code':("program.startDate.label"),'default':("Start Date")],-1)
printHtmlPart(46)
invokeTag('formatDate','g',151,['date':(programInstance?.startDate)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.endDate)) {
printHtmlPart(47)
invokeTag('message','g',158,['code':("program.endDate.label"),'default':("End Date")],-1)
printHtmlPart(48)
invokeTag('formatDate','g',160,['date':(programInstance?.endDate)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.status)) {
printHtmlPart(49)
invokeTag('message','g',167,['code':("program.status.label"),'default':("Status")],-1)
printHtmlPart(50)
invokeTag('fieldValue','g',169,['bean':(programInstance),'field':("status")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.dateCreated)) {
printHtmlPart(51)
invokeTag('message','g',176,['code':("program.dateCreated.label"),'default':("Date Created")],-1)
printHtmlPart(52)
invokeTag('formatDate','g',178,['date':(programInstance?.dateCreated)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.lastUpdated)) {
printHtmlPart(53)
invokeTag('message','g',185,['code':("program.lastUpdated.label"),'default':("Last Updated")],-1)
printHtmlPart(54)
invokeTag('formatDate','g',187,['date':(programInstance?.lastUpdated)],-1)
printHtmlPart(16)
}
printHtmlPart(55)
createTagBody(2, {->
printHtmlPart(56)
createTagBody(3, {->
invokeTag('message','g',195,['code':("default.button.edit.label"),'default':("Edit")],-1)
})
invokeTag('link','g',195,['class':("edit"),'action':("edit"),'resource':(programInstance)],3)
printHtmlPart(57)
invokeTag('actionSubmit','g',196,['class':("delete"),'action':("delete"),'value':(message(code: 'default.button.delete.label', default: 'Delete')),'onclick':("return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');")],-1)
printHtmlPart(58)
})
invokeTag('form','g',198,['url':([resource:programInstance, action:'delete']),'method':("DELETE")],2)
printHtmlPart(59)
})
invokeTag('captureBody','sitemesh',200,[:],1)
printHtmlPart(60)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1431724783976L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
