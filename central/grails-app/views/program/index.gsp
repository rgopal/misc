
<%@ page import="com.oumuo.central.Program" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'program.label', default: 'Program')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-program" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-program" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'program.name.label', default: 'Name')}" />
					
						<th><g:message code="program.person.label" default="Person" /></th>
					
						<th><g:message code="program.organization.label" default="Organization" /></th>
					
						<g:sortableColumn property="credential" title="${message(code: 'program.credential.label', default: 'Credential')}" />
					
						<g:sortableColumn property="academicStratum" title="${message(code: 'program.academicStratum.label', default: 'Academic Stratum')}" />
					
						<g:sortableColumn property="academicMajor" title="${message(code: 'program.academicMajor.label', default: 'Academic Major')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${programInstanceList}" status="i" var="programInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${programInstance.id}">${fieldValue(bean: programInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: programInstance, field: "person")}</td>
					
						<td>${fieldValue(bean: programInstance, field: "organization")}</td>
					
						<td>${fieldValue(bean: programInstance, field: "credential")}</td>
					
						<td>${fieldValue(bean: programInstance, field: "academicStratum")}</td>
					
						<td>${fieldValue(bean: programInstance, field: "academicMajor")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${programInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
