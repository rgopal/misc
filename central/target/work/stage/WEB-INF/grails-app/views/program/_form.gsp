<%@ page import="com.oumuo.central.Program" %>



<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="program.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${programInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'person', 'error')} required">
	<label for="person">
		<g:message code="program.person.label" default="Person" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="person" name="person.id" from="${com.oumuo.central.Person.findAllById(programInstance?.person?.id)}" optionKey="id" required="" value="${programInstance?.person?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'organization', 'error')} ">
	<label for="organization">
		<g:message code="program.organization.label" default="Organization" />
		
	</label>
	<g:select id="organization" name="organization.id" from="${com.oumuo.central.Organization.secureList()}" optionKey="id" value="${programInstance?.organization?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'catalogs', 'error')} ">
	<label for="catalogs">
		<g:message code="program.catalogs.label" default="Catalogs" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.catalogs?}" var="c">
    <li><g:link controller="catalog" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="catalog" action="create" params="['program.id': programInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'catalog.label', default: 'Catalog')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'rankings', 'error')} ">
	<label for="rankings">
		<g:message code="program.rankings.label" default="Rankings" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.rankings?}" var="r">
    <li><g:link controller="ranking" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="ranking" action="create" params="['program.id': programInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'ranking.label', default: 'Ranking')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'credential', 'error')} required">
	<label for="credential">
		<g:message code="program.credential.label" default="Credential" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="credential" from="${com.oumuo.lookup.Credential?.values()}" keys="${com.oumuo.lookup.Credential.values()*.name()}" required="" value="${programInstance?.credential?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicStratum', 'error')} required">
	<label for="academicStratum">
		<g:message code="program.academicStratum.label" default="Academic Stratum" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="academicStratum" from="${com.oumuo.lookup.AcademicStratum?.values()}" keys="${com.oumuo.lookup.AcademicStratum.values()*.name()}" required="" value="${programInstance?.academicStratum?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicMajor', 'error')} required">
	<label for="academicMajor">
		<g:message code="program.academicMajor.label" default="Academic Major" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="academicMajor" from="${com.oumuo.lookup.AcademicMajor?.values()}" keys="${com.oumuo.lookup.AcademicMajor.values()*.name()}" required="" value="${programInstance?.academicMajor?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'minimumGrade', 'error')} required">
	<label for="minimumGrade">
		<g:message code="program.minimumGrade.label" default="Minimum Grade" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="minimumGrade" from="${com.oumuo.lookup.Grade?.values()}" keys="${com.oumuo.lookup.Grade.values()*.name()}" required="" value="${programInstance?.minimumGrade?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'minimumPercentage', 'error')} ">
	<label for="minimumPercentage">
		<g:message code="program.minimumPercentage.label" default="Minimum Percentage" />
		
	</label>
	<g:field name="minimumPercentage" value="${fieldValue(bean: programInstance, field: 'minimumPercentage')}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicSession', 'error')} required">
	<label for="academicSession">
		<g:message code="program.academicSession.label" default="Academic Session" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="academicSession" from="${com.oumuo.lookup.AcademicSession?.values()}" keys="${com.oumuo.lookup.AcademicSession.values()*.name()}" required="" value="${programInstance?.academicSession?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'sessionFee', 'error')} ">
	<label for="sessionFee">
		<g:message code="program.sessionFee.label" default="Session Fee" />
		
	</label>
	<g:field name="sessionFee" value="${fieldValue(bean: programInstance, field: 'sessionFee')}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'ranking', 'error')} required">
	<label for="ranking">
		<g:message code="program.ranking.label" default="Ranking" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="ranking" from="${0..1000}" class="range" required="" value="${fieldValue(bean: programInstance, field: 'ranking')}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="program.startDate.label" default="Start Date" />
		
	</label>
	<input type="text" name="startDate_datepicker" id="startDate_datepicker" value="<g:formatDate date="${programInstance?.startDate}" formatName="dateonly.date.format" />"/><input type="hidden" name="startDate_day" id="startDate_day" value="<g:formatDate date="${programInstance?.startDate}" format="dd" />"/><input type="hidden" name="startDate_month" id="startDate_month" value="<g:formatDate date="${programInstance?.startDate}" format="MM" />"/><input type="hidden" name="startDate_year" id="startDate_year" value="<g:formatDate date="${programInstance?.startDate}" format="yyyy" />"/><g:javascript>
                          $(document).ready(function () {
                              $("#startDate_datepicker").datepicker({
                                  dateFormat:'dd/MM/yy',
                                  onClose: function(dateText, inst) {
                                          var date = new Date(dateText.replace(/(\d+).(\d+).(\d+)/, '$3/$2/$1'));
                                          if (!isNaN(date)) {
                                              $("#startDate_month").val(date.getMonth()+1);
                                              $("#startDate_day").val(date.getDate());
                                              $("#startDate_year").val(date.getFullYear());
                                          }
                                        },
                                  showAnim: "slide",
                                  showOptions: {direction: 'up'},
                                  showOn: "both",
                                  autoSize: true,
                                  constrainInput: true,
                                  showButtonPanel: true
                              });
                          })
                      </g:javascript>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="program.endDate.label" default="End Date" />
		
	</label>
	<input type="text" name="endDate_datepicker" id="endDate_datepicker" value="<g:formatDate date="${programInstance?.endDate}" formatName="dateonly.date.format" />"/><input type="hidden" name="endDate_day" id="endDate_day" value="<g:formatDate date="${programInstance?.endDate}" format="dd" />"/><input type="hidden" name="endDate_month" id="endDate_month" value="<g:formatDate date="${programInstance?.endDate}" format="MM" />"/><input type="hidden" name="endDate_year" id="endDate_year" value="<g:formatDate date="${programInstance?.endDate}" format="yyyy" />"/><g:javascript>
                          $(document).ready(function () {
                              $("#endDate_datepicker").datepicker({
                                  dateFormat:'dd/MM/yy',
                                  onClose: function(dateText, inst) {
                                          var date = new Date(dateText.replace(/(\d+).(\d+).(\d+)/, '$3/$2/$1'));
                                          if (!isNaN(date)) {
                                              $("#endDate_month").val(date.getMonth()+1);
                                              $("#endDate_day").val(date.getDate());
                                              $("#endDate_year").val(date.getFullYear());
                                          }
                                        },
                                  showAnim: "slide",
                                  showOptions: {direction: 'up'},
                                  showOn: "both",
                                  autoSize: true,
                                  constrainInput: true,
                                  showButtonPanel: true
                              });
                          })
                      </g:javascript>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'status', 'error')} required">
	<label for="status">
		<g:message code="program.status.label" default="Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="status" from="${com.oumuo.lookup.Status?.values()}" keys="${com.oumuo.lookup.Status.values()*.name()}" required="" value="${programInstance?.status?.name()}" />

</div>

