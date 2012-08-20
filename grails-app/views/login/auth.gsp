<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title></title>
</head>

<body>
<div class='container'>
    <div class="span6 offset2 well well-large">
        <h1><g:message code="springSecurity.login.header"/></h1>

        <form action='${postUrl}' method='POST' id='loginForm' class='form-horizontal' autocomplete='off'>
            <div class="errors"></div>
            <fieldset>
                <div class="control-group">
                    <label for="emailInput" class="control-label">E-mail</label>

                    <div class="controls">
                        <input id="emailInput" type="email" name="j_username">
                    </div>
                </div>

                <div class="control-group">
                    <label for="passwordInput" class="control-label">Password</label>

                    <div class="controls">
                        <input id="passwordInput" type="password" name="j_password">
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <label class="checkbox" for="rememberMeCheckbox">
                            <input type="checkbox" id="rememberMeCheckbox" value="${rememberMeParameter}">
                            <g:message code="springSecurity.login.remember.me.label"/>
                        </label>
                    </div>
                </div>
            </fieldset>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">${message(code: "springSecurity.login.button")}</button>
            </div>
        </form>
    </div>
</div>

<script id="formErrorTemplate" type='text/x-jsrender'>
    <div class='alert alert-block'>{{: #data }}</div>
</script>

<script type='text/javascript'>
    /*TODO can't it be done easier?*/
    (function () {
        document.forms['loginForm'].elements['j_username'].focus();
    })();

    $('#loginForm').submit(authAjax);

    function authAjax() {
        $.post(this.action, $(this).serialize(), function (login) {
            if (login.success) {
                top.onLoggedIn(login.username);
            } else if (login.error) {
                $('#loginForm .errors')
                        .html($('#formErrorTemplate').render(login.error))
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
