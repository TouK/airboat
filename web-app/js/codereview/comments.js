function addComment(changesetId) {

    var text = $('#add-comment-' + changesetId).val();
    if (text == "") {
        return false
    }

    $.post(uri.userComment.addComment,
        { changesetId:changesetId, text:text },
        function (comment) {
            $('#comments-' + changesetId).append($('#commentTemplate').render(comment));
        },
        "json");

    changeAddCommentDivToDefault(changesetId);
    hideAddCommentButtons(changesetId);
}

function cancelComment(changesetId) {
    changeAddCommentDivToDefault(changesetId);
    hideAddCommentButtons(changesetId);
}

function cancelLineComment(fileIdentifier, changesetId, lineNumber) {
    $('#add-line-comment-' + fileIdentifier).val("");
    $('#author-' + fileIdentifier).val("");
    hidePopovers(changesetId);
}

function addLineComment(fileIdentifier, changesetId, lineNumber) {
    var text = $('#add-line-comment-' + fileIdentifier).val();

    $.post(uri.lineComment.addComment,
        { text:text, lineNumber:lineNumber, fileId:fileIdentifier}
    ).done(function () {
            updateAccordion(changesetId, fileIdentifier);
            hideAndClearLineCommentFrom(changesetId, fileIdentifier);
        })
}

function hideAndClearLineCommentFrom(changesetId, fileIdentifier) {
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
    $('#add-line-comment-' + fileIdentifier).val("");
    $('#author-' + fileIdentifier).val("");
}

function hidePopovers(changesetId) {
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
}

function hideAddCommentButtons(changesetId) {
    $('#btn-' + changesetId).hide();
    $('#c-btn-' + changesetId).hide();
}

function changeAddCommentDivToDefault(changesetId) {
    $('#add-comment-' + changesetId).val("");
    $('#username-' + changesetId).val("");
    $('#add-comment-' + changesetId).removeClass('span12')
}

function showCommentsToChangeset(id) {
    $('#comments-' + id).html("");
    var fileUrl = uri.userComment.returnCommentsToChangeset
    fileUrl += id;
    $.getJSON(fileUrl, function (data) {
        for (i = 0; i < data.length; i++) {
            var comment = $("#commentTemplate").render(data[i]);
            $('#comments-' + id).append(comment);
        }
    });
}
