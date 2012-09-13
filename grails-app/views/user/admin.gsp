<!doctype html>
<html>
<head>
    <title>Admin page</title>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet"
          type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>

</head>

<body>

<div class="span12 well-large">
    <div class='container'>
        <div class="span6 offset4 well well-large">
            <div class="well-small">
                <h2>Projects:</h2>

                <div class="alert-success" id="removalSuccess"></div>
                <ul id="nameSelect"></ul>
            </div>

            <div class="well-small">
                <h2>Add project:</h2>

                <div class="errors"></div>

                <div class="alert-success" id="addingSuccess"></div>

                <form action='${postUrl}' class="form-inline" method='POST' id='addProjectForm'>
                    <div class="errors"></div>
                    <fieldset>
                        <div class="control-group">

                            <div class="controls">
                                <input id="urlInput" type="text" name="url" placeholder="git url">
                                <input id="nameInput" type="text" name="name" placeholder="name">
                                <button type="submit" class="btn btn-primary">Add</button>
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

<script id="formErrorsTemplate" type="text/x-jsrender">
    <ul class="alert-block" role="alert">
        {{for #data}}
        <li class="alert-error" {{if field}}data-field-id="{{:field}}"{{/if}}>{{:message}}</li>
        {{/for}}
    </ul>
</script>



<script type="text/javascript">

    if ($.cookies.get('skin')) {
        $("#skin").attr("href", $.cookies.get('skin').href);
    }
    $('#addProjectForm').submit(addProject);

    $(document).ready(function () {
        appendProjectOptionsToSelection("#nameSelect");
    });

    function appendProjectOptionsToSelection(id) {

        $.getJSON("${createLink(uri: '/project/names')}", function (namesOfProjects) {
            var options = $("#selectProjectOptions").render(namesOfProjects);
            $(id).html(options);
        });
    }
    function addProject() {
        var name = $("#nameInput").val();
        var url = $("#urlInput").val();

        $.post("${createLink(uri: '/project/create')}", {url:url, name:name}, function (project) {
            appendProjectOptionsToSelection("#nameSelect");
            if (!project.errors) {
                eraseForms();
                var message = project.message;
                $("#addingSuccess").text(message).hide().fadeIn();
            }
            else {
                $("#addingSuccess").text("");
                $('#addProjectForm .errors')
                        .html($('#formErrorsTemplate').render([project.errors]))
                        .hide().fadeIn()
            }
        }, 'json');


        return false;
    }

    function removeProject(name) {
        $.post("${createLink(uri: '/project/remove')}", {name:name},function (project) {
            var removalNotice = "Project " + name + project.message;
            $("#removalSuccess").text(removalNotice);
        }, 'json').then(function () {
                    eraseForms();
                    appendProjectOptionsToSelection("#nameSelect");
                })
    }

    function eraseForms() {
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