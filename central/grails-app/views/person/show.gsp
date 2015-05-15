
<%@ page import="com.oumuo.central.Person" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'person.label', default: 'Person')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-person" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-person" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list person">
			
				<g:if test="${personInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="person.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${personInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.personRoles}">
				<li class="fieldcontain">
					<span id="personRoles-label" class="property-label"><g:message code="person.personRoles.label" default="Person Roles" /></span>
					
						<g:each in="${personInstance.personRoles}" var="p">
						<span class="property-value" aria-labelledby="personRoles-label"><g:link controller="personRole" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.staffings}">
				<li class="fieldcontain">
					<span id="staffings-label" class="property-label"><g:message code="person.staffings.label" default="Staffings" /></span>
					
						<g:each in="${personInstance.staffings}" var="s">
						<span class="property-value" aria-labelledby="staffings-label"><g:link controller="staffing" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.accounts}">
				<li class="fieldcontain">
					<span id="accounts-label" class="property-label"><g:message code="person.accounts.label" default="Accounts" /></span>
					
						<g:each in="${personInstance.accounts}" var="a">
						<span class="property-value" aria-labelledby="accounts-label"><g:link controller="account" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.comments}">
				<li class="fieldcontain">
					<span id="comments-label" class="property-label"><g:message code="person.comments.label" default="Comments" /></span>
					
						<g:each in="${personInstance.comments}" var="c">
						<span class="property-value" aria-labelledby="comments-label"><g:link controller="comment" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.programs}">
				<li class="fieldcontain">
					<span id="programs-label" class="property-label"><g:message code="person.programs.label" default="Programs" /></span>
					
						<g:each in="${personInstance.programs}" var="p">
						<span class="property-value" aria-labelledby="programs-label"><g:link controller="program" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.catalogs}">
				<li class="fieldcontain">
					<span id="catalogs-label" class="property-label"><g:message code="person.catalogs.label" default="Catalogs" /></span>
					
						<g:each in="${personInstance.catalogs}" var="c">
						<span class="property-value" aria-labelledby="catalogs-label"><g:link controller="catalog" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.rankings}">
				<li class="fieldcontain">
					<span id="rankings-label" class="property-label"><g:message code="person.rankings.label" default="Rankings" /></span>
					
						<g:each in="${personInstance.rankings}" var="r">
						<span class="property-value" aria-labelledby="rankings-label"><g:link controller="ranking" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.rankingItems}">
				<li class="fieldcontain">
					<span id="rankingItems-label" class="property-label"><g:message code="person.rankingItems.label" default="Ranking Items" /></span>
					
						<g:each in="${personInstance.rankingItems}" var="r">
						<span class="property-value" aria-labelledby="rankingItems-label"><g:link controller="rankingItem" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.sex}">
				<li class="fieldcontain">
					<span id="sex-label" class="property-label"><g:message code="person.sex.label" default="Sex" /></span>
					
						<span class="property-value" aria-labelledby="sex-label"><g:fieldValue bean="${personInstance}" field="sex"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.dateOfBirth}">
				<li class="fieldcontain">
					<span id="dateOfBirth-label" class="property-label"><g:message code="person.dateOfBirth.label" default="Date Of Birth" /></span>
					
						<span class="property-value" aria-labelledby="dateOfBirth-label"><g:formatDate date="${personInstance?.dateOfBirth}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.race}">
				<li class="fieldcontain">
					<span id="race-label" class="property-label"><g:message code="person.race.label" default="Race" /></span>
					
						<span class="property-value" aria-labelledby="race-label"><g:fieldValue bean="${personInstance}" field="race"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="person.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${personInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.preferredLanguage}">
				<li class="fieldcontain">
					<span id="preferredLanguage-label" class="property-label"><g:message code="person.preferredLanguage.label" default="Preferred Language" /></span>
					
						<span class="property-value" aria-labelledby="preferredLanguage-label"><g:fieldValue bean="${personInstance}" field="preferredLanguage"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.userLogin}">
				<li class="fieldcontain">
					<span id="userLogin-label" class="property-label"><g:message code="person.userLogin.label" default="User Login" /></span>
					
						<span class="property-value" aria-labelledby="userLogin-label"><g:link controller="userLogin" action="show" id="${personInstance?.userLogin?.id}">${personInstance?.userLogin?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.homeEmail}">
				<li class="fieldcontain">
					<span id="homeEmail-label" class="property-label"><g:message code="person.homeEmail.label" default="Home Email" /></span>
					
						<span class="property-value" aria-labelledby="homeEmail-label"><g:fieldValue bean="${personInstance}" field="homeEmail"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.workEmail}">
				<li class="fieldcontain">
					<span id="workEmail-label" class="property-label"><g:message code="person.workEmail.label" default="Work Email" /></span>
					
						<span class="property-value" aria-labelledby="workEmail-label"><g:fieldValue bean="${personInstance}" field="workEmail"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.homePhone}">
				<li class="fieldcontain">
					<span id="homePhone-label" class="property-label"><g:message code="person.homePhone.label" default="Home Phone" /></span>
					
						<span class="property-value" aria-labelledby="homePhone-label"><g:fieldValue bean="${personInstance}" field="homePhone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.workPhone}">
				<li class="fieldcontain">
					<span id="workPhone-label" class="property-label"><g:message code="person.workPhone.label" default="Work Phone" /></span>
					
						<span class="property-value" aria-labelledby="workPhone-label"><g:fieldValue bean="${personInstance}" field="workPhone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.mobilePhone}">
				<li class="fieldcontain">
					<span id="mobilePhone-label" class="property-label"><g:message code="person.mobilePhone.label" default="Mobile Phone" /></span>
					
						<span class="property-value" aria-labelledby="mobilePhone-label"><g:fieldValue bean="${personInstance}" field="mobilePhone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.addressLine1}">
				<li class="fieldcontain">
					<span id="addressLine1-label" class="property-label"><g:message code="person.addressLine1.label" default="Address Line1" /></span>
					
						<span class="property-value" aria-labelledby="addressLine1-label"><g:fieldValue bean="${personInstance}" field="addressLine1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.addressLine2}">
				<li class="fieldcontain">
					<span id="addressLine2-label" class="property-label"><g:message code="person.addressLine2.label" default="Address Line2" /></span>
					
						<span class="property-value" aria-labelledby="addressLine2-label"><g:fieldValue bean="${personInstance}" field="addressLine2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.city}">
				<li class="fieldcontain">
					<span id="city-label" class="property-label"><g:message code="person.city.label" default="City" /></span>
					
						<span class="property-value" aria-labelledby="city-label"><g:fieldValue bean="${personInstance}" field="city"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.state}">
				<li class="fieldcontain">
					<span id="state-label" class="property-label"><g:message code="person.state.label" default="State" /></span>
					
						<span class="property-value" aria-labelledby="state-label"><g:fieldValue bean="${personInstance}" field="state"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.country}">
				<li class="fieldcontain">
					<span id="country-label" class="property-label"><g:message code="person.country.label" default="Country" /></span>
					
						<span class="property-value" aria-labelledby="country-label"><g:fieldValue bean="${personInstance}" field="country"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.zip}">
				<li class="fieldcontain">
					<span id="zip-label" class="property-label"><g:message code="person.zip.label" default="Zip" /></span>
					
						<span class="property-value" aria-labelledby="zip-label"><g:fieldValue bean="${personInstance}" field="zip"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="person.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${personInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="person.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${personInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.comment}">
				<li class="fieldcontain">
					<span id="comment-label" class="property-label"><g:message code="person.comment.label" default="Comment" /></span>
					
						<span class="property-value" aria-labelledby="comment-label"><g:fieldValue bean="${personInstance}" field="comment"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${personInstance?.userName}">
				<li class="fieldcontain">
					<span id="userName-label" class="property-label"><g:message code="person.userName.label" default="User Name" /></span>
					
						<span class="property-value" aria-labelledby="userName-label"><g:fieldValue bean="${personInstance}" field="userName"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:personInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${personInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
