var previousExpandedForFilesChangesetId; //FIXME remove - this does not belong here, it's here for popovers setup...

function showFile(dataset) {
    var changesetIdentifier = dataset.changeset_id;
    var projectFileId = dataset.file_id;
    var changeType = dataset.file_change_type;
    var textFormat = dataset.text_format;
    var fileNameSlice = dataset.file_name_slice;

    appendDiff(changesetIdentifier, projectFileId);

    var fileContentUrl = uri.projectFile.getFileWithContent + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    });
    if (changeType != 'DELETE') {
        if (toBoolean(textFormat)) {
            $.getJSON(fileContentUrl, function (file) {
                renderContentFileWithSyntaxHighlighter(changesetIdentifier, file, projectFileId);
                showFilesContent(changesetIdentifier);
            });
        }
        else {
            showMessageAboutNonTextFile(changesetIdentifier);
            showFilesContent(changesetIdentifier);
        }
    }
    else {
        cleanPreviousFilesContent(changesetIdentifier);
        showMessageAboutRemovedFile(changesetIdentifier);
        showFilesContent(changesetIdentifier);
    }

    if (previousExpandedForFilesChangesetId != null) {
        hidePopovers(previousExpandedForFilesChangesetId);
    }
    previousExpandedForFilesChangesetId = changesetIdentifier;
}

function toBoolean(toConvert) {
    return JSON.parse(toConvert);
}


function showFilesContent(changesetId) {
    $('.changeset[data-identifier=' + changesetId + '] .fileListings .fileListing').show();
}

function hideFile(changesetId) {
    $('.changeset[data-identifier=' + changesetId + '] .fileListings .fileListing').hide();
    hidePopovers(changesetId);
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

function cleanPreviousFilesContent(changesetId) {
    $("#content-files-" + changesetId).html("");
}

function showMessageAboutNonTextFile(changesetId) {
    $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
    $("#content-files-" + changesetId + " .codeViewer")
        .html("<h3>This file isn't text file.</h3>")
}

function showMessageAboutRemovedFile(changesetId) {
    $("#content-files-" + changesetId).html("<pre class='codeViewer'/>");
    $("#content-files-" + changesetId + " .codeViewer")
        .html("<h3>This file was removed.</h3>")
}

function attachLineCommentPopover(changesetId, projectFileId) {
    $('#content-files-' + changesetId + ' .linenums li').each(function (i, element, ignored) {
        var commentForm = $("#addLineCommentFormTemplate").render({fileId:projectFileId, changesetId:changesetId, lineNumber:i + 1 });
        $(element).popover({content:commentForm, placement:"left", trigger:"manual" });

        $(element).click(function () {
            var url = uri.lineComment.checkCanAddComment + '?' + $.param({
                changesetIdentifier: changesetId,  projectFileId: projectFileId
            });
            $.ajax({url: url}).done(function (response) {
                if (response.canAddComment) {
                    $('#content-files-' + changesetId + ' .linenums li').popover('hide');
                    $(element).popover('show')
                } else {
                    var cannotAddCommentMessage = $('#cannotAddLineCommentMessageTepmlate').render(response);
                    $.colorbox({html: cannotAddCommentMessage});
                }
            })
        });
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
