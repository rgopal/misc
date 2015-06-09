import com.oumuo.central.Person
import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_central_personshow_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/person/show.gsp" }
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
invokeTag('set','g',7,['var':("entityName"),'value':(message(code: 'person.label', default: 'Person'))],-1)
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
if(true && (personInstance?.name)) {
printHtmlPart(14)
invokeTag('message','g',28,['code':("person.name.label"),'default':("Name")],-1)
printHtmlPart(15)
invokeTag('fieldValue','g',30,['bean':(personInstance),'field':("name")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.personRoles)) {
printHtmlPart(18)
invokeTag('message','g',37,['code':("person.personRoles.label"),'default':("Person Roles")],-1)
printHtmlPart(19)
for( p in (personInstance.personRoles) ) {
printHtmlPart(20)
createTagBody(4, {->
expressionOut.print(p?.encodeAsHTML())
})
invokeTag('link','g',40,['controller':("personRole"),'action':("show"),'id':(p.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.staffings)) {
printHtmlPart(23)
invokeTag('message','g',48,['code':("person.staffings.label"),'default':("Staffings")],-1)
printHtmlPart(19)
for( s in (personInstance.staffings) ) {
printHtmlPart(24)
createTagBody(4, {->
expressionOut.print(s?.encodeAsHTML())
})
invokeTag('link','g',51,['controller':("staffing"),'action':("show"),'id':(s.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.accounts)) {
printHtmlPart(25)
invokeTag('message','g',59,['code':("person.accounts.label"),'default':("Accounts")],-1)
printHtmlPart(19)
for( a in (personInstance.accounts) ) {
printHtmlPart(26)
createTagBody(4, {->
expressionOut.print(a?.encodeAsHTML())
})
invokeTag('link','g',62,['controller':("account"),'action':("show"),'id':(a.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.comments)) {
printHtmlPart(27)
invokeTag('message','g',70,['code':("person.comments.label"),'default':("Comments")],-1)
printHtmlPart(19)
for( c in (personInstance.comments) ) {
printHtmlPart(28)
createTagBody(4, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',73,['controller':("comment"),'action':("show"),'id':(c.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.programs)) {
printHtmlPart(29)
invokeTag('message','g',81,['code':("person.programs.label"),'default':("Programs")],-1)
printHtmlPart(19)
for( p in (personInstance.programs) ) {
printHtmlPart(30)
createTagBody(4, {->
expressionOut.print(p?.encodeAsHTML())
})
invokeTag('link','g',84,['controller':("program"),'action':("show"),'id':(p.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.catalogs)) {
printHtmlPart(31)
invokeTag('message','g',92,['code':("person.catalogs.label"),'default':("Catalogs")],-1)
printHtmlPart(19)
for( c in (personInstance.catalogs) ) {
printHtmlPart(32)
createTagBody(4, {->
expressionOut.print(c?.encodeAsHTML())
})
invokeTag('link','g',95,['controller':("catalog"),'action':("show"),'id':(c.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.rankings)) {
printHtmlPart(33)
invokeTag('message','g',103,['code':("person.rankings.label"),'default':("Rankings")],-1)
printHtmlPart(19)
for( r in (personInstance.rankings) ) {
printHtmlPart(34)
createTagBody(4, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',106,['controller':("ranking"),'action':("show"),'id':(r.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.rankingItems)) {
printHtmlPart(35)
invokeTag('message','g',114,['code':("person.rankingItems.label"),'default':("Ranking Items")],-1)
printHtmlPart(19)
for( r in (personInstance.rankingItems) ) {
printHtmlPart(36)
createTagBody(4, {->
expressionOut.print(r?.encodeAsHTML())
})
invokeTag('link','g',117,['controller':("rankingItem"),'action':("show"),'id':(r.id)],4)
printHtmlPart(21)
}
printHtmlPart(22)
}
printHtmlPart(17)
if(true && (personInstance?.sex)) {
printHtmlPart(37)
invokeTag('message','g',125,['code':("person.sex.label"),'default':("Sex")],-1)
printHtmlPart(38)
invokeTag('fieldValue','g',127,['bean':(personInstance),'field':("sex")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.dateOfBirth)) {
printHtmlPart(39)
invokeTag('message','g',134,['code':("person.dateOfBirth.label"),'default':("Date Of Birth")],-1)
printHtmlPart(40)
invokeTag('formatDate','g',136,['date':(personInstance?.dateOfBirth)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.race)) {
printHtmlPart(41)
invokeTag('message','g',143,['code':("person.race.label"),'default':("Race")],-1)
printHtmlPart(42)
invokeTag('fieldValue','g',145,['bean':(personInstance),'field':("race")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.status)) {
printHtmlPart(43)
invokeTag('message','g',152,['code':("person.status.label"),'default':("Status")],-1)
printHtmlPart(44)
invokeTag('fieldValue','g',154,['bean':(personInstance),'field':("status")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.preferredLanguage)) {
printHtmlPart(45)
invokeTag('message','g',161,['code':("person.preferredLanguage.label"),'default':("Preferred Language")],-1)
printHtmlPart(46)
invokeTag('fieldValue','g',163,['bean':(personInstance),'field':("preferredLanguage")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.userLogin)) {
printHtmlPart(47)
invokeTag('message','g',170,['code':("person.userLogin.label"),'default':("User Login")],-1)
printHtmlPart(48)
createTagBody(3, {->
expressionOut.print(personInstance?.userLogin?.encodeAsHTML())
})
invokeTag('link','g',172,['controller':("userLogin"),'action':("show"),'id':(personInstance?.userLogin?.id)],3)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.homeEmail)) {
printHtmlPart(49)
invokeTag('message','g',179,['code':("person.homeEmail.label"),'default':("Home Email")],-1)
printHtmlPart(50)
invokeTag('fieldValue','g',181,['bean':(personInstance),'field':("homeEmail")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.workEmail)) {
printHtmlPart(51)
invokeTag('message','g',188,['code':("person.workEmail.label"),'default':("Work Email")],-1)
printHtmlPart(52)
invokeTag('fieldValue','g',190,['bean':(personInstance),'field':("workEmail")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.homePhone)) {
printHtmlPart(53)
invokeTag('message','g',197,['code':("person.homePhone.label"),'default':("Home Phone")],-1)
printHtmlPart(54)
invokeTag('fieldValue','g',199,['bean':(personInstance),'field':("homePhone")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.workPhone)) {
printHtmlPart(55)
invokeTag('message','g',206,['code':("person.workPhone.label"),'default':("Work Phone")],-1)
printHtmlPart(56)
invokeTag('fieldValue','g',208,['bean':(personInstance),'field':("workPhone")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.mobilePhone)) {
printHtmlPart(57)
invokeTag('message','g',215,['code':("person.mobilePhone.label"),'default':("Mobile Phone")],-1)
printHtmlPart(58)
invokeTag('fieldValue','g',217,['bean':(personInstance),'field':("mobilePhone")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.addressLine1)) {
printHtmlPart(59)
invokeTag('message','g',224,['code':("person.addressLine1.label"),'default':("Address Line1")],-1)
printHtmlPart(60)
invokeTag('fieldValue','g',226,['bean':(personInstance),'field':("addressLine1")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.addressLine2)) {
printHtmlPart(61)
invokeTag('message','g',233,['code':("person.addressLine2.label"),'default':("Address Line2")],-1)
printHtmlPart(62)
invokeTag('fieldValue','g',235,['bean':(personInstance),'field':("addressLine2")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.city)) {
printHtmlPart(63)
invokeTag('message','g',242,['code':("person.city.label"),'default':("City")],-1)
printHtmlPart(64)
invokeTag('fieldValue','g',244,['bean':(personInstance),'field':("city")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.state)) {
printHtmlPart(65)
invokeTag('message','g',251,['code':("person.state.label"),'default':("State")],-1)
printHtmlPart(66)
invokeTag('fieldValue','g',253,['bean':(personInstance),'field':("state")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.country)) {
printHtmlPart(67)
invokeTag('message','g',260,['code':("person.country.label"),'default':("Country")],-1)
printHtmlPart(68)
invokeTag('fieldValue','g',262,['bean':(personInstance),'field':("country")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.zip)) {
printHtmlPart(69)
invokeTag('message','g',269,['code':("person.zip.label"),'default':("Zip")],-1)
printHtmlPart(70)
invokeTag('fieldValue','g',271,['bean':(personInstance),'field':("zip")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.dateCreated)) {
printHtmlPart(71)
invokeTag('message','g',278,['code':("person.dateCreated.label"),'default':("Date Created")],-1)
printHtmlPart(72)
invokeTag('formatDate','g',280,['date':(personInstance?.dateCreated)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.lastUpdated)) {
printHtmlPart(73)
invokeTag('message','g',287,['code':("person.lastUpdated.label"),'default':("Last Updated")],-1)
printHtmlPart(74)
invokeTag('formatDate','g',289,['date':(personInstance?.lastUpdated)],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.comment)) {
printHtmlPart(75)
invokeTag('message','g',296,['code':("person.comment.label"),'default':("Comment")],-1)
printHtmlPart(76)
invokeTag('fieldValue','g',298,['bean':(personInstance),'field':("comment")],-1)
printHtmlPart(16)
}
printHtmlPart(17)
if(true && (personInstance?.userName)) {
printHtmlPart(77)
invokeTag('message','g',305,['code':("person.userName.label"),'default':("User Name")],-1)
printHtmlPart(78)
invokeTag('fieldValue','g',307,['bean':(personInstance),'field':("userName")],-1)
printHtmlPart(16)
}
printHtmlPart(79)
createTagBody(2, {->
printHtmlPart(80)
createTagBody(3, {->
invokeTag('message','g',315,['code':("default.button.edit.label"),'default':("Edit")],-1)
})
invokeTag('link','g',315,['class':("edit"),'action':("edit"),'resource':(personInstance)],3)
printHtmlPart(81)
invokeTag('actionSubmit','g',316,['class':("delete"),'action':("delete"),'value':(message(code: 'default.button.delete.label', default: 'Delete')),'onclick':("return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');")],-1)
printHtmlPart(82)
})
invokeTag('form','g',318,['url':([resource:personInstance, action:'delete']),'method':("DELETE")],2)
printHtmlPart(83)
})
invokeTag('captureBody','sitemesh',320,[:],1)
printHtmlPart(84)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1433807911833L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
