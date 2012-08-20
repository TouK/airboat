<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>

    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-collapse.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-tooltip.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-popover.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-dropdown.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.md5.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.syntaxhighlighter/jquery.syntaxhighlighter.js')}"
            type="text/javascript"></script>
    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <script src="${createLink(uri: '/libs/jquery.zclip/jquery.zclip.js')}" type="text/javascript"></script>

    <link href="${createLink(uri: '/css/codereview.css')}" type="text/css" rel="stylesheet"/>

    <script src="${createLink(uri: '/js/codereview/comments.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/files.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/changesets.js')}" type="text/javascript"></script>
</head>

<body>

<div class="navbar logonavbar">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="#">
                CodeReview
            </a>
            <img src="${createLink(uri: '/images/cereal_guy.png')}" height="80" class="pull-left" style="padding: 5px;">

            <ul class="nav">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Project<b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:void(0)" data-target="#" onclick="showProject('')">All projects</a></li>
                        <g:each in="${projects}" var="project">
                            <li><a href="javascript:void(0)" data-target="#" onclick="showProject('${project.name}')">${project.name}</a></li>
                        </g:each>
                    </ul>
                </li>
            </ul>

            <ul class="nav pull-right">
                <li id="loginStatus"></li>
                <li>
                    <a href="https://docs.google.com/spreadsheet/viewform?formkey=dElrejNuNVUzNEt3LTJZQnVCQ3RILWc6MQ#gid=0"
                       target="_blank">Feedback</a>
                </li>
            </ul>
        </div>
    </div>
</div>

<script type="text/javascript">
    $.SyntaxHighlighter.init({
        stripEmptyStartFinishLines:false,
        baseUrl:'${createLink(uri: '/libs/jquery.syntaxhighlighter')}',
        prettifyBaseUrl:'${createLink(uri: '/libs/prettify')}'
    });
</script>

<div id="content" class="padding"></div>

<script type="text/javascript">

    $.views.helpers({
        getGravatar:function (email, size) {
            var size = size || 50;
            return 'http://www.gravatar.com/avatar/' + $.md5(email) + '.jpg?s=' + size;
        }
    });

    $().ready(function () {

        $.templates({
            loginStatusTemplate:"#loginStatusTemplate"
        });

        $.link.loginStatusTemplate('#loginStatus', codeReview);

        showProject('');

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });

        $(".colorbox").colorbox(codeReview.colorboxSettings);
        $('.dropdown-toggle').dropdown();
    });

    function onLoggedIn(username) {
        $.colorbox.close();
        $.observable(codeReview).setProperty('loggedInUserName', username);
    }
</script>

<script id="loginStatusTemplate" type="text/x-jsrender">
    <div class="textInNavbar">
        <div data-link="visible{: loggedInUserName !== '' }">
            %{--TODO use uri global variable when referencing a controller--}%
            Yo <a href="user/{{:loggedInUserName}}"
                  data-link="{:loggedInUserName} href{: 'user/' + loggedInUserName}"></a>!
        Wanna <g:link controller='logout'>log out</g:link>?
        </div>

        <div data-link="visible{: loggedInUserName === '' }">
            Hello, Unknown Wanderer! <g:link class='colorbox' url='login'>Login</g:link>
            or <g:link class='colorbox' url='register'>register</g:link>, maybe?
        </div>
    </div>
</script>


<script id="changesetTemplate" type="text/x-jsrender">

    <div class='row-fluid'>
        <div class="span5 well">
            <div class="row-fluid">
                <div class="pull-left margin-right">
                    <img src='{{>~getGravatar(email)}}'/>
                </div>

                <div class="pull-left">
                    <span class="badge {{if belongsToCurrentUser}}badge-success{{/if}}">{{>author}}</span>
                    <br/>
                    <span class="badge">{{>projectName}}</span>
                </div>

                <div class="pull-right">
                    <span class="pull-right badge badge-info">{{>date}}</span>
                    <br/>
                    <span class="pull-right badge badge-info" id="hash-{{>identifier}}">{{>shortIdentifier}}</span>
                </div>
            </div>

            <div class="row-fluid well-small commitMessage">{{>commitComment}}</div>

            <div id="more-button-{{>identifier}}">
                <a class="btn btn-primary btn-big" onclick="showChangesetDetails('{{>identifier}}')">
                    More...
                </a>
            </div>

            <div id="changesetDetails-{{>identifier}}" style="display:none;">
                <div class="accordion" id="accordion-{{>identifier}}"></div>

                <div class="comments" id="comments-{{>identifier}}"></div>

                <div id="comment-form-{{>identifier}}"></div>

                <div id="less-button-{{>identifier}}">
                    <a class="btn btn-primary btn-big" onclick="showLessAboutChangeset('{{>identifier}}')">Less...</a>
                </div>
            </div>
        </div>

        <div class="span7 well" id="content-files-span-{{>identifier}}" style="display: none;">
            <div id="content-files-title-{{>identifier}}"></div>
            <br/>

            <div id="diff-{{>identifier}}"></div>

            <div class="files-right">
                <div id="content-files-{{>identifier}}"></div>
            </div>
        </div>
    </div>
</script>

<script id="accordionFilesTemplate" type="text/x-jsrender">
    <div class="accordion-group" id="accordion-group-{{>collapseId}}">
        {{for [#data] tmpl='#accordionFileBodyTemplate'}}{{/for}}
    </div>
</script>

<script id="accordionFileBodyTemplate" type="text/x-jsrender">

    <div class="accordion-heading">
        <div class="row-fluid">
            <div class="row-fluid span9">
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-{{>changesetId}}"
                   href="#collapse-inner-{{>collapseId}}">
                    {{>name}}
                </a>
            </div>
            {{if howManyComments != 0}}
            <div class="row-fluid span3">
                <button class="btn btn disabled pull-right" style="margin:2px 5px 0px 5px"><i
                        class="icon-comment"></i>   {{>howManyComments}}</button>
            </div>
            {{/if}}
        </div>
    </div>

    <div id='collapse-inner-{{>collapseId}}' class="accordion-body collapse"
         data-changeset_id='{{>changesetId}}' data-file_id='{{>fileId}}'>
        <div class="accordion-inner" id="accordion-inner-{{>fileId}}">
            <div id="fileComments-{{>fileId}}"></div>
        </div>
    </div>
</script>

<script id="fileTitleTemplate" type="text/x-jsrender">

    <div class="row-fluid">
        <div class="span11">
            <h1>{{>fileName}}</h1>
        </div>

        <div class="span1">
            <button type="button" class="btn pull-right " onClick="hideFile('{{>changesetId}}', '{{>fileId}}')">
                <i class="icon-remove"></i>
            </button>
        </div>
    </div>
</script>

<script id="snippetTemplate" type="text/x-jsrender">
    <div id="div-comments-{{>fileId}}-{{>lineNumber}}"></div>

    <div id="snippet-{{>fileId}}-{{>lineNumber}}"></div>
</script>

<!-- FIXME reuse comment form template for both types of comments -->
<script id="addLineCommentFormTemplate" type="text/x-jsrender">

    <form class="add_comment .form-inline">
        <textarea id="add-line-comment-{{>fileId}}" placeholder="Add comment..."
                  style="height:80px;width:282px;"></textarea>

        <div class="btn-group pull-right">
            <button type="button" class="btn btn-primary" id="addCommentButton-{{>fileId}}"
                    onClick="addLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Add comment</button>
            <button type="button" class="btn btn-primary"
                    onClick="cancelLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Cancel</button>
        </div>
    </form>
</script>

<script id="commentFormTemplate" type="text/x-jsrender">

    <form id="commentForm-{{>identifier}}" class="add_comment">
        <textarea onfocus="expandCommentForm('{{>identifier}}',this)"
                  id="add-comment-{{>identifier}}" placeholder="Add comment..."
                  class="span12" rows="1"></textarea>
    </form>

    <div id="commentFormButtons-{{>identifier}}" class="btn-group pull-right" style="display: none;">
        <button type="button" class="btn btn-primary btnWarningBackground" id="addCommentButton-{{>identifier}}"
                onClick="addComment('{{>identifier}}')">Add comment</button>
        <button type="button" class="btn btn-primary" id="cancellButton-{{>identifier}}"
                onClick="resetCommentForm('{{>identifier}}')">Cancel</button>
    </div>
</script>

<script id="commentTemplate" type="text/x-jsrender">

    <div class="alert {{>fromRevision}}">
        <img src="{{>~getGravatar(author)}}"/>
        <span class="label {{if belongsToCurrentUser}}label-success{{/if}}">{{>author}}</span>
        <span class="label label-info pull-right">{{>dateCreated}}</span>

        <div class="comment-content">{{>text}}</div>
    </div>
</script>

<script id="diffTemplate" type="text/x-jsrender">

    <div class="row-fluid">
        <div class="span11 well-small">
            <button type="button" class="btn btn-primary" onClick="showDiff('{{>changesetId}}')"
                    id="button-showing-diff-{{>changesetId}}">Show diff</button>
            <button type="button" class="btn btn-primary" onClick="hideDiff('{{>changesetId}}')" style="display:none"
                    id="button-hiding-diff-{{>changesetId}}">Hide diff</button>
        </div>
    </div>

    <div id="diff-box-{{>changesetId}}" style="display:none">
    </div>
</script>

</body>
</html>