<!doctype html>
<html>
<head>
    <title>Admin page</title>
    <meta name="layout" content="main"/>
    <script src="${createLink(uri: '/libs/jquery.cookie/jquery.cookies.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap-modified/bootstrap-modal.js')}" type="text/javascript"></script>

    %{--FIXNE get rid of this or add as a normal dependency--}%
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
                <div class="modal hide fade" id="confirmRemoval">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h3>Confirm removal</h3>
                    </div>
                    <div class="modal-body">
                        <p>Do You want to remove <span class='project'></span> project?</p>
                    </div>
                    <div class="modal-footer">
                        <a href="#" class="btn" class="close" data-dismiss="modal" aria-hidden="true">No</a>
                        <a href="javascript:void(0)" class="btn btn-primary"
                           onclick="removeProject($(this).parents('#confirmRemoval').data('modal').options.project)">Yes</a>
                    </div>
                </div>
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
            <a type="button" href="./" class="btn btn-primary">Go back</a>
        </div>
    </div>
</div>



<script id="selectProjectOptions" type="text/x-jsrender">
    <li>{{>name}}  <a href="#confirmRemoval" role="button" data-toggle="modal" data-project="{{>name}}"><i class="icon-remove"></i></a></li>
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

        $.getJSON(uri.project.names, function (namesOfProjects) {
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
        $("#confirmRemoval").modal("hide");
       $.post("${createLink(uri: '/project/remove')}", {name:name},function (project) {
            var removalNotice = "Project " + name + project.message;
            $("#removalSuccess").text(removalNotice);
        }, 'json').then(function () {
                    eraseForms();
                    appendProjectOptionsToSelection("#nameSelect");
                });
    }

    function eraseForms() {
        $("#nameSelect").select("");
        $("#nameInput").val("");
        $("#urlInput").val("");
    }

    $('#confirmRemoval').on('show', function (event) {
        var name = $(this).data('modal').options.project;
        $(this).find('.project').html(name);
    })

</script>
</body>
</html>
