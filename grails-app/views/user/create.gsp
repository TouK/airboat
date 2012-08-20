<%@ page import="codereview.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Register</title>
</head>

<body>
<div class="container">
    <div class="span6 offset2 well well-large">
        <h1>Register</h1>
        <g:form name='createUserForm' action="save" class="form-horizontal">
            <div class="errors"></div>
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
    </div>
</div>

<script id="formErrorsTemplate" type="text/x-jsrender">
    <ul class="alert-block" role="alert">
        {{for #data}}
            <li class="alert-error" {{if field}}data-field-id="{{:field}}"{{/if}}>{{:message}}</li>
        {{/for}}
    </ul>
</script>

<script type='text/javascript'>
    /*TODO can't it be done easier?*/
    (function () {
        document.forms['createUserForm'].elements['email'].focus();
    })();

    $('#createUserForm').submit(registerViaAjax);

    function registerViaAjax() {
        $.post(this.action, $(this).serialize(), function (registration) {
            if (registration.success) {
                top.onLoggedIn(registration.username);
            } else if (registration.errors) {
                $('#createUserForm .errors')
                        .html($('#formErrorsTemplate').render([registration.errors]))
                        .hide().fadeIn()
            } else {
                alert("An error occured. Please file a bug using our feedback form.")
            }
        }, 'json');
        return false;
    }
</script>
</body>
</html>
