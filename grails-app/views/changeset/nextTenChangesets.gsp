<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
    <g:javascript library="jquery" />
    <r:layoutResources/>
    <link href="http://borismoore.github.com/jsviews/demos/resources/presentation.css" rel="stylesheet" type="text/css" />
    <link href="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.css" rel="stylesheet" type="text/css" />
    <script src="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.min.js" type="text/javascript"></script>
    <script src="http://borismoore.github.com/jsviews/jsrender.js" type="text/javascript"></script>

</head>
<body>
<br />  <hr />
<div id="new-changesets"></div>
<!-- =============template=============== -->
<script id="showdata" type="text/x-jsrender">
    <hr />
    <div class="changeset">

        <div class="changeset-header">

            Author: {{>author}},

            Identifier: {{>identifier}},

            Date: {{>date}},

            <div class="buttons" style="float:right">
                <button type="button" class="show-changeset-button" href="#inline_content" onclick="showChangedFilesBox({{>number}})">Info</button>
            </div>
        </div>
        <div class="changeset-content" >
            Here's content of my changeset. For example comment which was written during commiting change.
        </div>
        <div class="changeset-comments">
            I don't know yet. But it'd probably be nice to have a div for comments.
        </div>
    </div>
</script>
<script type="text/javascript">
    $(document).ready(function() {
        var url = '${createLink(uri:'/changeset/getNextTenChangesets/')}';
        var offset = ${offset};

        url = url.concat(offset);

        $('#new-changesets').html("");
        $.getJSON(url, function(data) {
            for(i = 0; i < data.length; i++) {
                var changesets = {
                    author: data[i].author,
                    identifier: data[i].identifier,
                    date: data[i].date,
                    number: data[i].id
                }
                $('#new-changesets').append($("#showdata").render(changesets));
            }
        });

    });
</script>


</body>
</html>