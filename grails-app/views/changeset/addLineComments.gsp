<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Add line comments</title>

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
        function addLineComment(fileIdentifier) {


            var text = $('#add-comment').val();
            var lineNumber = $('#line-number').val();
            var fileId = $('#file-id').val();
            var author =  $('#author').val();
            if(text == "" || lineNumber == "") {
                return false
            }

            var url = "${createLink(controller:'LineComment', action:'addComment')}";

            $.post(url, {  text: text, lineNumber: lineNumber, fileId: fileId, author: author });

        }
    </script>

</head>
<body>

<form class="add_comment .form-inline"><textarea onfocus=" $('#btn-').show(100); $('#c-btn-').show(100); this.style.height='100px'; this.style.width='400px'; "
                                                 id="add-comment" placeholder="Add comment..." style="height:200px"></textarea>
    <input id="line-number" type="text" class="input-small" placeholder="Line number"/></input>
    <input id="file-id" type="text" class="input-small" placeholder="File id"/></input>
    <input id="author" type="text" class="input-small" placeholder="name"/></input>
    <br />
    <button type="button"  class="btn" id="btn" onClick="addLineComment()">Add comment</button>
    <button type="button" class="btn" id="c-btn" onClick="cancelLineComment()">Cancel</button>
</form>

</body>
</html>