<%@ page import="codereview.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Reset password</title>
</head>

<body>
<div class="span6 well well-large">
    <h1>Reset password</h1>
    <g:form name='forgottenPasswordForm' action="sendMailToResetPassword" class="form-horizontal">
        <div class="form-messages"></div>
        <fieldset>
            <div class="control-group">
                <label for="emailInput" class="control-label">E-mail</label>

                <div class="controls">
                    <input id="emailInput" type="email" name="email">
                </div>
            </div>
        </fieldset>

        <div class="form-actions">
            <g:submitButton name="sendResetlink" class="btn btn-primary" value="Send mail with link"/>
        </div>

    </g:form>
</div>

<script type='text/javascript'>
    /*TODO can't it be done easier?*/
    (function () {
        resizeColorbox({width:"650px", height:"350px"})
        document.forms['forgottenPasswordForm'].elements['email'].focus();
    })();

    $('#forgottenPasswordForm').submit(registerViaAjax);

    function registerViaAjax() {
        $('.btn').prop('disabled', 'disabled');
        $('.btn').addClass('disabled');
        $('.btn').prop('value', 'Sending...');
        $.post(this.action, $(this).serialize(), function (reminder) {
            if (reminder.success) {
                $('#forgottenPasswordForm .form-messages')
                        .html($('#defaultFormSuccessTemplate').render([reminder]))
                        .hide().fadeIn();
                resizeColorbox({width:"650px", height:"430px"});
            } else if (reminder.errors) {
                $('#forgottenPasswordForm .form-messages')
                        .html($('#defaultFormErrorsTemplate').render([reminder.errors]))
                        .hide().fadeIn();
                resizeColorbox({width:"650px", height:"380px"});
            } else {
                alert("An error occured. Please file a bug using our feedback form.")
            }
            $('.btn').removeProp('disabled');
            $('.btn').removeClass('disabled');
            $('.btn').prop('value', 'Send mail with link');
        }, 'json');
        return false;
    }

    function resizeColorbox(size) {
        parent.$.fn.colorbox.resize(size);
    }
</script>
</body>
</html>
