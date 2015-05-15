import com.oumuo.central.Program
import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_program_form_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/program/_form.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
expressionOut.print(hasErrors(bean: programInstance, field: 'name', 'error'))
printHtmlPart(1)
invokeTag('message','g',7,['code':("program.name.label"),'default':("Name")],-1)
printHtmlPart(2)
invokeTag('textField','g',10,['name':("name"),'required':(""),'value':(programInstance?.name)],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'person', 'error'))
printHtmlPart(4)
invokeTag('message','g',16,['code':("program.person.label"),'default':("Person")],-1)
printHtmlPart(2)
invokeTag('select','g',19,['id':("person"),'name':("person.id"),'from':(com.oumuo.central.Person.findAllById(programInstance?.person?.id)),'optionKey':("id"),'required':(""),'value':(programInstance?.person?.id),'class':("many-to-one")],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'organization', 'error'))
printHtmlPart(5)
invokeTag('message','g',25,['code':("program.organization.label"),'default':("Organization")],-1)
printHtmlPart(6)
invokeTag('select','g',28,['id':("organization"),'name':("organization.id"),'from':(com.oumuo.central.Organization.secureList()),'optionKey':("id"),'value':(programInstance?.organization?.id),'class':("many-to-one"),'noSelection':(['null': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'catalogs', 'error'))
printHtmlPart(7)
invokeTag('message','g',34,['code':("program.catalogs.label"),'default':("Catalogs")],-1)
printHtmlPart(8)
for( c in (programInstance?.catalogs) ) {
printHtmlPart(9)
createTagBody(2, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',40,['controller':("catalog"),'action':("show"),'id':(c.id)],2)
printHtmlPart(10)
}
printHtmlPart(11)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'catalog.label', default: 'Catalog')]))
})
invokeTag('link','g',43,['controller':("catalog"),'action':("create"),'params':(['program.id': programInstance?.id])],1)
printHtmlPart(12)
expressionOut.print(hasErrors(bean: programInstance, field: 'rankings', 'error'))
printHtmlPart(13)
invokeTag('message','g',52,['code':("program.rankings.label"),'default':("Rankings")],-1)
printHtmlPart(8)
for( r in (programInstance?.rankings) ) {
printHtmlPart(9)
createTagBody(2, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',58,['controller':("ranking"),'action':("show"),'id':(r.id)],2)
printHtmlPart(10)
}
printHtmlPart(11)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'ranking.label', default: 'Ranking')]))
})
invokeTag('link','g',61,['controller':("ranking"),'action':("create"),'params':(['program.id': programInstance?.id])],1)
printHtmlPart(12)
expressionOut.print(hasErrors(bean: programInstance, field: 'credential', 'error'))
printHtmlPart(14)
invokeTag('message','g',70,['code':("program.credential.label"),'default':("Credential")],-1)
printHtmlPart(2)
invokeTag('select','g',73,['name':("credential"),'from':(com.oumuo.lookup.Credential?.values()),'keys':(com.oumuo.lookup.Credential.values()*.name()),'required':(""),'value':(programInstance?.credential?.name())],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicStratum', 'error'))
printHtmlPart(15)
invokeTag('message','g',79,['code':("program.academicStratum.label"),'default':("Academic Stratum")],-1)
printHtmlPart(2)
invokeTag('select','g',82,['name':("academicStratum"),'from':(com.oumuo.lookup.AcademicStratum?.values()),'keys':(com.oumuo.lookup.AcademicStratum.values()*.name()),'required':(""),'value':(programInstance?.academicStratum?.name())],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicMajor', 'error'))
printHtmlPart(16)
invokeTag('message','g',88,['code':("program.academicMajor.label"),'default':("Academic Major")],-1)
printHtmlPart(2)
invokeTag('select','g',91,['name':("academicMajor"),'from':(com.oumuo.lookup.AcademicMajor?.values()),'keys':(com.oumuo.lookup.AcademicMajor.values()*.name()),'required':(""),'value':(programInstance?.academicMajor?.name())],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'minimumGrade', 'error'))
printHtmlPart(17)
invokeTag('message','g',97,['code':("program.minimumGrade.label"),'default':("Minimum Grade")],-1)
printHtmlPart(2)
invokeTag('select','g',100,['name':("minimumGrade"),'from':(com.oumuo.lookup.Grade?.values()),'keys':(com.oumuo.lookup.Grade.values()*.name()),'required':(""),'value':(programInstance?.minimumGrade?.name())],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'minimumPercentage', 'error'))
printHtmlPart(18)
invokeTag('message','g',106,['code':("program.minimumPercentage.label"),'default':("Minimum Percentage")],-1)
printHtmlPart(6)
invokeTag('field','g',109,['name':("minimumPercentage"),'value':(fieldValue(bean: programInstance, field: 'minimumPercentage'))],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicSession', 'error'))
printHtmlPart(19)
invokeTag('message','g',115,['code':("program.academicSession.label"),'default':("Academic Session")],-1)
printHtmlPart(2)
invokeTag('select','g',118,['name':("academicSession"),'from':(com.oumuo.lookup.AcademicSession?.values()),'keys':(com.oumuo.lookup.AcademicSession.values()*.name()),'required':(""),'value':(programInstance?.academicSession?.name())],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'sessionFee', 'error'))
printHtmlPart(20)
invokeTag('message','g',124,['code':("program.sessionFee.label"),'default':("Session Fee")],-1)
printHtmlPart(6)
invokeTag('field','g',127,['name':("sessionFee"),'value':(fieldValue(bean: programInstance, field: 'sessionFee'))],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'ranking', 'error'))
printHtmlPart(21)
invokeTag('message','g',133,['code':("program.ranking.label"),'default':("Ranking")],-1)
printHtmlPart(2)
invokeTag('select','g',136,['name':("ranking"),'from':(0..1000),'class':("range"),'required':(""),'value':(fieldValue(bean: programInstance, field: 'ranking'))],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'startDate', 'error'))
printHtmlPart(22)
invokeTag('message','g',142,['code':("program.startDate.label"),'default':("Start Date")],-1)
printHtmlPart(23)
invokeTag('formatDate','g',145,['date':(programInstance?.startDate),'formatName':("dateonly.date.format")],-1)
printHtmlPart(24)
invokeTag('formatDate','g',145,['date':(programInstance?.startDate),'format':("dd")],-1)
printHtmlPart(25)
invokeTag('formatDate','g',145,['date':(programInstance?.startDate),'format':("MM")],-1)
printHtmlPart(26)
invokeTag('formatDate','g',145,['date':(programInstance?.startDate),'format':("yyyy")],-1)
printHtmlPart(27)
createClosureForHtmlPart(28, 1)
invokeTag('javascript','g',165,[:],1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'endDate', 'error'))
printHtmlPart(29)
invokeTag('message','g',171,['code':("program.endDate.label"),'default':("End Date")],-1)
printHtmlPart(30)
invokeTag('formatDate','g',174,['date':(programInstance?.endDate),'formatName':("dateonly.date.format")],-1)
printHtmlPart(31)
invokeTag('formatDate','g',174,['date':(programInstance?.endDate),'format':("dd")],-1)
printHtmlPart(32)
invokeTag('formatDate','g',174,['date':(programInstance?.endDate),'format':("MM")],-1)
printHtmlPart(33)
invokeTag('formatDate','g',174,['date':(programInstance?.endDate),'format':("yyyy")],-1)
printHtmlPart(27)
createClosureForHtmlPart(34, 1)
invokeTag('javascript','g',194,[:],1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'status', 'error'))
printHtmlPart(35)
invokeTag('message','g',200,['code':("program.status.label"),'default':("Status")],-1)
printHtmlPart(2)
invokeTag('select','g',203,['name':("status"),'from':(com.oumuo.lookup.Status?.values()),'keys':(com.oumuo.lookup.Status.values()*.name()),'required':(""),'value':(programInstance?.status?.name())],-1)
printHtmlPart(36)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1431724785206L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
