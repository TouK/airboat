<%--
  Created by IntelliJ IDEA.
  User: touk
  Date: 24.07.12
  Time: 08:49
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>codeViewing in code mirror</title>
    <script src="${createLink(uri:'/js/codemirror.js')}"></script>
    <link rel="stylesheet" href="${createLink(uri:'/css/codemirror.css')}">

    <script src="${createLink(uri:'/js/javascript.js')}"></script>

    <g:javascript library="jquery" />
    <r:layoutResources/>
    <link href="http://borismoore.github.com/jsviews/demos/resources/presentation.css" rel="stylesheet" type="text/css" />
    <link href="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.css" rel="stylesheet" type="text/css" />
    <link href="${createLink(uri:'/js/js-view-templates.html')}"  type="text/css" />

    <!--###################COLORBOX###################-->
    <link media="screen" rel="stylesheet" href=" ${createLink(uri:'/css/colorbox.css')}" />


    <script src="${createLink(uri:'/js/jquery.colorbox-min.js')}" type="text/javascript"></script>
    <script src="http://borismoore.github.com/jsviews/jsrender.js" type="text/javascript"></script>




</head>
<body>
<script type="text/javascript">

    function popInfoBox (id)         {

        var identifier = id
        $(".show-changeset-button").colorbox({opacity:0.7 ,
            inline: true,
            width:"80%",
            height:"90%" ,
            fixed: true,
            onOpen:function(){
                $('#layer_content').html("") ;
                $('#layer_files').html("");
                var url = '${createLink(uri:'/changeset/getChangeset/')}';
                url = url.concat(id.toString());
                $.getJSON(url, function(data) {

                    for(i = 0; i < data.length; i++) {
                        var changesets = {
                            author: data[i].author,
                            identifier: data[i].identifier,
                            date: data[i].date

                        }
                        $('#layer_content').append($("#changeset").render(changesets));

                    }
                });
                var fileUrl = '${createLink(uri:'/changeset/getFileNamesForChangeset/')}';
                fileUrl = fileUrl.concat(id.toString());
                var firstFileId;
                $.getJSON(fileUrl, function(data) {
                    for(i = 0; i < data.length; i++) {
                        var files = {
                            name: data[i].name,
                            identifier: data[i].id

                        }
                        $('#layer_files').append($("#project-files").render(files));

                    }
                    firstFileId = data[0].id
                });

                var fileContentUrl = '${createLink(uri:'/projectFile/getFileWithContent/')}';
                fileContentUrl = fileContentUrl.concat("150");
                var fileContent;
                $.getJSON(fileContentUrl, function(data) {
                    fileContent = data.content;
                    $("#code").html(fileContent);
                });

            },
            onLoad:function(){
                //code
            }
        });
    }
</script>

<!-- template for a new layer -->
<div style="display:none">
    <div id='inline_content' style='padding:10px; background:#fff;'>
        <h1>Last changeset</h1>

        <div id="layer">
            <!-- ==========container=============== -->

            <div id="layer_content"></div>

        </div>
        <h2>Files changed in commit:</h2>
        <div id="layer_files">
        </div>
        <pre id="code"></pre>

    </div>
</div>

    <button type="button" class="show-changeset-button" onClick="popInfoBox(55)"  href="#inline_content">Pop Info box</button>




<!-- ###################JS-View Render templates############################ -->
<!-- =============template=============== -->
<script id="changeset" type="text/x-jsrender">

    <h3>Author: {{>author}}</h3>


    <h3>Identifier:  {{>identifier}}</h3>

    <h3>Date:  {{>date}}</h3>

</script>
<!-- =============template=============== -->
<script id="project-files" type="text/x-jsrender">

    <h3>File name: {{>name}},  Id {{>identifier}}</h3>
</script>

</body>
</html>