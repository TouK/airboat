<!doctype html>
<html>
    <head>
        <g:javascript library="jquery" />
        <r:layoutResources/>
          <link href="http://borismoore.github.com/jsviews/demos/resources/presentation.css" rel="stylesheet" type="text/css" />
        <link href="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.css" rel="stylesheet" type="text/css" />
        <script src="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.min.js" type="text/javascript"></script>



        <!--###################COLORBOX###################-->
        <link media="screen" rel="stylesheet" href=" ${createLink(uri:'/css/colorbox.css')}" />
        <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
        <script src="${createLink(uri:'/js/jquery.colorbox-min.js')}" type="text/javascript"></script>

        <script src="http://borismoore.github.com/jsviews/jsrender.js" type="text/javascript"></script>

        <!-- =============COLORBOX script===============-->
        <script type="text/javascript">
            $(function()
            {
                $('#link_content').colorbox({opacity:0.3});
            });
        </script>


    </head>
    <body>



    <a href="#" id="getdata-button">Get Last Changes</a>
    <!-- ==========container=============== -->
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
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date
                            }
                            $('#content').append($("#showdata").render(changesets));
                        }
                    });
                });
            });
        </script>


    <!-- =============COLORBOX=============== -->
    <a href='${createLink(uri:'/changeset/getLastChangesets')}' id='link_content' >Kliknij mnie</a>

    <script>
        $(document).ready(function(){
            //Examples of how to assign the ColorBox event to elements

            $(".iframe").colorbox({iframe:true, width:"85%", height:"85%"});


            //Example of preserving a JavaScript event for inline calls.
            $("#click").click(function(){
                $('#click').css({"background-color":"#f00", "color":"#fff", "cursor":"inherit"}).text("Open this window again and this message will still be here.");
                return false;
            });
        });
    </script>
    <p><a class='iframe' href="${createLink(uri:'/changeset/list')}">Click to see a layer which content is /changeset/list!</a></p>
    <p>It should look pretty different, but we've got problem with CSS :< </p>
    </body>
</html>