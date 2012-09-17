<!doctype html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="CodeReview"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript">
        var uri = { //TODO de-duplicate
            changeset:{
                getLastChangesets:"${createLink(uri:'/changeset/getLastChangesets/')}",
                getNextFewChangesetsOlderThan:"${createLink(uri:'/changeset/getNextFewChangesetsOlderThan/')}",
                getNextFewChangesetsOlderThanFromSameProject:"${createLink(uri:'/changeset/getNextFewChangesetsOlderThanFromSameProject/')}"
            },
            userComment:{
                addComment:"${createLink(uri: '/userComment/addComment')}"
            },
            lineComment:{
                checkCanAddComment:"${createLink(uri: '/lineComment/checkCanAddComment')}",
                addComment:"${createLink(uri: '/lineComment/addComment')}",
                addReply:"${createLink(uri: '/lineComment/addReply')}"
            },
            projectFile:{
                getFileWithContent:"${createLink(uri:'/projectFile/getFileWithContent/')}",
                getLineCommentsWithSnippetsToFile:"${createLink(uri:'/projectFile/getLineCommentsWithSnippetsToFile/')}",
                getDiffWithPreviousRevision:"${createLink(uri:'/projectFile/getDiffWithPreviousRevision/')}"
            },
            user:{
                changePassword:"${createLink(uri: '/user/changePassword')}"
            },
            login:"${createLink(uri: '/login/auth')}",

            logout:"${createLink(uri: '/logout')}",
            register:"${createLink(uri: '/register')}",

            libs:{
                zclip:{
                    swf:"${createLink(uri: '/libs/jquery.zclip/ZeroClipboard.swf')}"
                }
            }
        }

        var codeReview = {
            colorboxSettings:{transition:"fade", iframe:true, width:640, height:450}, loggedInUserName:'<sec:username/>', isAdmin:"<sec:ifAnyGranted roles="ROLE_ADMIN">true</sec:ifAnyGranted>" ? true : false, isAuthenticated:function () {
                return this.loggedInUserName !== '';
            }
            , displayedProjectName: ''

            , debugMode: true

            , error: function(message) {
                if (debugMode) {
                    $.error(message)
                }
            }

            , templates: {
                compileAll: function() {
                    $(arguments).each(function () {
                        codeReview.templates.compile(this);
                    })
                }

                , compile:function (templateName) {
                    templateName += 'Template'
                    var templateId = '#' + templateName;
                    if ($(templateId).size() != 1) {
                        $.error('Template ' + templateId + ' not found')
                    } else {
                        var map = {}
                        map[templateName] = templateId
                        $.templates(map)
                    }
                }

            }

            , getModel:function (selector) {
                return $.view($(selector)[0]).data
            }
        }

        //TODO move to codeReview object
        var hashAbbreviationLength = 6;
        var lineBoundary = 60; //TODO rename
    </script>

    <script src="${createLink(uri: '/libs/jquery-1.8.0.min.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.livequery.js')}" type="text/javascript"></script>
    <link href=" ${createLink(uri: '/libs/jquery.colorbox/colorbox.css')}"
          type="text/css" rel="stylesheet" media="screen"/>
    <script src="${createLink(uri: '/libs/jquery.colorbox/jquery.colorbox-min.js')}" type="text/javascript"></script>

    <script id="unauthorizedErrorGlobalHandler" type="text/javascript">
        $('#unauthorizedErrorGlobalHandler').ajaxError(function (event, jqXHR) {
            var unauthorized = 401;
            if (jqXHR.status === unauthorized) {
                showLoginForm();
            }
        });

        function showLoginForm() {
            $.colorbox($.extend(codeReview.colorboxSettings, {href:uri.login}));
        }
    </script>

    %{--TODO make use of <g:javascript library="library_name"/> tag? --}%
    <script src="${createLink(uri: '/libs/jsrender.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.observable.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.views.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>

    <link href=" ${createLink(uri: '/libs/bootstrap/less/default/swatchmaker.less')}" type="text/less"
          rel="stylesheet" media="screen" id="skin"/>
    <link href=" ${createLink(uri: '/libs/bootstrap/less/responsive.less')}" type="text/less" rel="stylesheet"
          media="screen"/>
    <script type="text/javascript">
        if ($.cookies.get('skin')) {
            $("#skin").attr("href", $.cookies.get('skin').href);
            if (!codeReview.isAuthenticated()) {
                $.cookies.del('skin')
                $("#skin").attr("href", "${createLink(uri: '/libs/bootstrap/less/default/swatchmaker.less')}");
            }
        }
    </script>
    <script src="${createLink(uri: '/libs/less-1.3.0.min.js')}" type="text/javascript" id="less"></script>

    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <g:layoutHead/>
    <r:layoutResources/>

    <g:if test='${System.getProperty('codereview.googleAnalyticsAccountKey')}'>
        <script id='googleAnalytics' type="text/javascript">

            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '${System.getProperty('codereview.googleAnalyticsAccountKey')}']);
            _gaq.push(['_trackPageview']);

            (function () {
                var ga = document.createElement('script');
                ga.type = 'text/javascript';
                ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(ga, s);
            })();
        </script>
    </g:if>
</head>

<body>
<script id="defaultFormErrorsTemplate" type="text/x-jsrender">
    <ul class="alert-block" role="alert">
        {{for #data}}
        <li class="alert-error" {{if field}}data-field-id="{{:field}}"{{/if}}>{{:message}}</li>
        {{/for}}
    </ul>
</script>

<script id="defaultFormSuccessTemplate" type='text/x-jsrender'>
    <div class='alert alert-block alert-success'>{{: #data.message }}</div>
</script>
<g:layoutBody/>
<r:layoutResources/>
</body>
</html>