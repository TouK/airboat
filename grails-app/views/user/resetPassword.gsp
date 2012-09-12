<%@ page import="codereview.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Reset password</title>
</head>

<body>

<div class="span12 well-large">
    <div class='container'>
        <div class="span7 offset4">
            Hello, ${username}!
            <h1>Reset password:</h1>

            <div class="span6 well-large well set-new-password-form">
                <h2>Set new password</h2>
                <g:form name='newPasswordForm' action="saveNewPassword" class="form-horizontal">
                    <div class="form-messages"></div>
                    <fieldset>

                        <div class="control-group">
                            <label for="newPassword" class="control-label">New password</label>

                            <div class="controls">
                                <input id="newPassword" type="password" name="newPassword">
                            </div>
                        </div>

                        <div class="control-group">
                            <label for="newPasswordRepeat" class="control-label">Repeat new password</label>

                            <div class="controls">
                                <input id="newPasswordRepeat" type="password" name="newPasswordRepeat">
                            </div>
                        </div>
                        <g:hiddenField name='token' value='${token}'/>
                    </fieldset>

                    <div class="form-actions">
                        <g:submitButton name="set-new-password" class="btn btn-primary" value="Set new password"/>
                    </div>

                </g:form>
            </div>

            <script type='text/javascript'>
                /*TODO can't it be done easier?*/
                (function () {
                    document.forms['newPasswordForm'].elements['newPassword'].focus();
                })();

                $('#newPasswordForm').submit(setNewPasswordViaAjax);

                function setNewPasswordViaAjax() {
                    $.post(this.action, $(this).serialize(), function (newPasswordSet) {
                        if (newPasswordSet.success) {
                            $('#newPasswordForm .form-messages')
                                    .html($('#defaultFormSuccessTemplate').render([newPasswordSet]))
                                    .hide().fadeIn();
                            resizeColorbox();
                            $('.btn').prop('disabled', 'disabled');
                            $('.btn').addClass('disabled');
                            $('.btn').prop('value', 'Password already set');
                        } else if (newPasswordSet.errors) {
                            $('#newPasswordForm .form-messages')
                                    .html($('#defaultFormErrorsTemplate').render([newPasswordSet.errors]))
                                    .hide().fadeIn();
                            resizeColorbox();
                        } else {
                            alert("An error occured. Please file a bug using our feedback form.")
                        }
                    }, 'json');
                    return false;
                }

                function resizeColorbox() {
                    parent.$.fn.colorbox.resize({width:"650px", height:"480px"});
                }
            </script>

        </div>
    </div>
</div>
</body>
</html>