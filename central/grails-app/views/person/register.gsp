<!doctype html>
<html>
    <head>

        <title>User Registration</title>
    </head>
    <body id="body">
        <h1>Registration</h1>
        <p>Complete the form below to create an account!</p>
        <g:hasErrors bean="${person}">
            <div class="errors">
                <g:renderErrors bean="${person}">
                </g:renderErrors>
            </div>
        </g:hasErrors>
        <g:form action="register" name="registerForm">
            <div class="formField">
                <label for="login">Login:</label>
                <g:textField name="login" value="${person?.login}">
                </g:textField>
            </div>
            <div class="formField">
                <label for="password">Password:</label>
                <g:passwordField name="password" value="${person?.password}"></g:passwordField>
                </div>
                <div class="formField">
                    <label for="confirm">Confirm Password:</label>
                <g:passwordField name="confirm" value="${params?.confirm}"></g:passwordField>
                </div>
                <div class="formField">
                    <label for="Name">Name:</label>
                <g:textField name="name" value="${person?.name}"></g:textField>
                </div>

            <g:submitButton class="formButton" name="register" value="Register">
            </g:submitButton>
        </g:form>
    <body>   
</html>