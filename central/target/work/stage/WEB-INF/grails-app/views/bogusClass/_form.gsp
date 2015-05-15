<%@ page import="central.BogusClass" %>



<div class="fieldcontain ${hasErrors(bean: bogusClassInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="bogusClass.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${bogusClassInstance?.name}"/>

</div>

