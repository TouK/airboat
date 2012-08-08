<%@ page import="codereview.User" %>
<!doctype html>
<html>
<head>
    <title>Register</title>

    <link media="screen" rel="stylesheet" href=" ${createLink(uri: '/css/bootstrap.css')}"/>
</head>

<body>
<div class="container">
    <div class="span6 offset2 well well-large">
        <h1>Register</h1>
        <g:form action="save" class="form-horizontal">
            <g:hasErrors bean="${command}">
                <ul class="alert-block" role="alert">
                    <g:eachError bean="${command}" var="error">
                        <li class="alert-error"
                            <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
            <fieldset>
                <div class="control-group">
                    <label for="emailInput" class="control-label">E-mail</label>

                    <div class="controls">
                        <input id="emailInput" type="email" name="email">
                    </div>
                </div>

                <div class="control-group">
                    <label for="passwordInput" class="control-label">Password</label>

                    <div class="controls">
                        <input id="passwordInput" type="password" name="password">
                    </div>
                </div>

                <div class="control-group">
                    <label for="passwordRepeatInput" class="control-label">Repeat password</label>

                    <div class="controls">
                        <input id="passwordRepeatInput" type="password" name="password2">
                    </div>
                </div>
            </fieldset>

            <div class="form-actions">
                <g:submitButton name="create" class="btn btn-primary" value="Register"/>
            </div>

        </g:form>
    </div></div>
</body>
</html>
