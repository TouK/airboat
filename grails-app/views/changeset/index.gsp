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
    <script src="${createLink(uri: '/libs/jquery.scrollto.min.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.ba-throttle-debounce.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.sizes.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.floatWithin.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/codereview/comments.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/files.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/changesets.js')}" type="text/javascript"></script>
</head>

<body>
<div class="underNavbar">

<div class="test">

</div>

<div class="navbar navbar-fixed-top navbar-inverse">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="#">
                <span class='brandlogo'>
                    CodeReview
                </span>
            </a>
            <span id='projectChooser'></span>
            <ul class='nav pull-right'>
                <span id="loginStatus"></span>
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

<div class="padding">
    <div id="content"></div>
</div>

<div class="alert alert-info" id="loading" style='display: none;'>
    <div class="well-small"><img id="loading-image" src="${createLink(uri: '/css/images/ajax-loader.gif')}"/> Loading...
    </div>
</div>

<script type="text/javascript">

    $('body').on('click', '.changeset .basicInfo, .changeset .details .lessButton', function () {
        var changeset = $(this).parents('.changeset').first();
        toggleChangesetDetails(changeset[0].dataset.identifier);
    });

    $.views.helpers({
        getGravatar:function (email, size) {
            var size = size || 50;
            return 'http://www.gravatar.com/avatar/' + $.md5(email) + '.jpg?' + $.param({
                s:size,
                d:'identicon'
            });
        }, colorForProjectName:function (projectName) {
            var md5hash = $.md5(projectName);
            return  colorFromMd5Hash(md5hash.substr(0, 12));
        }, iconForChangeType:function (changeType) {
            return iconForChangeType[changeType]
        }, textForChangeType:function (changeType) {
            return textForChangeType[changeType]
        }
    });

    iconForChangeType = {
        ADD:'icon-plus',
        DELETE:'icon-minus',
        MODIFY:'icon-edit',
        RENAME:'icon-pencil',
        COPY:'icon-move'
    }

    textForChangeType = {
        ADD:'added',
        DELETE:'deleted',
        MODIFY:'modified',
        RENAME:'renamed',
        COPY:'copied'
    }

    function colorFromMd5Hash(md5hash) {
        var colorCount = 18
        var numberOfHuesInHSL = 360;
        var color = (numberOfHuesInHSL / colorCount) * (parseInt(md5hash, 16) % colorCount)
        return "hsl(" + color + ", 50%, 50%)"
    }

    $().ready(function () {
        codeReview.initialFirstChangesetOffset = $('#content').position().top

        codeReview.templates.compileAll('loginStatus', 'changeset', 'comment', 'projectChooser');

        $.link.loginStatusTemplate('#loginStatus', codeReview, {target:'replace'});
        $.link.projectChooserTemplate('#projectChooser', codeReview, {target:'replace'});

        if (toBoolean(${singleChangeset})) {
            appendChangesetsBottom(${changeset});
            toggleChangesetDetails("${changesetId}");
        } else {

            if (toBoolean(${singleProject})) {
                showProject("${project}");
            } else {
                showProject('');
            }


            $(window).scroll(function () {
                if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                    onScrollThroughBottomAttempt()
                }
            });
        }

        $(".colorbox").colorbox(codeReview.colorboxSettings);
        $('.dropdown-toggle').dropdown();

        $('body').on('codeReview-pageStructureChanged', repositionZclips)
    });

    $(document)
            .ajaxStart(function () {
                $('#loading').show();
            }).ajaxStop(function () {
                $('#loading').hide();
                $('body').trigger('codeReview-pageStructureChanged') //most probably
            });

    function onLoggedIn(username, isAdmin) {
        isAdmin = isAdmin ? true : false;
        $.colorbox.close();
        $.observable(codeReview).setProperty('loggedInUserName', username);
        $.observable(codeReview).setProperty('isAdmin', isAdmin);
        setUserPreferences(username);
    }

    function setUserPreferences(username) {
        var url = "${createLink(uri: '/user/fetchSkinOptions/')}" + username;
        $.getJSON(url, function (skinOptions) {
            var skinHref = "${createLink(uri: '/libs/bootstrap/less/')}" + skinOptions.skin + "/swatchmaker.less";
            var skinOptions = {username:username, href:skinHref };
            $.cookies.set('skin', skinOptions);
            $("#skin").attr("href", $.cookies.get('skin').href);
        });
    }

    function repositionZclips() {
        for (var i = 1; i < ZeroClipboard.nextId; i++) {
            ZeroClipboard.clients[i].reposition()
        }
    }
</script>

<script id='projectChooserTemplate' type='text/x-jsrender'>

    <ul class="nav">
        <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Project <span
                    data-link='displayedProjectName'></span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" data-target="#" onclick="showProject('')">All projects</a></li>
                <g:each in="${projects}" var="project">
                    <li><a href="javascript:void(0)" data-target="#"
                           onclick="showProject('${project.name}')">${project.name}</a></li>
                </g:each>
            </ul>
        </li>
    </ul>
</script>

<script id="loginStatusTemplate" type="text/x-jsrender">
    <li data-link="visible{: loggedInUserName !== '' }">
        %{--TODO use uri global variable when referencing a controller--}%
        <a data-link="href{: 'user/options/'}"><span data-link="loggedInUserName"></span></a>
    </li>
    <li data-link="visible{: isAdmin }">
        <g:link controller='user' action='admin'>Admin page</g:link>
    </li>
    <li data-link="visible{: loggedInUserName !== '' }">
        <g:link controller='logout'>Log out</g:link>
    </li>

    <li data-link="visible{: loggedInUserName === '' }">
        <g:link class='colorbox' url='login'>Login</g:link>
    </li>
    <li data-link="visible{: loggedInUserName === '' }">
        <g:link class='colorbox' url='register'>Register</g:link>
    </li>
</script>

<script id="dayTemplate" type="text/x-jsrender">
    <div class="day"
         data-date='{{:date}}'>
        <div class="row-fluid">
            <h3 class="dayLabel well-small span5">
                {{>date}}
            </h3>
        </div>

        <div class="changesets">
            <!-- here will be changestes for the given day -->
        </div>
    </div>
</script>

<script>
    $.fn.floatWithin.defaults.offset = 55

    $('.changeset .left.column').livequery(function () {
        $(this).floatWithin('.changeset')
    })
</script>

<script id="changesetTemplate" type="text/x-jsrender">

    <div class='changeset {{if belongsToCurrentUser}} current-user {{/if}} row-fluid'
         data-identifier='{{:identifier}}' data-id='{{:id}}'>

        <div class="column left span5 well well-small">

            <div class="basicInfo row-fluid">
                <img class="pull-left" src='{{>~getGravatar(email)}}'/>

                <div class="pull-right">
                    <i class="icon-comment"></i>
                    <span class='commentsCount' data-link="allComments">{{>allComments}}</span>
                </div>

                <div class="nextToGravatar">

                    <div class="commitMessage"><h5>{{>commitMessage}}</h5></div>

                    <div class="commitFooter">
                        <span class='author'>{{>author}}</span> in
                        <span class="badge"
                              style="background-color: {{>~colorForProjectName(projectName)}}">{{>projectName}}</span>

                        <span class="pull-right changeset-date" data-date='{{:date}}'><i
                                class="icon-time"/> {{:date.substring(11)}}</span>
                        <span class="pull-right changeset-hash"
                              data-changeset_identifier='{{:identifier}}'>{{>shortIdentifier}}</span>
                    </div>

                </div>

                <div class="clearfix"></div>
            </div>

            <div id="changesetDetails-{{>identifier}}" style="display:none;" class="details row-fluid margin-top-small">

                <h5>Comments:</h5>

                <div class="comments" id="comments-{{>identifier}}">
                    {{for comments tmpl='#commentTemplate' /}}
                </div>

                <div id="comment-form-{{>identifier}}"></div>

                <h5>Changed files:</h5>

                <div class="projectFiles accordion margin-bottom-small" id="accordion-{{>identifier}}">
                    {{for projectFiles tmpl='#projectFileRowTemplate' /}}
                </div>

                <a class="wideButton lessButton">
                    <div class="center sizeOfIcon"><i class="icon-chevron-up"></i></div>
                </a>
            </div>
        </div>

        <div class="span7">
            <div class="well" id="content-files-span-{{>identifier}}" style="display: none;">
                <div id="content-files-title-{{>identifier}}"></div>
                <br/>

                <div id="diff-{{>identifier}}"></div>

                <div class="files-right">

                    <div id="content-files-{{>identifier}}"></div>
                </div>
            </div>
        </div>
    </div>
</script>

<script id="projectFileRowTemplate" type="text/x-jsrender">
    <div class="projectFile accordion-group" id="accordion-group-{{>collapseId}}"
         data-id={{:id}}>
        {{for [#data] tmpl='#accordionFileBodyTemplate'}}{{/for}}
    </div>
</script>

<script id="accordionFileBodyTemplate" type="text/x-jsrender">

    <div class="accordion-heading">
        <div class="row-fluid">

            <div class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-{{>changeset.identifier}}"
                 href="#collapse-inner-{{>collapseId}}">
                <i title="{{: ~textForChangeType(changeType.name) }}"
                   class="{{: ~iconForChangeType(changeType.name) }}"></i>
                <span class='linkLike'>{{:name}}</span>
                <span class="pull-right" data-link="visible{: commentsCount != 0 }">
                    <i class="icon-comment"></i><span class='commentsCount' data-link="commentsCount"></span>
                </span>
            </div>
        </div>
    </div>

    <div id='collapse-inner-{{>collapseId}}' class="details accordion-body collapse"
         data-changeset_id='{{:changeset.identifier}}'
         data-file_id='{{:id}}'
         data-file_change_type='{{:changeType.name}}'
         data-file_name_slice='{{:name}}'
         data-text_format='{{:textFormat}}'>
        <div class="accordion-inner" id="accordion-inner-{{>id}}">
            <div id="fileComments-{{>collapseId}}"></div>
        </div>
    </div>
</script>

<script id="fileTitleTemplate" type="text/x-jsrender">

    <div class="row-fluid">
        <div class="span11">
            <h1>{{:fileName}}</h1>
        </div>

        <div class="span1">
            <button type="button" class="btn pull-right " onClick="hideFile('{{>changesetId}}')">
                <i class="icon-remove"></i>
            </button>
        </div>
    </div>
</script>

<script id="snippetTemplate" type="text/x-jsrender">
    <div id="div-comments-{{>changesetId}}{{>fileId}}-{{>lineNumber}}"></div>
    <textarea id="add-reply-{{>fileId}}-{{>lineNumber}}" placeholder="Reply..."
              onfocus="expandReplyForm('{{>fileId}}', '{{>lineNumber}}')"
              class="span12" rows="1"></textarea>

    <div class="addLongCommentMessage" id="reply-info-{{>fileId}}-{{>lineNumber}}"></div>

    <div class="btn-group pull-right" id="replyFormButtons-{{>fileId}}-{{>lineNumber}}"
         style="display: none; margin-bottom:10px">
        <button type="button" class="btn btn-primary" id="replyButton-{{>fileId}}-{{>lineNumber}}"
                onClick="addReply('{{>changesetId}}', '{{>fileId}}', '{{>lineNumber}}')">Reply</button>
        %{--FIXME this function NEEEDS both changesetIdentifier and projectFileId to work in all cases--}%
        %{--amend parameters and corresponding markup--}%
        <button type="button" class="btn btn-primary"
                onClick="cancelReply('{{>fileId}}', '{{>lineNumber}}')">Cancel</button>
    </div>

    <div id="snippet-{{>fileId}}-{{>lineNumber}}"></div><hr/>
</script>

<!-- FIXME reuse comment form template for both types of comments -->
<script id="addLineCommentFormTemplate" type="text/x-jsrender">

    <form class=".form-inline">

        <textarea id="add-line-comment-{{>fileId}}" placeholder="Add comment..."
                  style="height:100px;width:95%;"></textarea>

        <div class="addLongCommentMessage"></div>

        <div class="btn-group pull-right">
            <button type="button" class="btn btn-primary" id="addCommentButton-{{>fileId}}"
                    onClick="addLineComment('{{>changesetId}}', '{{>fileId}}', '{{>lineNumber}}')">Add comment</button>
            <button type="button" class="btn btn-primary"
                    onClick="cancelLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Cancel</button>
        </div>
    </form>
</script>

<script id="cannotAddLineCommentMessageTepmlate" type="text/x-jsrender">
    <p class="alert alert-block">
        Adding comments to this version of file is disabled - a newer changeset with this file exists in this project.
    </p>
</script>

<script id="commentFormTemplate" type="text/x-jsrender">

    <form id="commentForm-{{>identifier}}" class="margin-bottom-small">
        <textarea onfocus="expandCommentForm('{{>identifier}}',this)"
                  id="add-comment-{{>identifier}}" placeholder="Add comment..."
                  class="span12" rows="1"></textarea>
    </form>

    <div class="addLongCommentMessageToChangeset"></div>

    <div id="commentFormButtons-{{>identifier}}" class="btn-group pull-right" style="display: none;">
        <button type="button" class="btn btn-primary btnWarningBackground" id="addCommentButton-{{>identifier}}"
                onClick="addComment('{{:id}}', '{{:identifier}}')">Add comment</button>
        <button type="button" class="btn btn-primary" id="cancellButton-{{>identifier}}"
                onClick="resetCommentForm('{{>identifier}}')">Cancel</button>
    </div>

    <div class="clearfix"></div>
</script>

<script id="commentTemplate" type="text/x-jsrender">

    <div class="comment {{>fromRevision}} {{if belongsToCurrentUser}} current-user {{/if}}">
        <img src="{{>~getGravatar(author, 35)}}"/>

        <div class="nextToGravatar">
            <div class="comment-content">{{>text}}</div>

            <div class="comment-footer">
                <span class="comment-date pull-right"><i class="icon-time"/> {{:dateCreated}}</span>
                <span class="author pull-left">{{>author}}</span>
            </div>
        </div>

    </div>
</script>

<script id="diffTemplate" type="text/x-jsrender">

    <div>
        <button type="button" class="btn btn-primary" onClick="showDiff('{{>changesetId}}')"
                id="button-showing-diff-{{>changesetId}}">Show diff</button>
        <button type="button" class="btn btn-primary" onClick="hideDiff('{{>changesetId}}')" style="display:none"
                id="button-hiding-diff-{{>changesetId}}">Hide diff</button>
    </div>

    <div id="diff-box-{{>changesetId}}" style="display: none">

    </div>
</script>

<script id="longCommentTemplate" type="text/x-jsrender">

    <div class="alert alert-block">
        {{: #data }}

    </div>
</script>
</div>
</body>
</html>