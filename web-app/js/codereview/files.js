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
