<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
    <title>Options</title>
</head>

<body>

<script id="skinOptionTemplate" type="text/x-jsrender">
    <li><a href="#" rel="{{>href}}" onclick="changeSkin('{{>href}}', '{{>name}}')">{{>name}}</a></li>
</script>

<script type="text/javascript">

    var baseUrl = "${createLink(uri: '/libs/bootstrap/themes/')}";

    var skins = [
        {href:baseUrl + 'default/bootstrap-default.css', name:"default"},
        {href:baseUrl + "cerulean/bootstrap-cerulean.css", name:"cerulean"},
        {href:baseUrl + "cyborg/bootstrap-cyborg.css", name:"cyborg"},
        {href:baseUrl + "journal/bootstrap-journal.css", name:"journal"},
        {href:baseUrl + "readable/bootstrap-readable.css", name:"readable"},
        {href:baseUrl + "simplex/bootstrap-simplex.css", name:"simplex"},
        {href:baseUrl + "slate/bootstrap-slate.css", name:"slate"},
        {href:baseUrl + "spacelab/bootstrap-spacelab.css", name:"spacelab"},
        {href:baseUrl + "united/bootstrap-united.css", name:"united"},
        {href:baseUrl + "touk/bootstrap-touk.css", name:"touk"}
    ]

    if ($.cookies.get('skin')) {
        $("#skin").attr("href", $.cookies.get('skin').href);
    }

    $(document).ready(function () {
        appendSkinOptions(skins);
    });

    function changeSkin(skinHref, skinName) {
        var skinOptions = {username:"anonymous", href:skinHref };
        var cookie = $.cookies.get('skin');
        if (!cookie) {
            $.cookies.set('skin', skinOptions);
        }
        else {
            skinOptions.username = cookie.username;
            $.cookies.set('skin', skinOptions);
        }
        $("#skin").attr("href", skinHref);
        changeUserSkin(skinName)
        return false;
    }

    function changeUserSkin(skinName) {
        var username = codeReview.loggedInUserName
        $.post("${createLink(uri: '/user/setSkinOptions/')}", { username:username, skin:skinName});
    }

    function appendSkinOptions(skins) {
        var skinOptions = $("#skinOptionTemplate").render(skins);
        $("#options").append(skinOptions);
    }

</script>

<div class="span12 well-large">
    <div class='container'>
        <div class="span7 offset4">
            <div class="span6 well-large well">

                <h2>Choose the skin you like:</h2>

                <ul id="options">
                </ul>
            </div>

            <div class="span6 well-large well change-password-form">
                <h2>Change password</h2>
                <g:form name='changePasswordForm' action="saveChangedPassword" class="form-horizontal">
                    <div class="form-messages"></div>
                    <fieldset>
                        <div class="control-group">
                            <label for="oldPassword" class="control-label">Old password</label>

                            <div class="controls">
                                <input id="oldPassword" type="password" name="oldPassword">
                            </div>
                        </div>

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
                    </fieldset>

                    <div class="form-actions">
                        <g:submitButton name="change-password" class="btn btn-primary" value="Change password"/>
                    </div>

                </g:form>
            </div>

            <script type='text/javascript'>
                /*TODO can't it be done easier?*/
                (function () {
                    document.forms['changePasswordForm'].elements['oldPassword'].focus();
                })();

                $('#changePasswordForm').submit(changePasswordViaAjax);

                function changePasswordViaAjax() {
                    $.post(this.action, $(this).serialize(), function (passwordChange) {
                        if (passwordChange.success) {
                            $('#changePasswordForm .form-messages')
                                    .html($('#defaultFormSuccessTemplate').render([passwordChange]))
                                    .hide().fadeIn();
                            resizeColorbox();
                        } else if (passwordChange.errors) {
                            $('#changePasswordForm .form-messages')
                                    .html($('#defaultFormErrorsTemplate').render([passwordChange.errors]))
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
            <a type="button" href="../../" class="btn btn-primary">Go back</a>

        </div>
    </div>
</div>
</body>
</html>