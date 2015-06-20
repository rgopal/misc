
<%@ page import="com.oumuo.central.Program" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'program.label', default: 'Program')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-program" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-program" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list program">
			
				<g:if test="${programInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="program.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${programInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.organization}">
				<li class="fieldcontain">
					<span id="organization-label" class="property-label"><g:message code="program.organization.label" default="Organization" /></span>
					
						<span class="property-value" aria-labelledby="organization-label"><g:link controller="organization" action="show" id="${programInstance?.organization?.id}">${programInstance?.organization?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.catalogs}">
				<li class="fieldcontain">
					<span id="catalogs-label" class="property-label"><g:message code="program.catalogs.label" default="Catalogs" /></span>
					
						<g:each in="${programInstance.catalogs}" var="c">
						<span class="property-value" aria-labelledby="catalogs-label"><g:link controller="catalog" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.rankings}">
				<li class="fieldcontain">
					<span id="rankings-label" class="property-label"><g:message code="program.rankings.label" default="Rankings" /></span>
					
						<g:each in="${programInstance.rankings}" var="r">
						<span class="property-value" aria-labelledby="rankings-label"><g:link controller="ranking" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.requirements}">
				<li class="fieldcontain">
					<span id="requirements-label" class="property-label"><g:message code="program.requirements.label" default="Requirements" /></span>
					
						<g:each in="${programInstance.requirements}" var="r">
						<span class="property-value" aria-labelledby="requirements-label"><g:link controller="requirement" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.learningAssessments}">
				<li class="fieldcontain">
					<span id="learningAssessments-label" class="property-label"><g:message code="program.learningAssessments.label" default="Learning Assessments" /></span>
					
						<g:each in="${programInstance.learningAssessments}" var="l">
						<span class="property-value" aria-labelledby="learningAssessments-label"><g:link controller="learningAssessment" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.courseRelations}">
				<li class="fieldcontain">
					<span id="courseRelations-label" class="property-label"><g:message code="program.courseRelations.label" default="Course Relations" /></span>
					
						<g:each in="${programInstance.courseRelations}" var="c">
						<span class="property-value" aria-labelledby="courseRelations-label"><g:link controller="courseRelation" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.authorships}">
				<li class="fieldcontain">
					<span id="authorships-label" class="property-label"><g:message code="program.authorships.label" default="Authorships" /></span>
					
						<g:each in="${programInstance.authorships}" var="a">
						<span class="property-value" aria-labelledby="authorships-label"><g:link controller="authorship" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.terms}">
				<li class="fieldcontain">
					<span id="terms-label" class="property-label"><g:message code="program.terms.label" default="Terms" /></span>
					
						<g:each in="${programInstance.terms}" var="t">
						<span class="property-value" aria-labelledby="terms-label"><g:link controller="term" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.studentPrograms}">
				<li class="fieldcontain">
					<span id="studentPrograms-label" class="property-label"><g:message code="program.studentPrograms.label" default="Student Programs" /></span>
					
						<g:each in="${programInstance.studentPrograms}" var="s">
						<span class="property-value" aria-labelledby="studentPrograms-label"><g:link controller="studentProgram" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.credential}">
				<li class="fieldcontain">
					<span id="credential-label" class="property-label"><g:message code="program.credential.label" default="Credential" /></span>
					
						<span class="property-value" aria-labelledby="credential-label"><g:fieldValue bean="${programInstance}" field="credential"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.academicStratum}">
				<li class="fieldcontain">
					<span id="academicStratum-label" class="property-label"><g:message code="program.academicStratum.label" default="Academic Stratum" /></span>
					
						<span class="property-value" aria-labelledby="academicStratum-label"><g:fieldValue bean="${programInstance}" field="academicStratum"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.academicMajor}">
				<li class="fieldcontain">
					<span id="academicMajor-label" class="property-label"><g:message code="program.academicMajor.label" default="Academic Major" /></span>
					
						<span class="property-value" aria-labelledby="academicMajor-label"><g:fieldValue bean="${programInstance}" field="academicMajor"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.minimumGrade}">
				<li class="fieldcontain">
					<span id="minimumGrade-label" class="property-label"><g:message code="program.minimumGrade.label" default="Minimum Grade" /></span>
					
						<span class="property-value" aria-labelledby="minimumGrade-label"><g:fieldValue bean="${programInstance}" field="minimumGrade"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.minimumPercentage}">
				<li class="fieldcontain">
					<span id="minimumPercentage-label" class="property-label"><g:message code="program.minimumPercentage.label" default="Minimum Percentage" /></span>
					
						<span class="property-value" aria-labelledby="minimumPercentage-label"><g:fieldValue bean="${programInstance}" field="minimumPercentage"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.academicSession}">
				<li class="fieldcontain">
					<span id="academicSession-label" class="property-label"><g:message code="program.academicSession.label" default="Academic Session" /></span>
					
						<span class="property-value" aria-labelledby="academicSession-label"><g:fieldValue bean="${programInstance}" field="academicSession"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.termFee}">
				<li class="fieldcontain">
					<span id="termFee-label" class="property-label"><g:message code="program.termFee.label" default="Term Fee" /></span>
					
						<span class="property-value" aria-labelledby="termFee-label"><g:fieldValue bean="${programInstance}" field="termFee"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.ranking}">
				<li class="fieldcontain">
					<span id="ranking-label" class="property-label"><g:message code="program.ranking.label" default="Ranking" /></span>
					
						<span class="property-value" aria-labelledby="ranking-label"><g:fieldValue bean="${programInstance}" field="ranking"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label"><g:message code="program.startDate.label" default="Start Date" /></span>
					
						<span class="property-value" aria-labelledby="startDate-label"><g:formatDate date="${programInstance?.startDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.endDate}">
				<li class="fieldcontain">
					<span id="endDate-label" class="property-label"><g:message code="program.endDate.label" default="End Date" /></span>
					
						<span class="property-value" aria-labelledby="endDate-label"><g:formatDate date="${programInstance?.endDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="program.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${programInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="program.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${programInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${programInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="program.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${programInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:programInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${programInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
