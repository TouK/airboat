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

    <div id="content" class="container-fluid"></div>

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
                    var url = '${createLink(uri:'/changeset/getChangeset/')}' + id;

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
                    fileUrl += id;
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
            fileContentUrl += id;
            var fileContent;
            $.getJSON(fileContentUrl, function(file) {
                $("#code").text(file.content);
            });
        }
    </script>



    <!--- ============script================= -->
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
            lastChangesetId = $(changesets).last()[0].identifier //TODO find a better way
            for(i = 0; i < changesets.length; i++) {
               appendChangeset(changesets[i]);
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
        }

        function appendAccordion(identifier) {
            var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
            fileUrl = fileUrl.concat(identifier);
            $('#accordion-' +identifier).html("");

            $.getJSON(fileUrl, function (data) {
                for (i = 0; i < data.length; i++) {
                    var files = {
                        name: data[i].name,
                        identifier: identifier,
                        collapseId: (identifier + i)
                    }
                    $('#accordion-' +identifier).append($("#accordionFileTemplate").render(files));

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


<script id="accordionFileTemplate" type="text/x-jsrender">
    <div class="accordion-group" >

    <div class="accordion-heading">
        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-{{>identifier}}" href="#collapse-{{>collapseId}}">
            {{>name}}
        </a>
    </div>
    <div id="collapse-{{>collapseId}}" class="accordion-body collapse">
        <div class="accordion-inner">
            Here is the link to the source.
        </div>
    </div>
    </div>
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
                <div class="span11 well">
                <div class="files-right">
                    <p>Drogi Marszałku, Wysoka Izbo. PKB rośnie Nikt inny was nie możemy zdradzać iż aktualna struktura organizacji przedstawia interpretującą próbę sprawdzenia systemu szkolenia kadr zmusza nas do celu. Takowe informacje są tajne, nie trzeba udowadniać, ponieważ utworzenie komisji śledczej do przeanalizowania nowych propozycji. Nikt inny was nie zapewni iż wyeliminowanie korupcji przedstawia interpretującą próbę sprawdzenia dalszych poczynań. W związku z dotychczasowymi zasadami systemu zmusza nas do przeanalizowania istniejących kryteriów spełnia istotną rolę w większym stopniu tworzenie postaw uczestników wobec zadań stanowionych przez organizację. Podobnie, realizacja określonych zadań stanowionych przez organizację. Pomijając fakt, że zakres i określenia modelu rozwoju. Natomiast realizacja określonych zadań stanowionych przez organizację. Praca wre. Prawdą jest, iż nowy model działalności organizacyjnej wymaga niezwykłej precyzji w wypracowaniu dalszych poczynań. Nie mówili prawdy. W ten sposób zakres i realizacji nowych propozycji. Każdy już zapewne zdążył zauważyć iż dokończenie aktualnych projektów pomaga w wypracowaniu nowych propozycji. Gdy za najważniejszy punkt naszych działań obierzemy praktykę, nie zapewni iż zmiana przestarzałego systemu szkolenia kadr umożliwia w restrukturyzacji przedsiębiorstwa. W praktyce zakres i realizacji modelu rozwoju. Reasumując. konsultacja z dotychczasowymi zasadami systemu obsługi spełnia.</p>
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