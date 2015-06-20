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
expressionOut.print(hasErrors(bean: programInstance, field: 'organization', 'error'))
printHtmlPart(4)
invokeTag('message','g',16,['code':("program.organization.label"),'default':("Organization")],-1)
printHtmlPart(5)
invokeTag('select','g',19,['id':("organization"),'name':("organization.id"),'from':(com.oumuo.central.Organization.secureList()),'optionKey':("id"),'value':(programInstance?.organization?.id),'class':("many-to-one"),'noSelection':(['null': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'catalogs', 'error'))
printHtmlPart(6)
invokeTag('message','g',25,['code':("program.catalogs.label"),'default':("Catalogs")],-1)
printHtmlPart(7)
for( c in (programInstance?.catalogs) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',31,['controller':("catalog"),'action':("show"),'id':(c.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'catalog.label', default: 'Catalog')]))
})
invokeTag('link','g',34,['controller':("catalog"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'catalogs' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'rankings', 'error'))
printHtmlPart(12)
invokeTag('message','g',43,['code':("program.rankings.label"),'default':("Rankings")],-1)
printHtmlPart(7)
for( r in (programInstance?.rankings) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',49,['controller':("ranking"),'action':("show"),'id':(r.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'ranking.label', default: 'Ranking')]))
})
invokeTag('link','g',52,['controller':("ranking"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'rankings' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'requirements', 'error'))
printHtmlPart(13)
invokeTag('message','g',61,['code':("program.requirements.label"),'default':("Requirements")],-1)
printHtmlPart(7)
for( r in (programInstance?.requirements) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',67,['controller':("requirement"),'action':("show"),'id':(r.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'requirement.label', default: 'Requirement')]))
})
invokeTag('link','g',70,['controller':("requirement"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'requirements' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'learningAssessments', 'error'))
printHtmlPart(14)
invokeTag('message','g',79,['code':("program.learningAssessments.label"),'default':("Learning Assessments")],-1)
printHtmlPart(7)
for( l in (programInstance?.learningAssessments) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(l?.encodeAsHTML())
})
invokeTag('link','g',85,['controller':("learningAssessment"),'action':("show"),'id':(l.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'learningAssessment.label', default: 'LearningAssessment')]))
})
invokeTag('link','g',88,['controller':("learningAssessment"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'learningAssessments' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'courseRelations', 'error'))
printHtmlPart(15)
invokeTag('message','g',97,['code':("program.courseRelations.label"),'default':("Course Relations")],-1)
printHtmlPart(7)
for( c in (programInstance?.courseRelations) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',103,['controller':("courseRelation"),'action':("show"),'id':(c.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'courseRelation.label', default: 'CourseRelation')]))
})
invokeTag('link','g',106,['controller':("courseRelation"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'courseRelations' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'authorships', 'error'))
printHtmlPart(16)
invokeTag('message','g',115,['code':("program.authorships.label"),'default':("Authorships")],-1)
printHtmlPart(7)
for( a in (programInstance?.authorships) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(a?.encodeAsHTML())
})
invokeTag('link','g',121,['controller':("authorship"),'action':("show"),'id':(a.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'authorship.label', default: 'Authorship')]))
})
invokeTag('link','g',124,['controller':("authorship"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'authorships' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'terms', 'error'))
printHtmlPart(17)
invokeTag('message','g',133,['code':("program.terms.label"),'default':("Terms")],-1)
printHtmlPart(7)
for( t in (programInstance?.terms) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(t?.encodeAsHTML())
})
invokeTag('link','g',139,['controller':("term"),'action':("show"),'id':(t.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'term.label', default: 'Term')]))
})
invokeTag('link','g',142,['controller':("term"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'terms' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'studentPrograms', 'error'))
printHtmlPart(18)
invokeTag('message','g',151,['code':("program.studentPrograms.label"),'default':("Student Programs")],-1)
printHtmlPart(7)
for( s in (programInstance?.studentPrograms) ) {
printHtmlPart(8)
createTagBody(2, {->
expressionOut.print(s?.encodeAsHTML())
})
invokeTag('link','g',157,['controller':("studentProgram"),'action':("show"),'id':(s.id)],2)
printHtmlPart(9)
}
printHtmlPart(10)
createTagBody(1, {->
expressionOut.print(message(code: 'default.add.label', args: [message(code: 'studentProgram.label', default: 'StudentProgram')]))
})
invokeTag('link','g',160,['controller':("studentProgram"),'action':("create"),'params':(['program.id': programInstance?.id, oneToManyPropertyName: 'studentPrograms' ])],1)
printHtmlPart(11)
expressionOut.print(hasErrors(bean: programInstance, field: 'credential', 'error'))
printHtmlPart(19)
invokeTag('message','g',169,['code':("program.credential.label"),'default':("Credential")],-1)
printHtmlPart(5)
invokeTag('select','g',172,['name':("credential"),'from':(com.oumuo.lookup.Credential?.values()),'keys':(com.oumuo.lookup.Credential.values()*.name()),'value':(programInstance?.credential?.name()),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicStratum', 'error'))
printHtmlPart(20)
invokeTag('message','g',178,['code':("program.academicStratum.label"),'default':("Academic Stratum")],-1)
printHtmlPart(5)
invokeTag('select','g',181,['name':("academicStratum"),'from':(com.oumuo.lookup.AcademicStratum?.values()),'keys':(com.oumuo.lookup.AcademicStratum.values()*.name()),'value':(programInstance?.academicStratum?.name()),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicMajor', 'error'))
printHtmlPart(21)
invokeTag('message','g',187,['code':("program.academicMajor.label"),'default':("Academic Major")],-1)
printHtmlPart(5)
invokeTag('select','g',190,['name':("academicMajor"),'from':(com.oumuo.lookup.AcademicMajor?.values()),'keys':(com.oumuo.lookup.AcademicMajor.values()*.name()),'value':(programInstance?.academicMajor?.name()),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'minimumGrade', 'error'))
printHtmlPart(22)
invokeTag('message','g',196,['code':("program.minimumGrade.label"),'default':("Minimum Grade")],-1)
printHtmlPart(5)
invokeTag('select','g',199,['name':("minimumGrade"),'from':(com.oumuo.lookup.Grade?.values()),'keys':(com.oumuo.lookup.Grade.values()*.name()),'value':(programInstance?.minimumGrade?.name()),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'minimumPercentage', 'error'))
printHtmlPart(23)
invokeTag('message','g',205,['code':("program.minimumPercentage.label"),'default':("Minimum Percentage")],-1)
printHtmlPart(5)
invokeTag('field','g',208,['name':("minimumPercentage"),'value':(fieldValue(bean: programInstance, field: 'minimumPercentage'))],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'academicSession', 'error'))
printHtmlPart(24)
invokeTag('message','g',214,['code':("program.academicSession.label"),'default':("Academic Session")],-1)
printHtmlPart(5)
invokeTag('select','g',217,['name':("academicSession"),'from':(com.oumuo.lookup.AcademicSession?.values()),'keys':(com.oumuo.lookup.AcademicSession.values()*.name()),'value':(programInstance?.academicSession?.name()),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'termFee', 'error'))
printHtmlPart(25)
invokeTag('message','g',223,['code':("program.termFee.label"),'default':("Term Fee")],-1)
printHtmlPart(5)
invokeTag('field','g',226,['name':("termFee"),'value':(fieldValue(bean: programInstance, field: 'termFee'))],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'ranking', 'error'))
printHtmlPart(26)
invokeTag('message','g',232,['code':("program.ranking.label"),'default':("Ranking")],-1)
printHtmlPart(5)
invokeTag('select','g',235,['name':("ranking"),'from':(0..1000),'class':("range"),'value':(fieldValue(bean: programInstance, field: 'ranking')),'noSelection':(['': ''])],-1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'startDate', 'error'))
printHtmlPart(27)
invokeTag('message','g',241,['code':("program.startDate.label"),'default':("Start Date")],-1)
printHtmlPart(28)
invokeTag('formatDate','g',244,['date':(programInstance?.startDate),'formatName':("dateonly.date.format")],-1)
printHtmlPart(29)
invokeTag('formatDate','g',244,['date':(programInstance?.startDate),'format':("dd")],-1)
printHtmlPart(30)
invokeTag('formatDate','g',244,['date':(programInstance?.startDate),'format':("MM")],-1)
printHtmlPart(31)
invokeTag('formatDate','g',244,['date':(programInstance?.startDate),'format':("yyyy")],-1)
printHtmlPart(32)
createClosureForHtmlPart(33, 1)
invokeTag('javascript','g',264,[:],1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'endDate', 'error'))
printHtmlPart(34)
invokeTag('message','g',270,['code':("program.endDate.label"),'default':("End Date")],-1)
printHtmlPart(35)
invokeTag('formatDate','g',273,['date':(programInstance?.endDate),'formatName':("dateonly.date.format")],-1)
printHtmlPart(36)
invokeTag('formatDate','g',273,['date':(programInstance?.endDate),'format':("dd")],-1)
printHtmlPart(37)
invokeTag('formatDate','g',273,['date':(programInstance?.endDate),'format':("MM")],-1)
printHtmlPart(38)
invokeTag('formatDate','g',273,['date':(programInstance?.endDate),'format':("yyyy")],-1)
printHtmlPart(32)
createClosureForHtmlPart(39, 1)
invokeTag('javascript','g',293,[:],1)
printHtmlPart(3)
expressionOut.print(hasErrors(bean: programInstance, field: 'status', 'error'))
printHtmlPart(40)
invokeTag('message','g',299,['code':("program.status.label"),'default':("Status")],-1)
printHtmlPart(2)
invokeTag('select','g',302,['name':("status"),'from':(com.oumuo.lookup.Status?.values()),'keys':(com.oumuo.lookup.Status.values()*.name()),'required':(""),'value':(programInstance?.status?.name())],-1)
printHtmlPart(41)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1434808221319L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
