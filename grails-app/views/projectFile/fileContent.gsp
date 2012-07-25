
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:javascript library="jquery" />
    <r:layoutResources/>
    <script src="${createLink(uri:'/js/codemirror.js')}"></script>
    <script src="${createLink(uri:'/js/javascript.js')}"></script>
    <link rel="stylesheet" href="${createLink(uri:'/css/codemirror.css')}">

  <title></title>
</head>
<body>

<script type="text/javascript">
$(document).ready(function() {

    var fileContentUrl = '${createLink(uri:'/projectFile/getFileWithContent/')}';
    fileContentUrl = fileContentUrl.concat("88");
    var fileContent;
    $.getJSON(fileContentUrl, function(data) {
        fileContent = data.content;
        $("#code").html(fileContent);
        var myTextarea = document.getElementById("code") ;
        var editor = CodeMirror.fromTextArea(myTextarea, {

            mode:  "javascript",

            lineNumbers: true});


    });

});

</script>
<textarea rows="30" cols="100" id="code"></textarea>
        <p id="test">Hej!</p>
</body>
</html>