<%@ page import="com.oumuo.central.Bogus" %>



<div class="fieldcontain ${hasErrors(bean: bogusInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="bogus.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${bogusInstance?.name}"/>

</div>

