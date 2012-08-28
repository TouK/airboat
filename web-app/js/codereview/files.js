var previousExpandedForFilesChangesetId; //FIXME remove - this does not belong here, it's here for popovers setup...
function showFile(changesetIdentifier, fileId, fileChangeType) {

    var fileContentUrl = uri.projectFile.getFileWithContent + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:fileId
    });

    if (fileChangeType != 'DELETE') {
        $.getJSON(fileContentUrl, function (file) {
            var title = $("#fileTitleTemplate").render({
                fileName:divideNameWithSlashesInTwo(file.name),
                changesetId:changesetIdentifier,
                fileId:fileId
            });

            $("#content-files-title-" + changesetIdentifier).html(title);

            $.SyntaxHighlighter.init({lineNumbers:true});

            $("#content-files-" + changesetIdentifier).html("<pre class='codeViewer'/>");
            $("#content-files-" + changesetIdentifier + " .codeViewer")
                .text(file.content)
                .addClass("language-" + file.filetype)
                .syntaxHighlight();

            $('#content-files-' + changesetIdentifier + ' .linenums li').each(function (i, element, ignored) {
                $(element).click(function () {
                    $('#content-files-' + changesetIdentifier + ' .linenums li').popover("hide");
                    $(element).popover("show");

                });
                //TODO check if creating the content of the popover (i.e. commentForm) can be deferred to popover activation
                var commentForm = $("#addLineCommentFormTemplate").render({fileId:fileId, changesetId:changesetIdentifier, lineNumber:i + 1 });

                $(element).popover({content:commentForm, placement:"left", trigger:"manual" });
            });
        });
    } else {
        $("#content-files-" + changesetIdentifier).html("");
        var title = $("#fileTitleTemplate").render({
            fileName:fileName,
            changesetId:changesetIdentifier,
            fileId:fileId
        });
        $("#content-files-title-" + changesetIdentifier).html(title);
        $("#content-files-" + changesetIdentifier).html("<pre class='codeViewer'/>");
        $("#content-files-" + changesetIdentifier + " .codeViewer")
            .html("<h3>This file was removed.</h3>")

    }

    $("#content-files-" + changesetIdentifier).show();
    $('#content-files-span-' + changesetIdentifier).show();
    $("#content-files-title-" + changesetIdentifier).show();
    if (previousExpandedForFilesChangesetId != null) {
        hidePopovers(previousExpandedForFilesChangesetId);
    }

    appendDiff(changesetIdentifier, fileId);
    previousExpandedForFilesChangesetId = changesetIdentifier;

}

function hideFile(changesetId, fileId) {
    $("#content-files-" + changesetId).hide();
    $('#content-files-span-' + changesetId).hide();
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
    hidePopovers(changesetId);
    $("#content-files-title-" + changesetId).hide();
}


function appendDiff(changesetIdentifier, projectFileId) {

    var diffUrl = uri.projectFile.getDiffWithPreviousRevision + '?' + $.param({
        changesetIdentifier: changesetIdentifier, projectFileId: projectFileId
    });

    $.getJSON(diffUrl, function (projectDiff) {
        var diff = $("#diffTemplate").render({changesetId:changesetIdentifier});
        $("#diff-" + changesetIdentifier).html(diff);

        $.SyntaxHighlighter.init({lineNumbers:false});

        $("#diff-box-" + changesetIdentifier).html("<pre class='codeViewer'/>");
        $("#diff-box-" + changesetIdentifier + " .codeViewer")
            .html(colorizeDiff(projectDiff.rawDiff))
            .addClass("language-" + projectDiff.fileType)
            .syntaxHighlight();

    });
}

function colorizeDiff(text) {
    var lines = escapeHTML(text).split("\n");
    for (i = 0; i < lines.length; i++) {
        if (lines[i][0] == '+') {
            lines[i] = '<span style="background-color:rgba(73,203,30,0.69)">' + lines[i] + "</span>";
        }
        else if (lines[i][0] == '-') {
            lines[i] = '<span style="background-color:rgba(217,52,51,0.82)">' + lines[i] + "</span>";
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
    $("#button-hiding-diff-" + changesetId).show(100);
    $("#button-showing-diff-" + changesetId).hide();
}

function hideDiff(changesetId) {
    $("#diff-box-" + changesetId).hide(100);
    $("#button-showing-diff-" + changesetId).show(100);
    $("#button-hiding-diff-" + changesetId).hide();
}