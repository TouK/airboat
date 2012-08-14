var previousExpandedForFilesChangesetId; //FIXME remove - this does not belong here, it's here for popovers setup...
function showFile(changesetId, fileId) {

    var fileContentUrl = uri.projectFile.getFileWithContent;
    fileContentUrl += fileId;
    var fileContent;
    $.getJSON(fileContentUrl, function (file) {
        var title = $("#fileTitleTemplate").render({
            fileName:divideNameWithSlashesInTwo(file.name),
            changesetId:changesetId,
            fileId:fileId
        });

        $("#content-files-title-" + changesetId).html(title);

        $.SyntaxHighlighter.init({lineNumbers: true});

        $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
        $("#content-files-" + changesetId + " .codeViewer")
            .text(file.content)
            .addClass("language-" + file.filetype)
            .syntaxHighlight();

        $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
            $(element).click(function () {
                $('#content-files-' + changesetId + ' .linenums li').popover("hide");
                $(element).popover("show");

            });
            //TODO check if creating the content of the popover (i.e. commentForm) can be deferred to popover activation
            var commentForm = $("#addLineCommentFormTemplate").render({fileId:fileId, changesetId:changesetId, lineNumber:i + 1 });
            var popoverTitle = $("#popoverTitleTemplate").render({
                fileName:divideNameWithSlashesInTwo(file.name),
                changesetId:changesetId,
                fileId:fileId,
                lineNumber:i
            });

            $(element).popover({content:commentForm, title:popoverTitle, placement:"left", trigger:"manual" });
        });
    });
    $("#content-files-" + changesetId).show(100);
    $('#content-files-span-' + changesetId).show(100);
    $("#content-files-title-" + changesetId).show(100);
    if (previousExpandedForFilesChangesetId != null) {
        hidePopovers(previousExpandedForFilesChangesetId);
    }


    $("#sh-btn-" + changesetId + fileId).hide();
    appendDiff(changesetId, fileId);
    previousExpandedForFilesChangesetId = changesetId;

}

function hideFile(changesetId, fileId) {
    $("#content-files-" + changesetId).hide();

    $("#sh-btn-" + changesetId + fileId).show();
    $('#content-files-span-' + changesetId).hide();
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
    hidePopovers(changesetId);
    $("#content-files-title-" + changesetId).hide();
}


function appendDiff(changesetId, fileId) {

    var diffUrl =  "projectFile/getDiff/" + fileId;

    $.getJSON(diffUrl, function (projectDiff) {
        var diff = $("#diffTemplate").render({changesetId: changesetId});
        $("#diff-" + changesetId).html(diff);

        $.SyntaxHighlighter.init({lineNumbers: false});

        $("#diff-box-" + changesetId).html("<pre class='codeViewer'/>");
        $("#diff-box-" + changesetId + " .codeViewer")
            .html(colorizeDiff(projectDiff.rawDiff))
            .addClass("language-" + projectDiff.fileType)
            .syntaxHighlight();

    })
}

function colorizeDiff(text) {
    var lines = escapeHTML(text).split("\n");
    for(i = 0; i< lines.length ; i++) {
        if(lines[i][0] == '+') {
            lines[i] = '<span style="background-color:rgba(73,203,30,0.69)">' + lines[i] +"</span>";
        }
        else if(lines[i][0] == '-') {
            lines[i] = '<span style="background-color:rgba(217,52,51,0.82)">' + lines[i] +"</span>";
        }
        else
            lines[i] = '<span>' + lines[i] + '</span>'
    }
    return lines.join("\n");
}

function escapeHTML(text) {
    return $('<div/>').text(text).html();
}

function showDiff(changesetId) {
    $("#diff-box-" + changesetId).show(100);
    $("#button-hiding-diff-"+changesetId).show(100);
    $("#button-showing-diff-"+changesetId).hide();
}

function hideDiff(changesetId) {
    $("#diff-box-" + changesetId).hide(100);
    $("#button-showing-diff-"+changesetId).show(100);
    $("#button-hiding-diff-"+changesetId).hide();
}