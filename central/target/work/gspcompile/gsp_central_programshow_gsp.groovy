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
if(true && (programInstance?.organization)) {
printHtmlPart(18)
invokeTag('message','g',37,['code':("program.organization.label"),'default':("Organization")],-1)
printHtmlPart(19)
createTagBody(3, {->
expressionOut.print(programInstance?.organization?.encodeAsHTML())
})
invokeTag('link','g',39,['controller':("organization"),'action':("show"),'id':(programInstance?.organization?.id)],3)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.catalogs)) {
printHtmlPart(20)
invokeTag('message','g',46,['code':("program.catalogs.label"),'default':("Catalogs")],-1)
printHtmlPart(21)
for( c in (programInstance.catalogs) ) {
printHtmlPart(22)
createTagBody(4, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',49,['controller':("catalog"),'action':("show"),'id':(c.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.rankings)) {
printHtmlPart(25)
invokeTag('message','g',57,['code':("program.rankings.label"),'default':("Rankings")],-1)
printHtmlPart(21)
for( r in (programInstance.rankings) ) {
printHtmlPart(26)
createTagBody(4, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',60,['controller':("ranking"),'action':("show"),'id':(r.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.requirements)) {
printHtmlPart(27)
invokeTag('message','g',68,['code':("program.requirements.label"),'default':("Requirements")],-1)
printHtmlPart(21)
for( r in (programInstance.requirements) ) {
printHtmlPart(28)
createTagBody(4, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',71,['controller':("requirement"),'action':("show"),'id':(r.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.learningAssessments)) {
printHtmlPart(29)
invokeTag('message','g',79,['code':("program.learningAssessments.label"),'default':("Learning Assessments")],-1)
printHtmlPart(21)
for( l in (programInstance.learningAssessments) ) {
printHtmlPart(30)
createTagBody(4, {->
expressionOut.print(l?.encodeAsHTML())
})
invokeTag('link','g',82,['controller':("learningAssessment"),'action':("show"),'id':(l.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.courseRelations)) {
printHtmlPart(31)
invokeTag('message','g',90,['code':("program.courseRelations.label"),'default':("Course Relations")],-1)
printHtmlPart(21)
for( c in (programInstance.courseRelations) ) {
printHtmlPart(32)
createTagBody(4, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',93,['controller':("courseRelation"),'action':("show"),'id':(c.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.authorships)) {
printHtmlPart(33)
invokeTag('message','g',101,['code':("program.authorships.label"),'default':("Authorships")],-1)
printHtmlPart(21)
for( a in (programInstance.authorships) ) {
printHtmlPart(34)
createTagBody(4, {->
expressionOut.print(a?.encodeAsHTML())
})
invokeTag('link','g',104,['controller':("authorship"),'action':("show"),'id':(a.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.terms)) {
printHtmlPart(35)
invokeTag('message','g',112,['code':("program.terms.label"),'default':("Terms")],-1)
printHtmlPart(21)
for( t in (programInstance.terms) ) {
printHtmlPart(36)
createTagBody(4, {->
expressionOut.print(t?.encodeAsHTML())
})
invokeTag('link','g',115,['controller':("term"),'action':("show"),'id':(t.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.studentPrograms)) {
printHtmlPart(37)
invokeTag('message','g',123,['code':("program.studentPrograms.label"),'default':("Student Programs")],-1)
printHtmlPart(21)
for( s in (programInstance.studentPrograms) ) {
printHtmlPart(38)
createTagBody(4, {->
expressionOut.print(s?.encodeAsHTML())
})
invokeTag('link','g',126,['controller':("studentProgram"),'action':("show"),'id':(s.id)],4)
printHtmlPart(23)
}
printHtmlPart(24)
}
printHtmlPart(17)
if(true && (programInstance?.credential)) {
printHtmlPart(39)
invokeTag('message','g',134,['code':("program.credential.label"),'default':("Credential")],-1)
printHtmlPart(40)
invokeTag('fieldValue','g',136,['bean':(programInstance),'field':("credential")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicStratum)) {
printHtmlPart(41)
invokeTag('message','g',143,['code':("program.academicStratum.label"),'default':("Academic Stratum")],-1)
printHtmlPart(42)
invokeTag('fieldValue','g',145,['bean':(programInstance),'field':("academicStratum")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicMajor)) {
printHtmlPart(43)
invokeTag('message','g',152,['code':("program.academicMajor.label"),'default':("Academic Major")],-1)
printHtmlPart(44)
invokeTag('fieldValue','g',154,['bean':(programInstance),'field':("academicMajor")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.minimumGrade)) {
printHtmlPart(45)
invokeTag('message','g',161,['code':("program.minimumGrade.label"),'default':("Minimum Grade")],-1)
printHtmlPart(46)
invokeTag('fieldValue','g',163,['bean':(programInstance),'field':("minimumGrade")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.minimumPercentage)) {
printHtmlPart(47)
invokeTag('message','g',170,['code':("program.minimumPercentage.label"),'default':("Minimum Percentage")],-1)
printHtmlPart(48)
invokeTag('fieldValue','g',172,['bean':(programInstance),'field':("minimumPercentage")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.academicSession)) {
printHtmlPart(49)
invokeTag('message','g',179,['code':("program.academicSession.label"),'default':("Academic Session")],-1)
printHtmlPart(50)
invokeTag('fieldValue','g',181,['bean':(programInstance),'field':("academicSession")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.termFee)) {
printHtmlPart(51)
invokeTag('message','g',188,['code':("program.termFee.label"),'default':("Term Fee")],-1)
printHtmlPart(52)
invokeTag('fieldValue','g',190,['bean':(programInstance),'field':("termFee")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.ranking)) {
printHtmlPart(53)
invokeTag('message','g',197,['code':("program.ranking.label"),'default':("Ranking")],-1)
printHtmlPart(54)
invokeTag('fieldValue','g',199,['bean':(programInstance),'field':("ranking")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.startDate)) {
printHtmlPart(55)
invokeTag('message','g',206,['code':("program.startDate.label"),'default':("Start Date")],-1)
printHtmlPart(56)
invokeTag('formatDate','g',208,['date':(programInstance?.startDate)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.endDate)) {
printHtmlPart(57)
invokeTag('message','g',215,['code':("program.endDate.label"),'default':("End Date")],-1)
printHtmlPart(58)
invokeTag('formatDate','g',217,['date':(programInstance?.endDate)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.status)) {
printHtmlPart(59)
invokeTag('message','g',224,['code':("program.status.label"),'default':("Status")],-1)
printHtmlPart(60)
invokeTag('fieldValue','g',226,['bean':(programInstance),'field':("status")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.dateCreated)) {
printHtmlPart(61)
invokeTag('message','g',233,['code':("program.dateCreated.label"),'default':("Date Created")],-1)
printHtmlPart(62)
invokeTag('formatDate','g',235,['date':(programInstance?.dateCreated)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (programInstance?.lastUpdated)) {
printHtmlPart(63)
invokeTag('message','g',242,['code':("program.lastUpdated.label"),'default':("Last Updated")],-1)
printHtmlPart(64)
invokeTag('formatDate','g',244,['date':(programInstance?.lastUpdated)],-1)
printHtmlPart(16)
}
printHtmlPart(65)
createTagBody(2, {->
printHtmlPart(66)
createTagBody(3, {->
invokeTag('message','g',252,['code':("default.button.edit.label"),'default':("Edit")],-1)
})
invokeTag('link','g',252,['class':("edit"),'action':("edit"),'resource':(programInstance)],3)
printHtmlPart(67)
invokeTag('actionSubmit','g',253,['class':("delete"),'action':("delete"),'value':(message(code: 'default.button.delete.label', default: 'Delete')),'onclick':("return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');")],-1)
printHtmlPart(68)
})
invokeTag('form','g',255,['url':([resource:programInstance, action:'delete']),'method':("DELETE")],2)
printHtmlPart(69)
})
invokeTag('captureBody','sitemesh',257,[:],1)
printHtmlPart(70)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1434808219386L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
