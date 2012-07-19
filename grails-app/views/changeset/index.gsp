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

        <!-- Jquery pagination -->
        <script type="text/javascript" src="${createLink(uri:'/js/scrollpagination.js')}"></script>
        <link href="${createLink(uri:'/css/scrollpagination_demo.css')}" rel="stylesheet" media="screen" />

    </head>

    <body>
     <!-- function to handle click for more info in new layer for chosen changeset -->
    <script type="text/javascript">
        function popInfoBox (id)         {

            var identifier = id
            $(".show-changeset-button").colorbox({opacity:0.3 ,
                    inline: true,
                    width:"80%",
                    height:"80%" ,
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
                $.getJSON(fileUrl, function(data) {
                    for(i = 0; i < data.length; i++) {
                        var files = {
                            name: data[i].name,
                            identifier: data[i].id

                        }
                        $('#layer_files').append($("#project-files").render(files));

                    }
                });
            },
                onLoad:function(){
                    //code
                }
            });
        }
    </script>




   <h3><a href="#" id="getdata-button" >Get Last Changes</a>   </h3>

    <!-- ==========container=============== -->

          <h2>      Author
               Idenifier
                Date
               More   </h2>
    <br />

        <div id="content"></div>

         <!-- =============template=============== -->
        <script id="showdata" type="text/x-jsrender">
        <hr />
               <div class="changeset">
               <div class="changeset-header">
                  <h3>
                    Author: {{>author}},

                    Identifier: {{>identifier}},

                    Date: {{>date}},
                  </h3>
                <div class="buttons" style="float:right">
                    <button type="button" class="show-changeset-button" href="#inline_content" onclick="popInfoBox({{>number}})">Info</button>
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

        <!--- ============script================= -->
        <!-- generates list of changesets -->
        <script type="text/javascript">
        var offset = 2;
        $(document).ready(function() {

                $('#getdata-button').live('click', function(){
                    $('#content').html("");
                    $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', function(data) {
                       for(i = 0; i < data.length; i++) {
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date,
                                number: data[i].id
                            }
                            $('#content').append($("#showdata").render(changesets));
                        }
                    });
                });
            $('#get-more-data-button').live('click', function(){

                    var url = '${createLink(uri:'/changeset/getNextTenChangesets/')}';
                    url = url.concat(offset);
                    offset = offset +1;

                    $.getJSON(url, function(data) {
                        for(i = 0; i < data.length; i++) {
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date,
                                number: data[i].id
                            }
                            $('#content2').append($("#showdata").render(changesets));
                        }
                    });
                });
            });


        </script>


    <!-- template for a new layer -->
    <div style='display:none'>
        <div id='inline_content' style='padding:10px; background:#fff;'>
            <h1>Last changeset</h1>

            <div id="layer">
                <!-- ==========container=============== -->

            <div id="layer_content"></div>

            </div>
            <h2>Files changed in commit:</h2>
            <div id="layer_files">
            </div>
    </div>
    </div>

    <div id="content2">

    </div>

    <h3><a href="#" id="get-more-data-button">Older</a>   </h3>
    <div class="loading" id="loading">Wait a moment... it's loading!</div>
    <div class="loading" id="nomoreresults">Sorry, no more results for your pagination demo.</div>



    </body>
</html>