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


    <script type="text/javascript">

        function addComment(changesetId) {

            var text = $('#add-comment-' + changesetId).val();
            var username = $('#username-' + changesetId).val();

            var comment = {
                author:username,
                content:text,
                date:new Date()
            }
            if(text == "" || username == "") {
                return false
            }

            $('#comments-' + changesetId).append($("#comment-template").render(comment));
            var url = "${createLink(controller:'UserComment', action:'addComment')}";

            $.post(url, { username:username, changesetId:changesetId, text:text });

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
            //var lineNumber = $('#line-number-' +fileIdentifier).val();

            var author =  $('#author-' +fileIdentifier).val();
            if(text == "" || lineNumber == "" || author =="") {
                return false
            }

            var url = "${createLink(controller:'LineComment', action:'addComment')}";

            $.post(url, {  text: text, lineNumber: lineNumber, fileId: fileIdentifier, author: author });

            $('#add-line-comment-' + fileIdentifier).val("");
            $('#author-' + fileIdentifier).val("");
            $('#line-number-' +fileIdentifier).val("");

            appendAccordion(changesetId);
            $('#collapse-' +changesetId +fileIdentifier).collapse("show");
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
                    var comments = {
                        author:data[i].author,
                        date:data[i].dateCreated,
                        content:data[i].text
                    }
                    $('#comments-' + id).append($("#comment-template").render(comments));
                }
            });
        }


    </script>

</head>

<body>

<script type="text/javascript">$.SyntaxHighlighter.init({'stripEmptyStartFinishLines': false});</script>

<div id="content" class="container-fluid"></div>

    <script type="text/javascript">

        function showFile(changesetId, fileId) {

            var fileContentUrl = '${createLink(uri:'/projectFile/getFileWithContent/')}';
            fileContentUrl += fileId;
            var fileContent;
            $.getJSON(fileContentUrl, function(file) {
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
                    $(element).popover({content: commentForm, placement: "left", trigger: "manual"});
                });
            });
            $("#content-files-" + changesetId).show(100);
            $('#content-files-span-' +changesetId).show(100);

            //appendAddLineCommentToFileForm(changesetId, fileId);


            $("#h-btn-" + changesetId + fileId).show();
            $("#sh-btn-" + changesetId + fileId).hide();


        }
        function appendAddLineCommentToFileForm(changesetId, fileId) {
            $("#content-files-comment-" + changesetId).html("");
           var commentForm = $("#addLineCommentFormTemplate").render({fileId:fileId, changesetId:changesetId });
            $("#content-files-comment-" + changesetId).append(commentForm);
        }
        function hideFile(changesetId, fileId)  {
            $("#content-files-" + changesetId).hide();
            $(".show-file").show();
            $(".hide-file").hide();
            $("#h-btn-" + changesetId + fileId).hide();
            $("#sh-btn-" + changesetId + fileId).show();
            $('#content-files-span-' +changesetId).hide();
            $('#content-files-' + changesetId + ' .linenums li').popover("hide");
            $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
                $(element).popover("hide");
            });
        }
    </script>


    <!-- generates list of changesets -->
    <script type="text/javascript">


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
                $.getJSON('${createLink(uri:'/changeset/getNextFewChangesetsOlderThan/')}' + lastChangesetId, appendChangesets)
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
            $('#less-button-' +changeset.identifier).hide();
            appendAccordion(changeset.identifier);
            $('#accordion-' +changeset.identifier).hide();
            $('#content-files-span-' +changeset.identifier).hide();


        }

        function appendAccordion(changesetId) {
            var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
            fileUrl = fileUrl.concat(changesetId);
            $('#accordion-' +changesetId).html("");

            $.getJSON(fileUrl, function (data) {
                for (i = 0; i < data.length; i++) {
                    var accordionRow = $("#accordionFileTemplate").render({
                        name:data[i].name,
                        changesetId: changesetId,
                        fileId:data[i].id,
                        collapseId:(changesetId + data[i].id)
                    });
                    $('#accordion-' +changesetId).append(accordionRow);
                    appendSnippetToFileInAccordion(data[i].id)
                    $("#h-btn-" + changesetId + data[i].id).hide();
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
                            var comment = $("#comment-template").render({
                                content:snippetData[j].commentGroup[z].text,
                                author:snippetData[j].commentGroup[z].author,
                                date:snippetData[j].commentGroup[z].dateCreated

                            });

                            $('#div-comments-' +snippetData[j].commentGroup[0].projectFile.id +"-" + snippetData[j].commentGroup[0].lineNumber).append(comment);
                        }
                    }

                }
            });
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
    <div class="accordion-group" >

    <div class="accordion-heading">
        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-{{>changesetId}}" href="#collapse-{{>collapseId}}">
            {{>name}}
        </a>

    </div>
    <div id="collapse-{{>collapseId}}" class="accordion-body collapse">
        <button type="button" class="btn pull-right " id="sh-btn-{{>collapseId}}" onClick="showFile('{{>changesetId}}', '{{>fileId}}')">Show file &gt;</button>
        <button type="button" class="btn pull-right " id="h-btn-{{>collapseId}}" onClick="hideFile('{{>changesetId}}', '{{>fileId}}')"> &lt; Hide file </button>
        <div class="accordion-inner" id="accordion-inner-{{>fileId}}">

            <div id="accordion-inner-div-snippet-{{>fileId}}"></div>

        </div>
    </div>
    </div>
</script>

<script id="lineCommentTemplate" type="text/x-jsrender">
    <p>{{>author}} wrote: {{>text}}</p>
</script>

<script id="snippetTemplate" type="text/x-jsrender">
    <div id="div-comments-{{>fileId}}-{{>snippetId}}"></div>
    <div id="snippet-{{>fileId}}-{{>snippetId}}"></div>
</script>


<script id="addLineCommentFormTemplate" type="text/x-jsrender">
<form class="add_comment .form-inline"><textarea
                                                 id="add-line-comment-{{>fileId}}" placeholder="Add comment..." style="height:80px"></textarea>


    <input id="author-{{>fileId}}" type="text" class="input-small" placeholder="name"/></input>
    <br />
    <button type="button"  class="btn" id="btn-{{>fileId}}" onClick="addLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Add comment</button>
    <button type="button" class="btn" id="c-btn-{{>fileId}}" onClick="cancelLineComment('{{>fileId}}', '{{>changesetId}}', '{{>lineNumber}}')">Cancel</button>
</form>

</script>

<script id="commentFormTemplate" type="text/x-jsrender">
    <form class="add_comment .form-inline"><textarea onfocus=" $('#btn-' +'{{>identifier}}').show(100); $('#c-btn-' +'{{>identifier}}').show(100); this.style.height='100px'; this.style.width='400px'; "
                                                     id="add-comment-{{>identifier}}" placeholder="Add comment..." class="slideable"></textarea>
        <input id="username-{{>identifier}}" type="text" class="input-small" placeholder="Your name"/></input>
        <br />
        <button type="button"  class="btn" id="btn-{{>identifier}}" onClick="addComment('{{>identifier}}')">Add comment</button>
        <button type="button" class="btn" id="c-btn-{{>identifier}}" onClick="cancelComment('{{>identifier}}')">Cancel</button>
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
                        <span class="label">{{>author}}</span>
                    </div>
                    <div class="span2">
                        <span class="label label-info">{{>date}}</span>
                        <span class="label label-info">{{>shortIdentifier}}</span>
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
                <div class="files-right">
                    <div id="content-files-{{>identifier}}" > </div>
                    <div id="content-files-comment-{{>identifier}}"></div>
                    </div>
                    </div>
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

    <!--FIXME make all js-views templates have 'Template' suffix-->
    <script id="box-changeset" type="text/x-jsrender">
        Author: {{>author}}</br>
        Identifier:  {{>identifier}}</br>
        Date:  {{>date}}</br>
    </script>

</body>
</html>