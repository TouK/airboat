<!doctype html>
<html>
<head>

    <g:javascript library="jquery"/>
    <r:layoutResources/>

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
            }
        }
    </script>


    <link href=" ${createLink(uri: '/css/bootstrap.css')}" type="text/css" rel="stylesheet" media="screen"/>

    <script src="${createLink(uri: '/js/jquery.md5.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/jsrender.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/bootstrap-collapse.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/bootstrap-tooltip.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/bootstrap-popover.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/jquery.syntaxhighlighter.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/jquery.zclip.js')}" type="text/javascript"></script>

    <link href="${createLink(uri: '/css/codereview.css')}" type="text/css" rel="stylesheet"/>

    <script src="${createLink(uri: '/js/codereview/comments.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/files.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/changesets.js')}" type="text/javascript"></script>
</head>

<body>

<h1>
    <sec:ifNotLoggedIn>Hello, unknown wanderer!</sec:ifNotLoggedIn>
    <sec:ifLoggedIn>Hello, <sec:username/>!</sec:ifLoggedIn>
</h1>

<script type="text/javascript">$.SyntaxHighlighter.init({'stripEmptyStartFinishLines':false});</script>

<div class="well">
    <a class="btn" onclick="showProject('codereview')">CodeReview</a>
    <a class="btn" onclick="showProject('cyclone')">Cyclone</a>
    <a class="btn" onclick="showProject('')">AllProjects</a>
    <a class="btn pull-right"
       href="https://docs.google.com/spreadsheet/ccc?key=0AqcWoYECBA_SdElrejNuNVUzNEt3LTJZQnVCQ3RILWc#gid=0"
       target="_blank">Feedback</a>
</div>

<div id="content" class="container-fluid"></div>

<script type="text/javascript">

    $.views.helpers({
        getGravatar:function (email, size) {
            var size = size || 50;
            return 'http://www.gravatar.com/avatar/' + $.md5(email) + '.jpg?s=' + size;
        }
    })

    var hashAbbreviationLength = 8;
    var lineBoundary = 60; //TODO rename

    $().ready(function () {
        showProject('')

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });
    });
</script>

<script id="changesetTemplate" type="text/x-jsrender">

    <div class="row-fluid">
        <div class="span4">
            <div class="span11 well">
                <div class="span2">
                    <img src='{{>~getGravatar(email)}}'/>
                </div>

                <div class="row-fluid">
                    <div class="span7">
                        <span class="label {{if belongsToCurrentUser}}label-success{{/if}}">{{>author}}</span> </br>
                        <span class="label">{{>projectName}}</span>
                    </div>

                    <div class="span2">
                        <span class="label label-info">{{>date}}</span>
                        <span class="label label-info" id="hash-{{>identifier}}">{{>shortIdentifier}}</span>
                    </div>
                </div>

                <div class="well-small">{{>commitComment}}</div>

                <div class="changeset-content"></div>

                <div id="more-button-{{>identifier}}">
                    <a class="btn btn-primary btn-big" onclick="showChangesetDetails('{{>identifier}}')">
                        More...
                    </a>
                </div>

                <div id="changesetDetails-{{>identifier}}" style="display:none;">
                    <div class="accordion" id="accordion-{{>identifier}}"></div>

                    <div class="comments" id="comments-{{>identifier}}"></div>

                    <div id="comment-form-{{>identifier}}">

                    </div>

                    <div id="less-button-{{>identifier}}">
                        <a class="btn btn-primary btn-big" onclick="showLessAboutChangeset('{{>identifier}}')">
                            Less...
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <div class="span8">
            <div class="span11 well" id="content-files-span-{{>identifier}}" style="display: none;">
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