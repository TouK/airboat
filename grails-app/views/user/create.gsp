<%@ page import="airboat.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Register</title>
</head>

<body>
<div class="span6 well well-large">
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
                        .html($('#defaultFormErrorsTemplate').render([registration.errors]))
                        .hide().fadeIn();
                resizeColorbox();
            } else {
                alert("An error occured. Please file a bug using our feedback form.")
            }
        }, 'json');
        return false;
    }

    function resizeColorbox() {
        parent.$.fn.colorbox.resize({width:"650px", height:"486px"});
    }
</script>
</body>
</html>
