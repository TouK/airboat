<!doctype html>
<html>
<head>

    <g:javascript library="jquery"/>
    <r:layoutResources/>

    <link media="screen" rel="stylesheet" href=" ${createLink(uri: '/css/bootstrap.css')}"/>

    <script src="${createLink(uri: '/js/jquery-latest.min.js')}" type="text/javascript"></script>

    <link href="${createLink(uri: '/css/js-view-presentation.css')}" rel="stylesheet" type="text/css"/>
    <!--TODO examine if neccessary after plugging in syntaxhighlighter -->
    <link href="${createLink(uri: '/css/js-view-syntaxhighlighter.css')}" rel="stylesheet" type="text/css"/>
    <script src="${createLink(uri: '/js/jsrender.js')}" type="text/javascript"></script>

    <link media="screen" rel="stylesheet" href=" ${createLink(uri: '/css/colorbox.css')}"/>
    <script src="${createLink(uri: '/js/jquery.colorbox-min.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/gravatar.js')}" type="text/javascript"></script>


    <script type="text/javascript">

        function addComment(changesetId) {

            var rawText = $('#add-comment-' + changesetId.toString()).val();
            var text = "<h1>your comment is: " + rawText;
            var username = $('#username-' + changesetId.toString()).val();
            text = text + " written by: " + username + " changeset id: " + changesetId.toString() + "</h1><br />";

            var comment = {
                author:username,
                content:rawText,
                date:new Date()
            }
            if(rawText == "" || username == "") {
                return false
            }

            $('#comments-' + changesetId.toString()).append($("#comment-template").render(comment));
            var url = "${createLink(controller:'UserComment', action:'addComment')}";

            $.post(url, { username:username, changesetId:changesetId, text:rawText });
            changeAddCommentDivToDefault(changesetId);
        }

        function cancelComment(changesetId) {
                changeAddCommentDivToDefault(changesetId);
        }

        function changeAddCommentDivToDefault(changesetId) {
            $('#add-comment-' + changesetId.toString()).val("");
            $('#username-' + changesetId.toString()).val("");
            $('#add-comment-' + changesetId.toString()).width("200px");
            $('#add-comment-' + changesetId.toString()).height("20px");
        }

        function showCommentsToChangeset(id) {
            $('#comments-' + id.toString()).html("");
            var fileUrl = '${createLink(uri:'/userComment/returnCommentsToChangeset/')}';
            fileUrl = fileUrl.concat(id.toString());
            $.getJSON(fileUrl, function (data) {
                for (i = 0; i < data.length; i++) {
                    var comments = {
                        author:data[i].author,
                        date:data[i].dateCreated,
                        content:data[i].content
                    }
                    $('#comments-' + id.toString()).append($("#comment-template").render(comments));
                }
            });
        }

        function slideTextArea(id) {
            $("#add-comment-" + id).width = 500;
        }
    </script>


</head>

<body>

    <div id="content" class="container"></div>

    <!-- function to handle click for more info in new layer for chosen changeset -->
    <script type="text/javascript">
        function showChangedFilesBox(id) {

            $(".show-changeset-button").colorbox({opacity:0.3,
                inline:true,
                width:"80%",
                height:"80%",
                fixed:true,
                onOpen:function () {
                    $('#changesetInfo').html("");
                    $('#layer_files').html("");
                    var url = '${createLink(uri:'/changeset/getChangeset/')}';
                    url = url.concat(id.toString());
                    $.getJSON(url, function (data) {

                        for (i = 0; i < data.length; i++) {
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date

                            }
                            $('#changesetInfo').append($("#box-changeset").render(changesets));

                        }
                    });

                    var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
                    fileUrl = fileUrl.concat(id.toString());
                    $.getJSON(fileUrl, function (data) {
                        for (i = 0; i < data.length; i++) {
                            var files = {
                                name: data[i].name,
                                identifier: data[i].id

                            }
                            $('#layer_files').append($("#project-files").render(files));

                        }
                    });
                            $("#code").html("<p>Click on file to see the content</p>");
                },
                onLoad:function () {
                    //code
                }
            });
        }

        function showFile(id) {
            var fileContentUrl = '${createLink(uri:'/projectFile/getFileWithContent/')}';
            fileContentUrl = fileContentUrl.concat(id);
            var fileContent;
            $.getJSON(fileContentUrl, function(file) {
                $("#code").text(file.content);
            });
        }
    </script>


    <!--TODO extract these templates, put in another file gathering js-view templates or something -->


    <!--- ============script================= -->
    <!-- generates list of changesets -->
    <script type="text/javascript">


        $(document).ready(function () {
            $('#content').html("");
            $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', appendChangesets);
        });

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });

        function onScrollThroughBottomAttempt() {
            if (!changesetsLoading) {
                changesetsLoading = true;
                $.getJSON('${createLink(uri:'/changeset/getNextFewChangesetsOlderThan/')}' + lastChangesetId, appendChangesets)
            }
        }

        $(document).ready(function () {
            $('#content').html("");
            $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', appendChangesets);
        });

        var lastChangesetId;
        var changesetsLoading;

        function appendChangesets(changesets) {
            lastChangesetId = $(changesets).last()[0].identifier //TODO find a better way
            for(i = 0; i < changesets.length; i++) {
               appendChangeset(changesets[i]);
            }
            changesetsLoading = false;
        }

        function appendChangeset(changeset) {
            changeset = $.extend({emailSubstitutedWithGravatar: get_gravatar(changeset.email, 50)}, changeset)
            $('#content').append($("#changesetTemplate").render(changeset));
            showCommentsToChangeset(changeset.identifier)
        }

    </script>


    <!-- template for a new layer which is shown in popping box -->
    <div style='display:none'>
        <div id='inline_content' style='padding:10px; background:#fff;'>
            <h1>Changeset</h1>

            <div>
                <div id="changesetInfo"></div>
            </div>

            <h2>Files changed in commit:</h2>
            <ul id="layer_files"></ul>
            <pre id="code"></pre>
        </div>
    </div>


    <script id="changesetTemplate" type="text/x-jsrender">

        <div class="row-fluid">

            <div class="span1"><img src="{{>emailSubstitutedWithGravatar}}"/></div>

            <div class="span11 well">

                <div class="changeset-header">
                    <span class="label">{{>author}}</span>
                    <span class="label label-info">{{>identifier}}</span>
                    <span class="label label-info">{{>date}}</span>
                    <a class="show-changeset-button btn" href="#inline_content" onclick="showChangedFilesBox('{{>identifier}}')"><i class="icon-file"/> Files</a>
                </div>

                <hr/>

                <div class="changeset-content">{{>commitComment}}</div>

                <hr/>

                <div class="comments" id="comments-{{>identifier}}"></div>

                <hr/>

                <form class="add_comment .form-inline">
                    <textarea onfocus="this.style.width = '480px'; this.style.height = '120px';" id="add-comment-{{>identifier}}" placeholder="Write your comment here!" class="slideable"/>
                    <input id="username-{{>identifier}}" type="text" class="input-small" placeholder="Your name!"/>
                    <button type="button"  class="btn" onClick="addComment('{{>identifier}}')">Add comment</button>
                    <button type="button"  class="btn" onClick="cancelComment('{{>identifier}}')">Cancel</button>
                </form>

            </div>

        </div>

    </script>

    <script id="comment-template" type="text/x-jsrender">

        <div class="alert">
            <img src=" ${createLink(uri: '/images/favicon.ico')}"/>    <!-- TODO: it should be a gravatar! -->
            <span class="label">{{>author}}</span>
            <span class="label label-info">{{>date}}</span>
            <hr/>
            <div class="comment-content">{{>content}}</div>
        </div>

    </script>

    <script id="box-changeset" type="text/x-jsrender">
        Author: {{>author}}</br>
        Identifier:  {{>identifier}}</br>
        Date:  {{>date}}</br>
    </script>

    <script id="project-files" type="text/x-jsrender">
        <li><a href="#" onclick="showFile({{>identifier}})" >{{>name}}</a> </li>
    </script>


</body>
</html>