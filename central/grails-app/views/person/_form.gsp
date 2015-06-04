<%@ page import="com.oumuo.central.Person" %>



<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="person.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" maxlength="64" required="" value="${personInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'personRoles', 'error')} ">
	<label for="personRoles">
		<g:message code="person.personRoles.label" default="Person Roles" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.personRoles?}" var="p">
    <li><g:link controller="personRole" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="personRole" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'personRole.label', default: 'PersonRole')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'staffings', 'error')} ">
	<label for="staffings">
		<g:message code="person.staffings.label" default="Staffings" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.staffings?}" var="s">
    <li><g:link controller="staffing" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="staffing" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'staffing.label', default: 'Staffing')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'accounts', 'error')} ">
	<label for="accounts">
		<g:message code="person.accounts.label" default="Accounts" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.accounts?}" var="a">
    <li><g:link controller="account" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="account" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'account.label', default: 'Account')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'comments', 'error')} ">
	<label for="comments">
		<g:message code="person.comments.label" default="Comments" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.comments?}" var="c">
    <li><g:link controller="comment" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="comment" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'comment.label', default: 'Comment')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'programs', 'error')} ">
	<label for="programs">
		<g:message code="person.programs.label" default="Programs" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.programs?}" var="p">
    <li><g:link controller="program" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="program" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'program.label', default: 'Program')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'catalogs', 'error')} ">
	<label for="catalogs">
		<g:message code="person.catalogs.label" default="Catalogs" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.catalogs?}" var="c">
    <li><g:link controller="catalog" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="catalog" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'catalog.label', default: 'Catalog')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'rankings', 'error')} ">
	<label for="rankings">
		<g:message code="person.rankings.label" default="Rankings" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.rankings?}" var="r">
    <li><g:link controller="ranking" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="ranking" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'ranking.label', default: 'Ranking')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'rankingItems', 'error')} ">
	<label for="rankingItems">
		<g:message code="person.rankingItems.label" default="Ranking Items" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.rankingItems?}" var="r">
    <li><g:link controller="rankingItem" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="rankingItem" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'rankingItem.label', default: 'RankingItem')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'sex', 'error')} required">
	<label for="sex">
		<g:message code="person.sex.label" default="Sex" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="sex" from="${com.oumuo.central.Person$Sex?.values()}" keys="${com.oumuo.central.Person$Sex.values()*.name()}" required="" value="${personInstance?.sex?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'dateOfBirth', 'error')} ">
	<label for="dateOfBirth">
		<g:message code="person.dateOfBirth.label" default="Date Of Birth" />
		
	</label>
	<input type="text" name="dateOfBirth_datepicker" id="dateOfBirth_datepicker" value="<g:formatDate date="${personInstance?.dateOfBirth}" formatName="dateonly.date.format" />"/><input type="hidden" name="dateOfBirth_day" id="dateOfBirth_day" value="<g:formatDate date="${personInstance?.dateOfBirth}" format="dd" />"/><input type="hidden" name="dateOfBirth_month" id="dateOfBirth_month" value="<g:formatDate date="${personInstance?.dateOfBirth}" format="MM" />"/><input type="hidden" name="dateOfBirth_year" id="dateOfBirth_year" value="<g:formatDate date="${personInstance?.dateOfBirth}" format="yyyy" />"/><g:javascript>
                          $(document).ready(function () {
                              $("#dateOfBirth_datepicker").datepicker({
                                  dateFormat:'dd/MM/yy',
                                  onClose: function(dateText, inst) {
                                          var date = new Date(dateText.replace(/(\d+).(\d+).(\d+)/, '$3/$2/$1'));
                                          if (!isNaN(date)) {
                                              $("#dateOfBirth_month").val(date.getMonth()+1);
                                              $("#dateOfBirth_day").val(date.getDate());
                                              $("#dateOfBirth_year").val(date.getFullYear());
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

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'race', 'error')} ">
	<label for="race">
		<g:message code="person.race.label" default="Race" />
		
	</label>
	<g:select name="race" from="${com.oumuo.lookup.Race?.values()}" keys="${com.oumuo.lookup.Race.values()*.name()}" value="${personInstance?.race?.name()}"  noSelection="['': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'status', 'error')} required">
	<label for="status">
		<g:message code="person.status.label" default="Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="status" from="${com.oumuo.lookup.Status?.values()}" keys="${com.oumuo.lookup.Status.values()*.name()}" required="" value="${personInstance?.status?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'preferredLanguage', 'error')} required">
	<label for="preferredLanguage">
		<g:message code="person.preferredLanguage.label" default="Preferred Language" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="preferredLanguage" from="${com.oumuo.lookup.Language?.values()}" keys="${com.oumuo.lookup.Language.values()*.name()}" required="" value="${personInstance?.preferredLanguage?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'userLogin', 'error')} required">
	<label for="userLogin">
		<g:message code="person.userLogin.label" default="User Login" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="userLogin" name="userLogin.id" from="${com.oumuo.UserLogin.findAllById(personInstance?.userLogin?.id)}" optionKey="id" required="" value="${personInstance?.userLogin?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'homeEmail', 'error')} ">
	<label for="homeEmail">
		<g:message code="person.homeEmail.label" default="Home Email" />
		
	</label>
	<g:field type="email" name="homeEmail" value="${personInstance?.homeEmail}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'workEmail', 'error')} ">
	<label for="workEmail">
		<g:message code="person.workEmail.label" default="Work Email" />
		
	</label>
	<g:field type="email" name="workEmail" value="${personInstance?.workEmail}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'homePhone', 'error')} ">
	<label for="homePhone">
		<g:message code="person.homePhone.label" default="Home Phone" />
		
	</label>
	<g:textField name="homePhone" pattern="${personInstance.constraints.homePhone.matches}" value="${personInstance?.homePhone}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'workPhone', 'error')} ">
	<label for="workPhone">
		<g:message code="person.workPhone.label" default="Work Phone" />
		
	</label>
	<g:textField name="workPhone" pattern="${personInstance.constraints.workPhone.matches}" value="${personInstance?.workPhone}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'mobilePhone', 'error')} ">
	<label for="mobilePhone">
		<g:message code="person.mobilePhone.label" default="Mobile Phone" />
		
	</label>
	<g:textField name="mobilePhone" pattern="${personInstance.constraints.mobilePhone.matches}" value="${personInstance?.mobilePhone}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'addressLine1', 'error')} ">
	<label for="addressLine1">
		<g:message code="person.addressLine1.label" default="Address Line1" />
		
	</label>
	<g:textField name="addressLine1" value="${personInstance?.addressLine1}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'addressLine2', 'error')} ">
	<label for="addressLine2">
		<g:message code="person.addressLine2.label" default="Address Line2" />
		
	</label>
	<g:textField name="addressLine2" value="${personInstance?.addressLine2}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'city', 'error')} ">
	<label for="city">
		<g:message code="person.city.label" default="City" />
		
	</label>
	<g:textField name="city" value="${personInstance?.city}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'state', 'error')} ">
	<label for="state">
		<g:message code="person.state.label" default="State" />
		
	</label>
	<g:textField name="state" value="${personInstance?.state}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'country', 'error')} required">
	<label for="country">
		<g:message code="person.country.label" default="Country" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="country" from="${com.oumuo.lookup.Country?.values()}" keys="${com.oumuo.lookup.Country.values()*.name()}" required="" value="${personInstance?.country?.name()}" />

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'zip', 'error')} ">
	<label for="zip">
		<g:message code="person.zip.label" default="Zip" />
		
	</label>
	<g:textField name="zip" maxlength="10" value="${personInstance?.zip}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'comment', 'error')} ">
	<label for="comment">
		<g:message code="person.comment.label" default="Comment" />
		
	</label>
	<g:textArea name="comment" cols="40" rows="5" maxlength="1000" value="${personInstance?.comment}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'userName', 'error')} required">
	<label for="userName">
		<g:message code="person.userName.label" default="User Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="userName" required="" value="${personInstance?.userName}"/>

</div>

