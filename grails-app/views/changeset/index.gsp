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


<script type="text/javascript">
    $(function(){
        $('#content2').scrollPagination({
            'contentPage': "${createLink(uri:'/changeset/getLastChangesets/')}", // the page where you are searching for results
            'contentData': {}, // you can pass the children().size() to know where is the pagination
            'scrollTarget': $(window), // who gonna scroll? in this example, the full window
            'heightOffset': 10, // how many pixels before reaching end of the page would loading start? positives numbers only please
            'beforeLoad': function(){ // before load, some function, maybe display a preloader div
                $('#loading').fadeIn();
            },
            'afterLoad': function(elementsLoaded){ // after loading, some function to animate results and hide a preloader div
                $('#loading').fadeOut();
                var i = 0;
                $(elementsLoaded).fadeInWithDelay();
                if ($('#content').children().size() > 100){ // if more than 100 results loaded stop pagination (only for test)
                    $('#nomoreresults').fadeIn();
                    $('#content').stopScrollPagination();
                }
            }
        });

        // code for fade in element by element with delay
        $.fn.fadeInWithDelay = function(){
            var delay = 0;
            return this.each(function(){
                $(this).delay(delay).animate({opacity:1}, 200);
                delay += 100;
            });
        };

    });
    </script>
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

               <div class="author">
                    {{>author}}
               </div>
                   <div class="identifier">
                    {{>identifier}}
               </div>

                <div class="date">
                    {{>date}}
               </div>
                <div class="buttons">
                    <button type="button" class="show-changeset-button" href="#inline_content" onclick="popInfoBox({{>number}})">Info</button>
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
        <!-- generates table with listed changes -->
        <script type="text/javascript">
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

    <ul id="content2">
        <li><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc elementum elementum felis. Quisque porta turpis nec eros consectetur lacinia. Pellentesque sagittis adipiscing egestas. </p></li>
        <li><p>Aliquam dapibus tincidunt odio. Phasellus volutpat dui nec ante volutpat euismod. </p></li>
        <li><p>Phasellus vehicula turpis nec dui facilisis eget condimentum risus ullamcorper. Nunc imperdiet, tortor ultrices aliquam eleifend, nisl turpis venenatis dui, at vestibulum magna tellus in tortor. </p></li>
        <li><p>Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris tincidunt nisi in tortor tincidunt ut ullamcorper lectus dapibus.  </p></li>
        <li><p>Aenean interdum dui vitae purus molestie nec placerat nibh semper. Maecenas ultrices elementum dapibus. Aenean feugiat, metus in mattis mattis, justo nisi dignissim libero, ac volutpat dui nibh quis metus.</p></li>
        <li><p> Morbi eget tristique dui. Vivamus nec turpis eu nisi euismod accumsan sed in tortor. Maecenas laoreet leo ut tortor viverra facilisis.</p></li>
    </ul>
    <div class="loading" id="loading">Wait a moment... it's loading!</div>
    <div class="loading" id="nomoreresults">Sorry, no more results for your pagination demo.</div>



    </body>
</html>