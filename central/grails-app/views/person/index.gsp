
<%@ page import="com.oumuo.central.Person" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'person.label', default: 'Person')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-person" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-person" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'person.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="sex" title="${message(code: 'person.sex.label', default: 'Sex')}" />
					
						<g:sortableColumn property="dateOfBirth" title="${message(code: 'person.dateOfBirth.label', default: 'Date Of Birth')}" />
					
						<g:sortableColumn property="race" title="${message(code: 'person.race.label', default: 'Race')}" />
					
						<g:sortableColumn property="status" title="${message(code: 'person.status.label', default: 'Status')}" />
					
						<g:sortableColumn property="preferredLanguage" title="${message(code: 'person.preferredLanguage.label', default: 'Preferred Language')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${personInstanceList}" status="i" var="personInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${personInstance.id}">${fieldValue(bean: personInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: personInstance, field: "sex")}</td>
					
						<td><g:formatDate date="${personInstance.dateOfBirth}" /></td>
					
						<td>${fieldValue(bean: personInstance, field: "race")}</td>
					
						<td>${fieldValue(bean: personInstance, field: "status")}</td>
					
						<td>${fieldValue(bean: personInstance, field: "preferredLanguage")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${personInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
