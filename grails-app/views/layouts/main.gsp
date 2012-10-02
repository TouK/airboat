<!doctype html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Airboat"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript">
        var uri = { //TODO de-duplicate
            changeset:{
                getLastChangesets:"${createLink(uri:'/changeset/getLastChangesets/')}",
                getNextFewChangesetsOlderThan:"${createLink(uri:'/changeset/getNextFewChangesetsOlderThan/')}",
                getNextFewChangesetsOlderThanFromSameProject:"${createLink(uri:'/changeset/getNextFewChangesetsOlderThanFromSameProject/')}",
                getLastFilteredChangesets:"${createLink(uri:'/changeset/getLastFilteredChangesets/')}",
                getNextFewFilteredChangesetsOlderThan: "${createLink(uri: '/changeset/getNextFewFilteredChangesetsOlderThan/')}"
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
                getFileListings:"${createLink(uri:'/projectFile/getFileListings/')}",
                getLineCommentsWithSnippetsToFile:"${createLink(uri:'/projectFile/getLineCommentsWithSnippetsToFile/')}"
            },
            user:{
                changePassword:"${createLink(uri: '/user/changePassword')}"
            },
            login:"${createLink(uri: '/login/auth')}",

            logout:"${createLink(uri: '/logout')}",
            register:"${createLink(uri: '/register')}",

            libs:{
                clippy:{
                    swf:"${createLink(uri: '/libs/clippy/clippy.swf')}"
                }
            }
        }

        var VIEW_TYPE = { SINGLE_CHANGESET:'changeset', PROJECT:'project', FILTER:'filter'};
        var DATA_TYPE = { CHANGESET:'changeset', PROJECT:'project', FILTER:'filter'};

        var airboat = {
            navbarOffset: 55

            , colorboxSettings:{transition:"fade", iframe:true, width:640, height:450}, loggedInUserName:'<sec:username/>', isAdmin:"<sec:ifAnyGranted roles="ROLE_ADMIN">true</sec:ifAnyGranted>" ? true : false, isAuthenticated:function () {
                return this.loggedInUserName !== '';
            }
            , displayedProjectName: ''

            , currentFilter: ''
            , currentViewType: null
            , shouldLoadChangesets: true

            , debugMode: true

            , error: function(message) {
                if (debugMode) {
                    $.error(message)
                }
            }, templates:{
                compileAll:function () {
                    $(arguments).each(function () {
                        airboat.templates.compile(this);
                    })
                }, compile:function (templateName) {
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

            , getModel:function (selectorOrElement) {
                var element
                if (typeof selectorOrElement === 'string')
                    element = $(selectorOrElement)[0];
                else {
                    element = selectorOrElement
                }
                return $.view(element).data
            }
        }

        //TODO move to airboat object
        var hashAbbreviationLength = 6;
        var lineBoundary = 60; //TODO rename
    </script>

    <script src="${createLink(uri: '/libs/jquery-1.8.0.min.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.livequery.js')}" type="text/javascript"></script>
    <link href=" ${createLink(uri: '/libs/jquery.colorbox/colorbox.css')}"
          type="text/css" rel="stylesheet" media="screen"/>
    <script src="${createLink(uri: '/libs/jquery.colorbox/jquery.colorbox-min.js')}" type="text/javascript"></script>
    <link rel="stylesheet" type="text/css" href="${createLink(uri: '/libs/jboesch-Gritter/css/jquery.gritter.css')}"/>
    <script type="text/javascript" src="${createLink(uri: '/libs/jboesch-Gritter/js/jquery.gritter.js')}"></script>

    <script id="unauthorizedErrorGlobalHandler" type="text/javascript">
        $('#unauthorizedErrorGlobalHandler').ajaxError(function (event, jqXHR) {
            var unauthorized = 401;
            if (jqXHR.status === unauthorized) {
                showLoginForm();
            }
        });

        function showLoginForm() {
            $.colorbox($.extend(airboat.colorboxSettings, {href:uri.login}));
        }
    </script>

    <script id="gritterOptions" type="text/javascript">
        $.extend($.gritter.options, {
            position: 'bottom-right', // defaults to 'top-right' but can be 'bottom-left', 'bottom-right', 'top-left', 'top-right' (added in 1.7.1)
            fade_in_speed: 'fast', // how fast notifications fade in (string or int)
            fade_out_speed: 500, // how fast the notices fade out
            time: 6000 // hang on the screen for...
        });
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
            if (!airboat.isAuthenticated()) {
                $.cookies.del('skin')
                $("#skin").attr("href", "${createLink(uri: '/libs/bootstrap/less/default/swatchmaker.less')}");
            }
        }
    </script>

    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <g:layoutHead/>
    <r:layoutResources/>

    <script src="${createLink(uri: '/libs/less-1.3.0.min.js')}" type="text/javascript" id="less"></script>

    <g:if test='${System.getProperty('airboat.googleAnalyticsAccountKey')}'>
        <script id='googleAnalytics' type="text/javascript">

            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', '${System.getProperty('airboat.googleAnalyticsAccountKey')}']);
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
