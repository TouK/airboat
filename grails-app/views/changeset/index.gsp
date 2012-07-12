<!doctype html>
<html>
    <head>
        <g:javascript library="jquery" />
        <r:layoutResources/>
        <link href="http://borismoore.github.com/jsviews/demos/resources/presentation.css" rel="stylesheet" type="text/css" />
        <link href="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.css" rel="stylesheet" type="text/css" />
        <script src="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.min.js" type="text/javascript"></script>

        <script src="http://borismoore.github.com/jsviews/jsrender.js" type="text/javascript"></script>
    </head>
    <body>
           <!-- ==========container=============== -->
    <a href="#" id="getdata-button">Get Last Changes</a>
    <table>
        <thead>
            <tr>
                <th>Author</th>
                <th>Idenifier</th>
                <th>Date</th>
            </tr>
        </thead>
        <tbody id="content"></tbody>
    </table>


         <!-- =============template=============== -->
        <script id="showdata" type="text/x-jsrender">
            <tr>

                <td>
                    {{>author}}
                </td>
                <td>
                    {{>identifier}}

                </td>
                <td>
                    {{>date}}
                </td>

            </tr>

        </script>
        <!--- ============script================= -->
        <script type="text/javascript">
            $(document).ready(function() {
                $('#getdata-button').live('click', function(){
                    $('#content').html("");
                    $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', function(data) {
                       for(i = 0; i < data.length; i++) {
                            var change = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date
                            }
                            $('#content').append($("#showdata").render(change));
                        }
                    });
                });
            });
        </script>

    </body>
</html>