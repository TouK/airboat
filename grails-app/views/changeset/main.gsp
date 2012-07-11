<%@ page import="codereview.Changeset" %>
<!doctype html>
<html>
<head>
    <g:javascript library="jquery" />
    <r:layoutResources/>
</head>
<body>
    <div id="header">
     <h1>JSON response</h1>
    </div>
     <div id="content">
     </div>

    <a href="#" id="getdata-button">Get JSON Data</a>
    <div id="showdata"></div>
    <script type="text/javascript">
        $(document).ready(function(){
         $('#getdata-button').live('click', function(){
            $.getJSON('http://localhost:8080/codeReview/changeset/getLastChangesets', function(data) {
                for(i = 0; i <3; i++) {
                $('#showdata').append("<p>Author="+data[i].author+" Date="+data[i].date+" Identifier="+data[i].identifier+"</p>");
                }
            });
        });
    });
</script>
</body>
</html>