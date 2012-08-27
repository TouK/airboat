<!doctype html>
<html>
<head>
  <title>Admin page</title>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>

    <link rel="stylesheet" type="text/css" href="${createLink(uri: '/libs/gritter/css/jquery.gritter.css')}" />
    <script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript" src="${createLink(uri: '/libs/gritter/js/jquery.gritter.js')}"></script>

</head>
<body>

    <div class="span12 well-large" >
        <div class='container'>
            <div class="span6 offset4 well well-large">
                <h1>You're on Admin page, think twice!</h1>

                <div class="well-small">
                    <h2>Projects:</h2>
                    <ul id="nameSelect"></ul>
                </div>


                <div class="well-small">
                    <h2>Add project:</h2>
                    <div class="alert-error" id="addingErrors"></div>
                    <form action='${postUrl}' class="form-inline" method='POST' id='addProjectForm'>
                        <div class="errors"></div>
                        <fieldset>
                            <div class="control-group">


                                <div class="controls">
                                    <input id="urlInput" type="text" name="url" placeholder="git url">
                                    <input id="nameInput" type="text" name="name" placeholder="name">
                                    <button type="submit" class="btn btn-primary" >Add</button>
                                </div>
                            </div>
                        </fieldset>

                    </form>
                </div>
                <a type="button" href="./" class="btn btn-primary">Ok</a>
            </div>
        </div>
    </div>



<script id="selectProjectOptions" type="text/x-jsrender">
    <li>{{>name}}  <i class="icon-remove" onclick="confirmRemoval('{{>name}}')"></i></li>
</script>



<script type="text/javascript">

    if($.cookies.get( 'skin' )) {
        $("#skin").attr("href", $.cookies.get( 'skin').href);
    }
    $('#addProjectForm').submit(addProject);


    $(document).ready( function(){
       appendProjectOptionsToSelection("#nameSelect");


    });

    var noticeParameters = {
        title: '',
        text: '',
        sticky: false,
        time: 4000
    }

    function appendProjectOptionsToSelection(id) {

        $.getJSON("${createLink(uri: '/project/names')}", function (namesOfProjects) {
            var options = $("#selectProjectOptions").render(namesOfProjects);
            $(id).html(options);
        });
    }
    function addProject() {
        var name = $("#nameInput").val();
        var url = $("#urlInput").val();
        if(!name || !url) {
            $("#addingErrors").text("You have to fill both fields.");
            return false;
        }
        $.post("${createLink(uri: '/project/create')}", {url: url, name: name}, function (project) {
            appendProjectOptionsToSelection("#nameSelect");
            eraseForms();
            var additionNotice = noticeParameters;
            additionNotice.text = project.message;
            additionNotice.title = "Adding...";
            $.gritter.add(additionNotice);
            $("#addingErrors").text(project.errors);
        }, 'json');


        return false;
    }

    function removeProject(name) {
        $.post("${createLink(uri: '/project/remove')}", {name: name}, function (project) {
            var removalNotice = noticeParameters;
            removalNotice.text = "Project " + name + " removed.";
            removalNotice.title = "Removing...";
            $.gritter.add(removalNotice);

        }, 'json').then(function() {
                    eraseForms();
                    appendProjectOptionsToSelection("#nameSelect");
                })

    }

    function eraseForms(){
        $("#nameSelect").select("");
        $("#nameInput").val("");
        $("#urlInput").val("");
    }
    function confirmRemoval(name) {
        $("<div>Are you sure about the removal of project " + name + "?</div>").dialog({ buttons:[
            {
                text:"Ok",
                click:function () {
                    $(this).dialog("close");
                    removeProject(name);
                }
            },
            {
                text:"Cancel",
                click:function () {
                    $(this).dialog("close");
                }

            }
        ] });
        return false;
    }

</script>
</body>
</html>