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
                getFileNamesForChangeset:"${createLink(uri:'/changeset/getFileNamesForChangeset/')}"
            },
            userComment:{
                addComment:"${createLink(uri: '/userComment/addComment')}",
                returnCommentsToChangeset:"${createLink(uri:'/userComment/returnCommentsToChangeset/')}"
            },
            lineComment:{
                addComment:"${createLink(uri: '/lineComment/addComment')}"
            },
            projectFile:{
                getFileWithContent:"${createLink(uri:'/projectFile/getFileWithContent/')}",
                getLineCommentsWithSnippetsToFile:"${createLink(uri:'/projectFile/getLineCommentsWithSnippetsToFile/')}"
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
            colorboxSettings: {transition:"fade", iframe:true, width:640, height:420}

            , loggedInUserName:'<sec:username/>'

            , isAuthenticated:function(){
                return this.loggedInUserName !== '';
            }
        }

        //TODO move to codeReview object
        var hashAbbreviationLength = 8;
        var lineBoundary = 60; //TODO rename
    </script>

    <script src="${createLink(uri: '/libs/jquery-1.8.0.min.js')}" type="text/javascript"></script>
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

    <link href=" ${createLink(uri: '/libs/bootstrap/themes/default/bootstrap-default.css')}" type="text/css" rel="stylesheet" media="screen" id="skin"/>
    <link href=" ${createLink(uri: '/libs/bootstrap/bootstrap-responsive.css')}" type="text/css" rel="stylesheet"
          media="screen"/>

    <link href="${createLink(uri: '/css/codereview.css')}" type="text/css" rel="stylesheet"/>
    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <g:layoutHead/>
    <r:layoutResources/>
</head>

<body>
<g:layoutBody/>
<r:layoutResources/>
</body>
</html>