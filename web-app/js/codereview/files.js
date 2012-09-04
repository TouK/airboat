var previousExpandedForFilesChangesetId; //FIXME remove - this does not belong here, it's here for popovers setup...

function showFile(changesetIdentifier, projectFileId, fileChangeType, fileName) {
    appendDiff(changesetIdentifier, projectFileId);

    var fileContentUrl = uri.projectFile.getFileWithContent + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    });

    if (fileChangeType != 'DELETE') {
        $.getJSON(fileContentUrl, function (file) {
            if (file.isText) {
                fillFileTitleTemplate(divideNameWithSlashesInTwo(file.name), changesetIdentifier, projectFileId);
                renderContentFileWithSyntaxHighlighter(changesetIdentifier, file, projectFileId);
                showFilesContent(changesetIdentifier);
            }
            else {
                showMessageAboutNonTextFile(changesetIdentifier);
            }
        });
    }
    else {
        cleanPreviousFilesContent(changesetIdentifier);
        fillFileTitleTemplate(fileName, changesetIdentifier, projectFileId);
        showMessageAboutRemovedFile(changesetIdentifier);
        showFilesContent(changesetIdentifier);
    }

    if (previousExpandedForFilesChangesetId != null) {
        hidePopovers(previousExpandedForFilesChangesetId);
    }
    previousExpandedForFilesChangesetId = changesetIdentifier;
}

function hideFile(changesetId, projectFileId) {
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

function showFilesContent(changesetId) {
    $('#content-files-span-' + changesetId).show();
}

function cleanPreviousFilesContent(changesetId) {
    $("#content-files-" + changesetId).html("");
}

function setContentFilesTitle(changesetId, title) {
    $("#content-files-title-" + changesetId).html(title);
}

function showMessageAboutNonTextFile(changesetId) {
    $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
    $("#content-files-" + changesetId + " .codeViewer")
        .html("<h3>This file isn't text file.</h3>")
}

function fillFileTitleTemplate(fileName, changesetId, projectFileId) {
    var title = $("#fileTitleTemplate").render({
        fileName:fileName,
        changesetId:changesetId,
        fileId:projectFileId
    });
    setContentFilesTitle(changesetId, title);
}

function showMessageAboutRemovedFile(changesetId) {
    $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
    $("#content-files-" + changesetId + " .codeViewer")
        .html("<h3>This file was removed.</h3>")
}

function attachLineCommentPopover(changesetId, projectFileId) {
    $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
        $(element).click(function () {
            $('#content-files-' + changesetId + ' .linenums li').popover("hide");
            $(element).popover("show");
        });
        //TODO check if creating the content of the popover (i.e. commentForm) can be deferred to popover activation
        var commentForm = $("#addLineCommentFormTemplate").render({fileId:projectFileId, changesetId:changesetId, lineNumber:i + 1 });
        $(element).popover({content:commentForm, placement:"left", trigger:"manual" });
    });
}

function renderContentFileWithSyntaxHighlighter(changesetId, file, projectFileId) {
    $.SyntaxHighlighter.init({lineNumbers:true});
    $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
    $("#content-files-" + changesetId + " .codeViewer")
        .text(file.content)
        .addClass("language-" + file.filetype)
        .syntaxHighlight();
    attachLineCommentPopover(changesetId, projectFileId);
}
