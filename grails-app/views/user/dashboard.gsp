<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
    <title>Dashboard</title>
</head>
<body>

<script id="skinOptionTemplate" type="text/x-jsrender">
    <li><a href="#" rel="{{>href}}" onclick="changeSkin('{{>href}}', '{{>name}}')">{{>name}}</a></li>
</script>

<script type="text/javascript">



    if($.cookies.get( 'skin' )) {
        $("#skin").attr("href", $.cookies.get( 'skin').href);
    }



    $(document).ready(function() {
        appendProjects();
        appendLastChangesets();
        appendLastComments();
    });

    function appendProjects() {
        $.getJSON("${createLink(uri: '/user/projects')}", $.param({username: codeReview.loggedInUserName}), function (projects) {
            var projectsToAppend = $("#projectTemplate").render(projects);
            $("#projects").html(projectsToAppend);

        });
    }

    function appendLastChangesets() {
        $.getJSON("${createLink(uri: '/user/changesets')}", $.param({username: codeReview.loggedInUserName}), function (changesets) {
            var projectsToAppend = $("#changesetTemplate").render(changesets);
            $("#changesets").html(projectsToAppend);

        });
    }

    function appendLastComments() {
        $.getJSON("${createLink(uri: '/user/comments')}", $.param({username: codeReview.loggedInUserName}), function (comments) {
            var projectsToAppend = $("#commentTemplate").render(comments);
            $("#comments").html(projectsToAppend);

        });
    }

</script>

<script id="projectTemplate" type="text/x-jsrender">
    <h3>{{>name}}</h3>
    <li><a href="#" rel="{{>href}}" onclick=> {{>url}}</a></li>
</script>



<script id="changesetTemplate" type="text/x-jsrender">
<div class="">


        <div class="well-small alert-info">
            <div>

                commited to <span class="badge">{{>project.name}}</span>

                <span class=" badge badge-info">{{>date}}</span>
                <span >{{>identifier}}</span>
            </div>

            <div class=" margin-top-small margin-bottom-small"><h5>Commit message:</h5> {{>commitComment}}</div>
        </div>
               </br>


</div>
    </script>

<script id="commentTemplate" type="text/x-jsrender">

    <div class="alert {{>fromRevision}}">

        <span class="label label-info {{>name}}">{{>dateCreated}}</span>

        <div class="comment-content">{{>text}}</div>
    </div>
</script>


    <div class='container well-large '>
        <div class="well">


            <h1><sec:username/> dashboard</h1>
            <div class="row-fluid">
                <div class="span4"><h2>Projects</h2></div>
                <div class="span4"><h2>Commits</h2></div>
                <div class="span4"><h2>Comments</h2></div>
            </div>

            <div class="row-fluid">
                <div id="projects" class="span4"></div>
                <div id="changesets" class="span4"></div>
                <div id="comments" class="span4"></div>
            </div>



        </div>
    </div>

</body>
</html>