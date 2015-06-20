<%@ page import="com.oumuo.central.Program" %>



<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="program.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${programInstance?.name}"/>

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
<g:link controller="catalog" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'catalogs' ]">${message(code: 'default.add.label', args: [message(code: 'catalog.label', default: 'Catalog')])}</g:link>
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
<g:link controller="ranking" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'rankings' ]">${message(code: 'default.add.label', args: [message(code: 'ranking.label', default: 'Ranking')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'requirements', 'error')} ">
	<label for="requirements">
		<g:message code="program.requirements.label" default="Requirements" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.requirements?}" var="r">
    <li><g:link controller="requirement" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="requirement" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'requirements' ]">${message(code: 'default.add.label', args: [message(code: 'requirement.label', default: 'Requirement')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'learningAssessments', 'error')} ">
	<label for="learningAssessments">
		<g:message code="program.learningAssessments.label" default="Learning Assessments" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.learningAssessments?}" var="l">
    <li><g:link controller="learningAssessment" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="learningAssessment" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'learningAssessments' ]">${message(code: 'default.add.label', args: [message(code: 'learningAssessment.label', default: 'LearningAssessment')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'courseRelations', 'error')} ">
	<label for="courseRelations">
		<g:message code="program.courseRelations.label" default="Course Relations" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.courseRelations?}" var="c">
    <li><g:link controller="courseRelation" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="courseRelation" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'courseRelations' ]">${message(code: 'default.add.label', args: [message(code: 'courseRelation.label', default: 'CourseRelation')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'authorships', 'error')} ">
	<label for="authorships">
		<g:message code="program.authorships.label" default="Authorships" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.authorships?}" var="a">
    <li><g:link controller="authorship" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="authorship" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'authorships' ]">${message(code: 'default.add.label', args: [message(code: 'authorship.label', default: 'Authorship')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'terms', 'error')} ">
	<label for="terms">
		<g:message code="program.terms.label" default="Terms" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.terms?}" var="t">
    <li><g:link controller="term" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="term" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'terms' ]">${message(code: 'default.add.label', args: [message(code: 'term.label', default: 'Term')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'studentPrograms', 'error')} ">
	<label for="studentPrograms">
		<g:message code="program.studentPrograms.label" default="Student Programs" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${programInstance?.studentPrograms?}" var="s">
    <li><g:link controller="studentProgram" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="studentProgram" action="create" params="['program.id': programInstance?.id, oneToManyPropertyName: 'studentPrograms' ]">${message(code: 'default.add.label', args: [message(code: 'studentProgram.label', default: 'StudentProgram')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'credential', 'error')} ">
	<label for="credential">
		<g:message code="program.credential.label" default="Credential" />
		
	</label>
	<g:select name="credential" from="${com.oumuo.lookup.Credential?.values()}" keys="${com.oumuo.lookup.Credential.values()*.name()}" value="${programInstance?.credential?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicStratum', 'error')} ">
	<label for="academicStratum">
		<g:message code="program.academicStratum.label" default="Academic Stratum" />
		
	</label>
	<g:select name="academicStratum" from="${com.oumuo.lookup.AcademicStratum?.values()}" keys="${com.oumuo.lookup.AcademicStratum.values()*.name()}" value="${programInstance?.academicStratum?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicMajor', 'error')} ">
	<label for="academicMajor">
		<g:message code="program.academicMajor.label" default="Academic Major" />
		
	</label>
	<g:select name="academicMajor" from="${com.oumuo.lookup.AcademicMajor?.values()}" keys="${com.oumuo.lookup.AcademicMajor.values()*.name()}" value="${programInstance?.academicMajor?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'minimumGrade', 'error')} ">
	<label for="minimumGrade">
		<g:message code="program.minimumGrade.label" default="Minimum Grade" />
		
	</label>
	<g:select name="minimumGrade" from="${com.oumuo.lookup.Grade?.values()}" keys="${com.oumuo.lookup.Grade.values()*.name()}" value="${programInstance?.minimumGrade?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'minimumPercentage', 'error')} ">
	<label for="minimumPercentage">
		<g:message code="program.minimumPercentage.label" default="Minimum Percentage" />
		
	</label>
	<g:field name="minimumPercentage" value="${fieldValue(bean: programInstance, field: 'minimumPercentage')}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'academicSession', 'error')} ">
	<label for="academicSession">
		<g:message code="program.academicSession.label" default="Academic Session" />
		
	</label>
	<g:select name="academicSession" from="${com.oumuo.lookup.AcademicSession?.values()}" keys="${com.oumuo.lookup.AcademicSession.values()*.name()}" value="${programInstance?.academicSession?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'termFee', 'error')} ">
	<label for="termFee">
		<g:message code="program.termFee.label" default="Term Fee" />
		
	</label>
	<g:field name="termFee" value="${fieldValue(bean: programInstance, field: 'termFee')}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: programInstance, field: 'ranking', 'error')} ">
	<label for="ranking">
		<g:message code="program.ranking.label" default="Ranking" />
		
	</label>
	<g:select name="ranking" from="${0..1000}" class="range" value="${fieldValue(bean: programInstance, field: 'ranking')}" noSelection="['': '']"/>

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

