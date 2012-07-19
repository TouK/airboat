<%--
  Created by IntelliJ IDEA.
  User: touk
  Date: 19.07.12
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
   <g:javascript library="jquery" />
    <r:layoutResources/>
    <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
    <!--<script type="text/javascript" src="${createLink(uri:'/js/prototype.js')}"></script>    -->


</head>
<body>
<h3><a href="#" id="send-JSON">send JSON</a>   </h3>

<script type="text/javascript">
    $(document).ready(function() {

        $('#send-JSON').live('click', function(){
            var JSONObject = new Object;
            JSONObject.id = "23";
            JSONObject.name = "Test 1";
            var JSONstring;
            JSONstring =  JSON.stringify(JSONObject);



            var url = "${createLink(controller:'UserComment', action:'receiveJSON')}";
            $.ajax({
                url: url,
                context: document.body,
                data: JSONstring,
                async: true
            }).done(function() {

                    });
        });
    });


</script>
</body>
</html>