<!doctype html>
<html>
    <head>
        <!-- TODO  tidy up resources section, it's a mess -->
        <g:javascript library="jquery" />
        <r:layoutResources/>

        <!-- jsviews -->
        <link href="http://borismoore.github.com/jsviews/demos/resources/presentation.css" rel="stylesheet" type="text/css" />
        <link href="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.css" rel="stylesheet" type="text/css" />
        <script src="http://borismoore.github.com/jsviews/demos/resources/syntaxhighlighter.min.js" type="text/javascript"></script>

        <!--###################COLORBOX###################--> <!-- TODO ASCII ART... -->
        <link media="screen" rel="stylesheet" href=" ${createLink(uri:'/css/colorbox.css')}" />
        <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>

        <script src="${createLink(uri:'/js/jquery.colorbox-min.js')}" type="text/javascript"></script>
        <script src="http://borismoore.github.com/jsviews/jsrender.js" type="text/javascript"></script>

        <!-- Jquery pagination -->
        <script type="text/javascript" src="${createLink(uri:'/js/scrollpagination.js')}"></script>
        <link href="${createLink(uri:'/css/scrollpagination_demo.css')}" rel="stylesheet" media="screen" />       <!-- TODO not used anymore - remove -->
        <link href="${createLink(uri:'/css/main-content.css')}" rel="stylesheet" media="screen" />

        <link rel="stylesheet" href="${resource(dir: 'css', file: 'codereview.css')}" type="text/css">


        <!--TODO extract function scripts, because they're making this view unreadable -->

        <script type="text/javascript">
            function addComment(changesetId) {
                var rawText = $('textarea#add-comment-'+changesetId.toString()).val();
                var text = "<h1>your comment is: " +rawText ;
                var username =      $('textarea#username-'+changesetId.toString()).val();
                text = text + " written by: " + username  + " changeset id: " + changesetId.toString() + "</h1><br />";

                var comment = {
                    author: username,
                    content: rawText,
                    date: new Date()
                }
                $('#comments-'+changesetId.toString()).append($("#comment-template").render(comment));
                var url = "${createLink(controller:'UserComment', action:'addComment')}";

                $.post(url, { username: username, changesetId: changesetId, content: rawText } );
                var howManyComments = $('#comments-count-'+changesetId.toString()).html();
                howManyComments = (parseInt(howManyComments) + 1).toString();
                $('#comments-count-'+changesetId.toString()).html(howManyComments.toString());
            }
        </script>

    </head>

    <body>


    <!-- function to get picture from gravatar-->
    <script>
        function get_gravatar(email, size) {
            // MD5 (Message-Digest Algorithm) by WebToolkit
            // http://www.webtoolkit.info/javascript-md5.html
            var MD5=function(s){function L(k,d){return(k<<d)|(k>>>(32-d))}function K(G,k){var I,d,F,H,x;F=(G&2147483648);H=(k&2147483648);I=(G&1073741824);d=(k&1073741824);x=(G&1073741823)+(k&1073741823);if(I&d){return(x^2147483648^F^H)}if(I|d){if(x&1073741824){return(x^3221225472^F^H)}else{return(x^1073741824^F^H)}}else{return(x^F^H)}}function r(d,F,k){return(d&F)|((~d)&k)}function q(d,F,k){return(d&k)|(F&(~k))}function p(d,F,k){return(d^F^k)}function n(d,F,k){return(F^(d|(~k)))}function u(G,F,aa,Z,k,H,I){G=K(G,K(K(r(F,aa,Z),k),I));return K(L(G,H),F)}function f(G,F,aa,Z,k,H,I){G=K(G,K(K(q(F,aa,Z),k),I));return K(L(G,H),F)}function D(G,F,aa,Z,k,H,I){G=K(G,K(K(p(F,aa,Z),k),I));return K(L(G,H),F)}function t(G,F,aa,Z,k,H,I){G=K(G,K(K(n(F,aa,Z),k),I));return K(L(G,H),F)}function e(G){var Z;var F=G.length;var x=F+8;var k=(x-(x%64))/64;var I=(k+1)*16;var aa=Array(I-1);var d=0;var H=0;while(H<F){Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=(aa[Z]|(G.charCodeAt(H)<<d));H++}Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=aa[Z]|(128<<d);aa[I-2]=F<<3;aa[I-1]=F>>>29;return aa}function B(x){var k="",F="",G,d;for(d=0;d<=3;d++){G=(x>>>(d*8))&255;F="0"+G.toString(16);k=k+F.substr(F.length-2,2)}return k}function J(k){k=k.replace(/rn/g,"n");var d="";for(var F=0;F<k.length;F++){var x=k.charCodeAt(F);if(x<128){d+=String.fromCharCode(x)}else{if((x>127)&&(x<2048)){d+=String.fromCharCode((x>>6)|192);d+=String.fromCharCode((x&63)|128)}else{d+=String.fromCharCode((x>>12)|224);d+=String.fromCharCode(((x>>6)&63)|128);d+=String.fromCharCode((x&63)|128)}}}return d}var C=Array();var P,h,E,v,g,Y,X,W,V;var S=7,Q=12,N=17,M=22;var A=5,z=9,y=14,w=20;var o=4,m=11,l=16,j=23;var U=6,T=10,R=15,O=21;s=J(s);C=e(s);Y=1732584193;X=4023233417;W=2562383102;V=271733878;for(P=0;P<C.length;P+=16){h=Y;E=X;v=W;g=V;Y=u(Y,X,W,V,C[P+0],S,3614090360);V=u(V,Y,X,W,C[P+1],Q,3905402710);W=u(W,V,Y,X,C[P+2],N,606105819);X=u(X,W,V,Y,C[P+3],M,3250441966);Y=u(Y,X,W,V,C[P+4],S,4118548399);V=u(V,Y,X,W,C[P+5],Q,1200080426);W=u(W,V,Y,X,C[P+6],N,2821735955);X=u(X,W,V,Y,C[P+7],M,4249261313);Y=u(Y,X,W,V,C[P+8],S,1770035416);V=u(V,Y,X,W,C[P+9],Q,2336552879);W=u(W,V,Y,X,C[P+10],N,4294925233);X=u(X,W,V,Y,C[P+11],M,2304563134);Y=u(Y,X,W,V,C[P+12],S,1804603682);V=u(V,Y,X,W,C[P+13],Q,4254626195);W=u(W,V,Y,X,C[P+14],N,2792965006);X=u(X,W,V,Y,C[P+15],M,1236535329);Y=f(Y,X,W,V,C[P+1],A,4129170786);V=f(V,Y,X,W,C[P+6],z,3225465664);W=f(W,V,Y,X,C[P+11],y,643717713);X=f(X,W,V,Y,C[P+0],w,3921069994);Y=f(Y,X,W,V,C[P+5],A,3593408605);V=f(V,Y,X,W,C[P+10],z,38016083);W=f(W,V,Y,X,C[P+15],y,3634488961);X=f(X,W,V,Y,C[P+4],w,3889429448);Y=f(Y,X,W,V,C[P+9],A,568446438);V=f(V,Y,X,W,C[P+14],z,3275163606);W=f(W,V,Y,X,C[P+3],y,4107603335);X=f(X,W,V,Y,C[P+8],w,1163531501);Y=f(Y,X,W,V,C[P+13],A,2850285829);V=f(V,Y,X,W,C[P+2],z,4243563512);W=f(W,V,Y,X,C[P+7],y,1735328473);X=f(X,W,V,Y,C[P+12],w,2368359562);Y=D(Y,X,W,V,C[P+5],o,4294588738);V=D(V,Y,X,W,C[P+8],m,2272392833);W=D(W,V,Y,X,C[P+11],l,1839030562);X=D(X,W,V,Y,C[P+14],j,4259657740);Y=D(Y,X,W,V,C[P+1],o,2763975236);V=D(V,Y,X,W,C[P+4],m,1272893353);W=D(W,V,Y,X,C[P+7],l,4139469664);X=D(X,W,V,Y,C[P+10],j,3200236656);Y=D(Y,X,W,V,C[P+13],o,681279174);V=D(V,Y,X,W,C[P+0],m,3936430074);W=D(W,V,Y,X,C[P+3],l,3572445317);X=D(X,W,V,Y,C[P+6],j,76029189);Y=D(Y,X,W,V,C[P+9],o,3654602809);V=D(V,Y,X,W,C[P+12],m,3873151461);W=D(W,V,Y,X,C[P+15],l,530742520);X=D(X,W,V,Y,C[P+2],j,3299628645);Y=t(Y,X,W,V,C[P+0],U,4096336452);V=t(V,Y,X,W,C[P+7],T,1126891415);W=t(W,V,Y,X,C[P+14],R,2878612391);X=t(X,W,V,Y,C[P+5],O,4237533241);Y=t(Y,X,W,V,C[P+12],U,1700485571);V=t(V,Y,X,W,C[P+3],T,2399980690);W=t(W,V,Y,X,C[P+10],R,4293915773);X=t(X,W,V,Y,C[P+1],O,2240044497);Y=t(Y,X,W,V,C[P+8],U,1873313359);V=t(V,Y,X,W,C[P+15],T,4264355552);W=t(W,V,Y,X,C[P+6],R,2734768916);X=t(X,W,V,Y,C[P+13],O,1309151649);Y=t(Y,X,W,V,C[P+4],U,4149444226);V=t(V,Y,X,W,C[P+11],T,3174756917);W=t(W,V,Y,X,C[P+2],R,718787259);X=t(X,W,V,Y,C[P+9],O,3951481745);Y=K(Y,h);X=K(X,E);W=K(W,v);V=K(V,g)}var i=B(Y)+B(X)+B(W)+B(V);return i.toLowerCase()};
            var size = size || 80;
            return 'http://www.gravatar.com/avatar/' + MD5(email) + '.jpg?s=' + size;
        }
       </script>

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
        function showCommentsToChangeset(id){
            $('#comments-'+id.toString()).html("");
            var fileUrl = '${createLink(uri:'/userComment/returnCommentsToChangeset/')}';
            fileUrl = fileUrl.concat(id.toString());
            $.getJSON(fileUrl, function(data) {
                for(i = 0; i < data.length; i++) {
                    var comments = {
                        author: data[i].author,
                        date: data[i].dateCreated,
                        content: data[i].content

                    }
                    var howManyComments = data.length;
                    $('#comments-count-'+id.toString()).html(howManyComments.toString());
                    $('#comments-'+id.toString()).append($("<h3>Comments: </h3>"));
                    $('#comments-'+id.toString()).append($("#comment-template").render(comments));

                }
            });
        }
        function hideCommentsToChangeset(id){
            $('#comments-'+id.toString()).html("");
        }


    </script>





    <!-- ==========container=============== -->

    <br />

        <div id="content" class="main-content"></div>

       <!--TODO extract these templates, put in another file gathering js-view templates or something -->

         <!-- =============template=============== -->
        <script id="showdata" type="text/x-jsrender">
        <hr />
               <div class="changeset">
               <div class="changeset-header">


                      <img src="{{>email}}">

                     {{>author}},

                    {{>identifier}},

                    {{>date}},

                <div class="buttons" style="float:right">
                    <button type="button" class="show-changeset-button" href="#inline_content" onclick="popInfoBox({{>number}})">Info</button>
                </div>
               </div>
               <div class="changeset-content" >
                   <b>Comment written during commiting:</b> {{>commitComment}}
               </div>
                   <div class="comments-preview">
                   <h3>There are <b id="comments-count-{{>number}}"> {{>howManyComments}}</b> comments
                       <button type="button" class="show-comments-button" href="#" onclick="showCommentsToChangeset({{>number}})"> Show comments</button>
                       <button type="button" class="hide-comments-button" href="#" onclick="hideCommentsToChangeset({{>number}})"> Hide comments</button>
                   </h3>
                   </div>
                   <div class="comments" id="comments-{{>number}}">


                   </div>
                   <div class="add_comment">


                           <div class="add-comment-content">
                           <label>Comment</label>
                           <br />
                           <textarea  cols="90" rows="5" id="add-comment-{{>number}}">Write your comment here!</textarea>
                           </div>

                            <div class="add-comment-username">
                           <label>Name</label>
                           <br>
                           <textarea rows="1"  cols="30" id="username-{{>number}}">your name!</textarea>
                            <br />
                            </div>





                   </div>
                   <button type="button" onClick="addComment({{>number}})"  href="#">Add Comment</button>
               </div>

                <br />
        </script>

     <script id="comment-template" type="text/x-jsrender">
        <div class="comments">
            <h3>Author: {{>author}}, Date:  {{>date}}</h3>


            <div class="comment-content">{{>content}}</h3>  </div>

            <h3></h3>
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


                    $('#content').html("");
                    $.getJSON('${createLink(uri:'/changeset/getLastChangesets')}', function(data) {
                       for(i = 0; i < data.length; i++) {
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,
                                date: data[i].date,
                                number: data[i].id,                                                           //TODO: duplicate code
                                commitComment: data[i].commitComment,
                                email: get_gravatar(data[i].email, 50),
                                howManyComments: data[i].howManyComments
                            }
                            $('#content').append($("#showdata").render(changesets));
                        }
                    });

            $('#get-more-data-button').live('click', function(){

                    var url = '${createLink(uri:'/changeset/getNextTenChangesets/')}';
                    url = url.concat(offset);
                    offset = offset +1;

                    $.getJSON(url, function(data) {
                        for(i = 0; i < data.length; i++) {
                            var changesets = {
                                author: data[i].author,
                                identifier: data[i].identifier,                        //TODO extract this <- it looks the same as above
                                date: data[i].date,
                                number: data[i].id,
                                commitComment: data[i].commitComment,
                                email: get_gravatar(data[i].email, 50),
                                howManyComments: data[i].howManyComments
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




    <h3><a href="#" id="get-more-data-button">Older</a>   </h3>
    <div class="loading" id="loading">Wait a moment... it's loading!</div>         <!--TODO remove these two lines -->
    <div class="loading" id="nomoreresults">Sorry, no more results for your pagination demo.</div>



    </body>
</html>