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

    <script id="changeset" type="text/x-jsrender">

        <h3>Author</h3>
         <p>   {{>author}}         </p>

        <h3>Identifier</h3>
         <p>     {{>identifier}}   </p>

        <h3>Date</h3>
         <p >   {{>date}}          </p>




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

            $(".ajax").colorbox({opacity:0.3 ,
                    inline: true,
                    width:"80%",
                    height:"80%" ,
                    onOpen:function(){
                        $('#layer_content').html("") ;
                        $.getJSON('${createLink(uri:'/changeset/getLastChangeset')}', function(data) {

                            for(i = 0; i < data.length; i++) {
                                var changesets = {
                                    author: data[i].author,
                                    identifier: data[i].identifier,
                                    date: data[i].date
                                }
                                $('#layer_content').append($("#changeset").render(changesets));
                            }
                        });

                    },
                    onLoad:function(){
                    //code
                    }
             });

        });
    </script>
    <p><p><a class='ajax' href="#inline_content" title="ChangesetList">Changeset info (Ajax)</a></p></p>
    <p>It should look pretty different, but we've got problem with CSS :< </p>

    <div style='display:none'>
        <div id='inline_content' style='padding:10px; background:#fff;'>
            <h1>Last changeset</h1>
            <p><strong>Hello, here's the layer with changeset</strong></p>
            <div id="layer">
                <!-- ==========container=============== -->

                    <div id="layer_content"></div>

            </div>
            <h2>Elephants</h2>
            <p>Elephants are large land mammals in two extant genera of the family Elephantidae: Elephas and Loxodonta,
            with the third genus Mammuthus extinct.[1] Three living species of elephant are recognized: the
            African bush elephant, the African forest elephant and the Indian or Asian elephant;[2] although some group
            the two African species into one[3] and some researchers also postulate the existence of a fourth species
            in West Africa.[4] All other species and genera of Elephantidae are extinct. Most have been extinct since
            the last ice age, although dwarf forms of mammoths might have survived as late as 2,000 BCE.[5]
            Elephants and other Elephantidae were once classified with other thick-skinned animals in a now
            invalid order, Pachydermata.
            Elephants are the largest living land animals on Earth today.[6] The elephant's gestation
            period is 22 months, the longest of any land animal.[7] At birth, an elephant calf typically weighs
            105 kilograms (230 lb).[7] They typically live for 50 to 70 years, but the oldest recorded elephant
            lived for 82 years.[8] The largest elephant ever recorded was shot in Angola in 1955.[9] This male weighed
            about 10,900 kg (24,000 lb),[10] with a shoulder height of 3.96 metres (13.0 ft), 1 metre (3.3 ft) taller
            than the average male African elephant.[10] The smallest elephants, about the size of a calf or a large pig, were
            a prehistoric species that lived on the island of Crete during the Pleistocene epoch.[11]</p>



        </div>
    </div>

    </body>
</html>