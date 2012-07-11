<!doctype html>
<html>
    <head>
        <g:javascript library="jquery" />
        <r:layoutResources/>
    </head>
    <body>

        <div id="content">
        </div>

        <a href="#" id="getdata-button">Get Last Changes</a>
        <div id="showdata"></div>

        <script type="text/javascript">
            $(document).ready(function() {
                $('#getdata-button').live('click', function(){
                    $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', function(data) {
                        for(i = 0; i < data.length; i++) {
                            $('#showdata').append("<div>Author="+data[i].author+" Date="+data[i].date+" Identifier="+data[i].identifier+"</div>");
                        }
                    });
                });
            });
        </script>

    </body>
</html>