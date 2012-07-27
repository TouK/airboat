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
        $('#comments-' + changesetId.toString()).append($("#comment-template").render(comment));
        var url = "${createLink(controller:'UserComment', action:'addComment')}";

        $.post(url, { username:username, changesetId:changesetId, text:rawText });
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


<br/>

<div id="content" class="container"></div>

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


<!--JS-View Render templates - used in scripts generating content -->
<script id="changesetTemplate" type="text/x-jsrender">
    <div class="changeset well">
        <div class="changeset-header">
            <img src="{{>emailSubstitutedWithGravatar}}">
            {{>author}},
            {{>identifier}},
            {{>date}}
        </div>

        <div class="changeset-content">
           <h4> Commit:</h4> {{>commitComment}}
        </div>

        <button type="button" class="show-changeset-button btn" href="#inline_content"
                onclick="showChangedFilesBox('{{>identifier}}')">Changed files</button>

        <div class="comments" id="comments-{{>identifier}}"></div>

        <form class="add_comment .form-inline" >
                   <textarea onfocus="this.style.width = '600px'; this.style.height = '200px';"
                             id="add-comment-{{>identifier}}"
                              placeholder="Write your comment here!" class="slideable"/>
            <input id="username-{{>identifier}}" type="text" class="input-small" placeholder="Your name!"/>
            <button type="button"  class="btn" onClick="addComment('{{>identifier}}')">Add comment</button>
       </form>
      </div>
    <br/>
</script>

<script id="comment-template" type="text/x-jsrender">
     <div class="container-fluid well">
          <div class="row-fluid">
              <div class="span1">
                  <img src=" ${createLink(uri: '/images/favicon.ico')}"/>    <!-- TODO: it should be a gravatar! -->
              </div>
              <div class="span2">
                  <h4>{{>author}} {{>date}} </h4>
                  <div class="comment-content">{{>content}}</div>
              </div>
          </div>
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