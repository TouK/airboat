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
    <script src="${createLink(uri: '/js/bootstrap-collapse.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/bootstrap-tooltip.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/bootstrap-popover.js')}" type="text/javascript"></script>

    <script type="text/javascript" src="${createLink(uri: '/js/jquery.syntaxhighlighter.min.js')}"></script>
    <script type="text/javascript" src="js/jquery.zclip.js"></script>

    <script type="text/javascript">

        function addComment(changesetId) {

            var text = $('#add-comment-' + changesetId).val();
            if(text == "") {
                return false
            }

            $.post("${createLink(controller:'UserComment', action:'addComment')}",
                    { changesetId:changesetId, text:text },
                    function (comment) {
                        $('#comments-' + changesetId).append($("#comment-template").render(comment));
                    },
                    "json");

            changeAddCommentDivToDefault(changesetId);
            hideAddCommentButtons(changesetId);
        }

        function cancelComment(changesetId) {
            changeAddCommentDivToDefault(changesetId);
            hideAddCommentButtons(changesetId);
        }
        
        function cancelLineComment(fileIdentifier, changesetId, lineNumber) {
            $('#add-line-comment-' + fileIdentifier).val("");
            $('#author-' + fileIdentifier).val("");

            $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
                $(element).popover("hide");
            });
        }

        function addLineComment(fileIdentifier, changesetId, lineNumber) {
            var text = $('#add-line-comment-' +fileIdentifier).val();
            var author =  $('#author-' +fileIdentifier).val();

            if(text == "" || lineNumber == "" || author =="") {
                return false
            }

            var url = "${createLink(controller:'LineComment', action:'addComment')}";

            $.post(url, {  text: text, lineNumber: lineNumber, fileId: fileIdentifier, author: author })
                    .done( function() {

            $('#add-line-comment-' + fileIdentifier).val("");
            $('#author-' + fileIdentifier).val("");


            updateAccordion(changesetId, fileIdentifier);
            $('#content-files-' + changesetId + ' .linenums li').popover("hide");
            });
        }


        function hideAddCommentButtons(changesetId) {
            $('#btn-' +changesetId).hide();
            $('#c-btn-' +changesetId).hide();
        }
        function changeAddCommentDivToDefault(changesetId) {
            $('#add-comment-' + changesetId).val("");
            $('#username-' + changesetId).val("");
            $('#add-comment-' + changesetId).width("200px");
            $('#add-comment-' + changesetId).height("20px");
        }

        function showCommentsToChangeset(id) {
            $('#comments-' + id).html("");
            var fileUrl = '${createLink(uri:'/userComment/returnCommentsToChangeset/')}';
            fileUrl += id;
            $.getJSON(fileUrl, function (data) {
                for (i = 0; i < data.length; i++) {
                    var comment = $("#comment-template").render(data[i]);
                    $('#comments-' + id).append(comment);
                }
            });
        }
    </script>

</head>

<body>

<h1>
    <sec:ifNotLoggedIn>Hello, unknown wanderer!</sec:ifNotLoggedIn>
    <sec:ifLoggedIn>Hello, <sec:username/>!</sec:ifLoggedIn>
</h1>

<script type="text/javascript">$.SyntaxHighlighter.init({'stripEmptyStartFinishLines': false});</script>

    <div class="well">
        <a class="btn" onclick="showProject('codereview')">CodeReview</a>
        <a class="btn" onclick="showProject('cyclone')">Cyclone</a>
        <a class="btn" onclick="showProject('')">AllProjects</a>
    </div>
    <div id="content" class="container-fluid"></div>

    <script type="text/javascript">
        var previousExpandedForFilesChangesetId;
        function showFile(changesetId, fileId) {

            var fileContentUrl = '${createLink(uri:'/projectFile/getFileWithContent/')}';
            fileContentUrl += fileId;
            var fileContent;
            $.getJSON(fileContentUrl, function(file) {
                var title = $("#fileTitleTemplate").render({
                    fileName: divideNameWithSlashesInTwo(file.name),
                    changesetId: changesetId,
                    fileId: fileId
                });

                $("#content-files-title-" + changesetId).html(title);

                $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
                $("#content-files-" + changesetId + " .codeViewer")
                    .text(file.content)
                    .addClass("language-" + file.filetype)
                    .syntaxHighlight();

                $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
                    $(element).click(function () {
                        $('#content-files-' + changesetId + ' .linenums li').popover("hide");
                        $(element).popover("show");

                    });
                    var commentForm = $("#addLineCommentFormTemplate").render({fileId:fileId, changesetId:changesetId, lineNumber:i });
                    var popoverTitle = $("#popoverTitleTemplate").render({
                        fileName: divideNameWithSlashesInTwo(file.name),
                        changesetId: changesetId,
                        fileId: fileId,
                        lineNumber: i
                    });

                    $(element).popover({content: commentForm, title: popoverTitle, placement: "left", trigger: "manual" });
                });
            });
            $("#content-files-" + changesetId).show(100);
            $('#content-files-span-' +changesetId).show(100);
            $("#content-files-title-" + changesetId).show(100);
            if( previousExpandedForFilesChangesetId != null) {
                $('#content-files-' + previousExpandedForFilesChangesetId + ' .linenums li').each(function (i, element, ignored) {
                    $(element).popover("hide");
                });
            }


            $("#sh-btn-" + changesetId + fileId).hide();

            previousExpandedForFilesChangesetId = changesetId;

        }

        function hideFile(changesetId, fileId)  {
            $("#content-files-" + changesetId).hide();

            $("#sh-btn-" + changesetId + fileId).show();
            $('#content-files-span-' +changesetId).hide();
            $('#content-files-' + changesetId + ' .linenums li').popover("hide");
            $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
                $(element).popover("hide");
            });
            $("#content-files-title-" + changesetId).hide();
        }
    </script>


    <!-- generates list of changesets -->
    <script type="text/javascript">

        var projectName = ''

        function showProject(projectId){
            projectName = projectId
            $(document).ready(function () {
                $('#content').html("");
                $.getJSON('${createLink(uri:'/changeset/getLastChangesets/')}' + '?' + $.param({projectName: projectName}), appendChangesets);
            });

            $(".collapse").collapse();
        }


        $(document).ready(function () {
            $('#content').html("");
            $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', appendChangesets);

        });

        $(".collapse").collapse();

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });

        function onScrollThroughBottomAttempt() {
            if (!changesetsLoading) {
                changesetsLoading = true;
                $.getJSON('${createLink(uri:'/changeset/getNextFewChangesetsOlderThan/')}' + '?' + $.param({projectName: projectName, changesetId: lastChangesetId}), appendChangesets)
            }
        }


        var lastChangesetId;
        var changesetsLoading;




        function appendChangesets(changesets) {
            if (changesets.length > 0) {
                lastChangesetId = $(changesets).last()[0].identifier //TODO find a better way
                for(i = 0; i < changesets.length; i++) {
                    appendChangeset(changesets[i]);
                }
            }
            changesetsLoading = false;
        }

        function appendChangeset(changeset) {
            var shortIdentifier = changeset.identifier.substr(0, 8) + "...";
            changeset = $.extend({emailSubstitutedWithGravatar: get_gravatar(changeset.email, 50), shortIdentifier: shortIdentifier}, changeset)
            $('#content').append($("#changesetTemplate").render(changeset));
            showCommentsToChangeset(changeset.identifier);
            $('#comments-' + changeset.identifier).hide();
            appendCommentForm(changeset.identifier);
            $('#less-button-' + changeset.identifier).hide();
            appendAccordion(changeset.identifier, null);
            $('#accordion-' + changeset.identifier).hide();
            $('#content-files-span-' + changeset.identifier).hide();

            $('#hash-' + changeset.identifier).tooltip({title: changeset.identifier + ", click to copy", trigger:"hover"});
            $('#hash-' + changeset.identifier).zclip({
                path:'js/ZeroClipboard.swf',
                copy:changeset.identifier
            });
        }

        function appendAccordion(changesetId, fileIdentifier) {
            var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
            fileUrl = fileUrl.concat(changesetId);
            var lineBoundary = 60;

            $('#accordion-' +changesetId).html("");

            $.getJSON(fileUrl, function (data) {

                for (i = 0; i < data.length; i++) {
                    var accordionRow = $("#accordionFileTemplate").render({
                        name:sliceName(data[i].name, lineBoundary),
                        changesetId: changesetId,
                        fileId:data[i].id,
                        collapseId:(changesetId + data[i].id),
                        howManyComments: data[i].lineComments.length
                    });
                    //
                    $('#accordion-' +changesetId).append(accordionRow);
                    appendSnippetToFileInAccordion(data[i].id)



                }

            });



        }

        function updateAccordion(changesetId, fileIdentifier) {
            var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
            fileUrl = fileUrl.concat(changesetId);
            var lineBoundary = 60;

            $.getJSON(fileUrl, function (data) {

                for (i = 0; i < data.length; i++) {
                    var accordionRow = $("#accordionFileUpdateTemplate").render({
                        name: sliceName(data[i].name, lineBoundary),
                        changesetId: changesetId,
                        fileId:data[i].id,
                        collapseId:(changesetId + data[i].id),
                        howManyComments: data[i].lineComments.length
                    });
                    if(fileIdentifier == data[i].id) {
                        $('#accordion-group-' +changesetId + data[i].id).html("");
                    $('#accordion-group-' +changesetId + data[i].id).append(accordionRow);
                    appendSnippetToFileInAccordion(data[i].id)

                    }

                }
                });


        }

        function appendSnippetToFileInAccordion(fileId)   {
            var snippetUrl =  '${createLink(uri:'/projectFile/getLineCommentsWithSnippetsToFile/')}' +fileId;
            $.getJSON(snippetUrl, function (snippetData) {
                if(snippetData.length > 0) {
                    $('#accordion-inner-div-' +snippetData[0].commentGroup[0].projectFile.id).html("");

                    for (j = 0; j < snippetData.length; j++) {
                        var snippet = $("#snippetTemplate").render({
                            snippet:snippetData[j].snippet,
                            fileId: snippetData[j].commentGroup[0].projectFile.id,
                            snippetId: snippetData[j].commentGroup[0].lineNumber
                        });

                        $('#accordion-inner-div-snippet-' +snippetData[j].commentGroup[0].projectFile.id).append(snippet);
                        $("#snippet-" + snippetData[j].commentGroup[0].projectFile.id + "-" + snippetData[j].commentGroup[0].lineNumber).html("<pre class='codeViewer'/></pre>");
                        $("#snippet-" + snippetData[j].commentGroup[0].projectFile.id + "-" + snippetData[j].commentGroup[0].lineNumber + " .codeViewer")
                                .text(snippetData[j].snippet)
                                .addClass("linenums:"+(snippetData[j].commentGroup[0].lineNumber+1))
                                .addClass("language-" + snippetData[j].filetype)
                                .syntaxHighlight();

                        for(z = 0; z < snippetData[j].commentGroup.length; z++) {
                            var comment = $("#comment-template").render(snippetData[j].commentGroup[z]);
                            $('#div-comments-' +snippetData[j].commentGroup[0].projectFile.id +"-" + snippetData[j].commentGroup[0].lineNumber).append(comment);
                        }
                    }

                }
            });
        }
        function divideNameWithSlashesInTwo(name) {
           var splitted, newName;
            splitted = name.split("/");
            newName = splitted.slice(0, Math.ceil(splitted.length/2)).join("/");
            newName += "/ ";
            newName += splitted.slice( Math.ceil(splitted.length/2), splitted.length).join("/");
            return newName;
        }

        function sliceName(name, lineWidth) {
            var newName  = "" ;
            var boundary = lineWidth;
            var splitted = name.split("/");
            var i;
            for(i = 0; i < splitted.length ; i++) {
                if(newName.length + splitted[i].length >= boundary){
                    boundary += lineWidth;
                    newName += " ";
                    newName += splitted[i] + "/";
                }
                else {
                    newName += splitted[i] + "/";
                }

            }

            return newName.substr(0,newName.length -1);
        }

        function showMoreAboutChangeset(identifier)  {
            $("#more-button-" +identifier).hide();
            $('#less-button-' +identifier).show(100);
            $('#comments-' + identifier).show(100);
            $('#comment-form-' +identifier).show(100);
            $('#accordion-' +identifier).show(100);

        }

        function showLessAboutChangeset(identifier) {
            $("#less-button-" +identifier).hide();
            $('#more-button-' +identifier).show(100);
            $('#comments-' + identifier).hide();
            $('#comment-form-' +identifier).hide();
            $('#accordion-' +identifier).hide();
        }

        function appendCommentForm(identifier) {
            $("#comment-form-" + identifier).html('');
            $('#comment-form-' +identifier).append($("#commentFormTemplate").render({identifier: identifier}));
            $('#comment-form-' +identifier).hide();
            hideAddCommentButtons(identifier);

        }

    </script>


<script id="accordionFileTemplate" type="text/x-jsrender">
    <div class="accordion-group" id="accordion-group-{{>collapseId}}">

    <div class="accordion-heading">
        <div class="row-fluid">
            <div class="row-fluid span9">
                <a class="accordion-toggle" id="collapse-{{>collapseId}}" data-toggle="collapse" data-parent="#accordion-{{>changesetId}}" href="#collapse-inner-{{>collapseId}}">
                    {{>name}}
                </a>
            </div>
            {{if howManyComments != 0}}
            <div class="row-fluid span3" >
                 <button class="btn btn disabled pull-right" style="margin:2px 5px 0px 5px"><i class="icon-comment"></i>   {{>howManyComments}}  </button>
            </div>
            {{/if}}
        </div>
    </div>
    <div id="collapse-inner-{{>collapseId}}" class="accordion-body collapse">
        <button type="button" class="btn pull-right " id="sh-btn-{{>collapseId}}" onClick="showFile('{{>changesetId}}', '{{>fileId}}')">Show file &gt;</button>

        <div class="accordion-inner" id="accordion-inner-{{>fileId}}">

            <div id="accordion-inner-div-snippet-{{>fileId}}"></div>

        </div>
    </div>
    </div>
</script>
<script id="accordionFileUpdateTemplate" type="text/x-jsrender">
        <div class="accordion-heading">
            <div class="row-fluid">
                <div class="row-fluid span9">
                    <a class="accordion-toggle"  data-toggle="collapse" data-parent="#accordion-{{>changesetId}}" href="#collapse-inner-{{>collapseId}}">
                        {{>name}}
                    </a>
                </div>
                {{if howManyComments != 0}}
                <div class="row-fluid span3" >
                    <button class="btn btn disabled pull-right" style="margin:2px 5px 0px 5px"><i class="icon-comment"></i>   {{>howManyComments}}  </button>
                </div>
                {{/if}}
            </div>
        </div>
        <div id="collapse-inner-{{>collapseId}}" class="accordion-body collapse in">
            <button type="button" class="btn pull-right " id="sh-btn-{{>collapseId}}" onClick="showFile('{{>changesetId}}', '{{>fileId}}')">Show file &gt;</button>

            <div class="accordion-inner" id="accordion-inner-{{>fileId}}">

                <div id="accordion-inner-div-snippet-{{>fileId}}"></div>

            </div>
        </div>

</script>

<script id="fileTitleTemplate" type="text/x-jsrender">
    <div class="row-fluid">

        <div class="span11 ">
    <h1>{{>fileName}}</h1>
            </div>
        <div class="span1">
    <button type="button" class="btn pull-right " onClick="hideFile('{{>changesetId}}', '{{>fileId}}')"><i class="icon-remove"> </i></button>
            </div>
        </div>
</script>

<script id="popoverTitleTemplate" type="text/x-jsrender">
    <div class="row-fluid">
            <button type="button" class="btn pull-right " onClick="cancelLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')"><i class="icon-remove"> </i></button>
    </div>
</script>

<script id="snippetTemplate" type="text/x-jsrender">
    <div id="div-comments-{{>fileId}}-{{>snippetId}}"></div>
    <div id="snippet-{{>fileId}}-{{>snippetId}}"></div>
</script>


<script id="addLineCommentFormTemplate" type="text/x-jsrender">
<form class="add_comment .form-inline">
    <textarea id="add-line-comment-{{>fileId}}" placeholder="Add comment..." style="height:80px"></textarea>
    <input id="author-{{>fileId}}" type="text" class="input-small" placeholder="name"/></input>
    <br />
    <button type="button"  class="btn" id="btn-{{>fileId}}" onClick="addLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Add comment</button>

</form>

</script>

<script id="commentFormTemplate" type="text/x-jsrender">
    <form class="add_comment .form-inline"><textarea onfocus=" $('#btn-' +'{{>identifier}}').show(100); $('#c-btn-' +'{{>identifier}}').show(100); this.style.height='100px'; this.style.width='400px'; "
                                                     id="add-comment-{{>identifier}}" placeholder="Add comment..." class="slideable"></textarea>
        <br />
        <button type="button" class="btn btn-primary" id="btn-{{>identifier}}" onClick="addComment('{{>identifier}}')">Add comment</button>
        <button type="button" class="btn btn-danger" id="c-btn-{{>identifier}}" onClick="cancelComment('{{>identifier}}')">Cancel</button>
    </form>
</script>

<script id="changesetTemplate" type="text/x-jsrender">

        <div class="row-fluid">
            <div class="span4">
            <div class="span11 well">
                <div class="span2">
                    <img src="{{>emailSubstitutedWithGravatar}}"/>
                </div>

                <div class="row-fluid">
                    <div class="span7">
                        <span class="label">{{>author}}</span> </br>
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
                <a class="btn btn-primary btn-big" onclick="showMoreAboutChangeset('{{>identifier}}')">
                    More...
                </a>
                </div>
                <div class="accordion" id="accordion-{{>identifier}}" ></div>


                <div class="comments" id="comments-{{>identifier}}"></div>
                <hr/>
                <div id="comment-form-{{>identifier}}">

                </div>

                <div id="less-button-{{>identifier}}">
                    <a class="btn btn-primary btn-big" onclick="showLessAboutChangeset('{{>identifier}}')">
                        Less...
                    </a>
                </div>


                <div id="comment-form-buttons-{{>identifier}}">

                </div>


         </div>

        </div>

            <div class="span8">

                <div class="span11 well" id="content-files-span-{{>identifier}}">
                    <div id="content-files-title-{{>identifier}}"></div>
                    <br />
                <div class="files-right">
                    <div id="content-files-{{>identifier}}" > </div>

                    </div>
                    </div>
            </div>
        </div>
    </script>

<script id="comment-template" type="text/x-jsrender">

        <div class="alert">
            <img src=" ${createLink(uri: '/images/favicon.ico')}"/>    <!-- TODO: it should be a gravatar! -->
            <span class="label">{{>author}}</span>
            <span class="label label-info">{{>dateCreated}}</span>
            <hr/>
            <div class="comment-content">{{>text}}</div>
        </div>

    </script>

    <!--FIXME make all js-views templates have 'Template' suffix-->
    <script id="box-changeset" type="text/x-jsrender">
        Author: {{>author}}</br>
        Identifier:  {{>identifier}}</br>
        Date:  {{>date}}</br>
    </script>

</body>
</html>