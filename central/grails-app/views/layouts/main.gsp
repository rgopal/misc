<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> 
<html lang="en" class="no-js"><!--<![endif]-->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><g:layoutTitle default="Oumuo"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
        <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
        <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">

    <s2ui:resources module='register' />
    <%--

The 'resources' tag in SecurityUiTagLib renders these tags if you're not using the resources plugin:

	<g:javascript library='jquery' plugin='jquery' />
	<link rel="stylesheet" media="screen" href="${resource(dir:'css',file:'reset.css',plugin:'spring-security-ui')}"/>
	<link rel="stylesheet" media="screen" href="${resource(dir:'css',file:'spring-security-ui.css',plugin:'spring-security-ui')}"/>
	<jqui:resources />
	<link rel="stylesheet" media="screen" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.10.3.custom.css',plugin:'spring-security-ui')}"/>
	<link rel="stylesheet" media="screen" href="${resource(dir:'css',file:'jquery.jgrowl.css',plugin:'spring-security-ui')}"/>
	<link rel="stylesheet" media="screen" href="${resource(dir:'css',file:'jquery.safari-checkbox.css',plugin:'spring-security-ui')}"/>
	<link rel="stylesheet" media="screen" href="${resource(dir:'css',file:'auth.css',plugin:'spring-security-ui')}"/>

or these if you are:

   <r:require module="register"/>
   <r:layoutResources/>

If you need to customize the resources, replace the <s2ui:resources> tag with
the explicit tags above and edit those, not the taglib code.
--%>
        
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    <g:layoutHead/>
</head>
<body>
    <div id="grailsLogo" role="banner"><a href="http://oumuo.com"><asset:image src="grails_logo.png" alt="Oumuo"/></a>

    </div>

<ct:loginToggle />
<g:layoutBody/>

<div class="footer" role="contentinfo"></div>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>


</body>
</html>
