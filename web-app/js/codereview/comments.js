function addComment(changesetId) {

    var text = $('#add-comment-' + changesetId).val();
    if (text == "") {
        return false
    }

    $.post(uri.userComment.addComment,
        { changesetId:changesetId, text:text },
        function (comment) {
            if (comment.errors == null) {
                $('#comments-' + changesetId).append($('#commentTemplate').render(comment));
                resetCommentForm(changesetId);
                $('.longComment').remove();
            }
            else {
                $('.longComment').remove();
                $('#commentForm-' + changesetId).append($('#longCommentTemplate').render());
            }
        },
        "json"
    );
}

function cancelLineComment(fileIdentifier, changesetId, lineNumber) {
    $('#add-line-comment-' + fileIdentifier).val("");
    hidePopovers(changesetId);
}

function addLineComment(projectFileId, changesetId, lineNumber) {
    var text = $('#add-line-comment-' + projectFileId).val();

    $.post(uri.lineComment.addComment,
        { text:text, lineNumber:lineNumber, fileId:projectFileId},
        function (commentGroupsWithSnippetsForCommentedFile) {
            if (commentGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetId, projectFileId);
                hideAndClearLineCommentFrom(changesetId, projectFileId);
                $('.longComment').remove();
            }
            else {
                $('.longComment').remove();
                $('.addLongCommentMessage').append($('#longCommentTemplate').render());
            }
        },
        "json"
    );
}

function hideAndClearLineCommentFrom(changesetId, fileIdentifier) {
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
    $('#add-line-comment-' + fileIdentifier).val("");
}

function hidePopovers(changesetId) {
    $('#content-files-' + changesetId + ' .linenums li').popover("hide");
}

function expandCommentForm(changesetId) {
    $('#commentFormButtons-' + changesetId).slideDown(100);
    $('#add-comment-' + changesetId).attr('rows', 3);
}

function resetCommentForm(changesetId) {
    $('#add-comment-' + changesetId).val("");
    $('#add-comment-' + changesetId).attr('rows', 1);
    $('.longComment').remove();
    $('#commentFormButtons-' + changesetId).hide();
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
