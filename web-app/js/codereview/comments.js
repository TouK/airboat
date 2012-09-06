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
                $('.addLongCommentMessageToChangeset').html("");
            }
            else {
                $('.addLongCommentMessageToChangeset')
                    .html($('#longCommentTemplate').render(" Your comment is too long!"))
                    .hide().fadeIn();
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
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "blank") {
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render("Comment can't be empty"))
                    .hide().fadeIn();
            }
        },
        "json"
    );
}

function addReply(fileId, changesetId, lineNumber) {
    var text = $("#add-reply-" + fileId + "-" + lineNumber).val();

    $.post(uri.lineComment.addComment,
        { text:text, lineNumber:lineNumber, fileId:fileId},
        function (commentGroupsWithSnippetsForCommentedFile) {
            if (commentGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetId, fileId);
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('#reply-info-' + fileId + '-' + lineNumber)
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "blank") {
                $('#reply-info-' + fileId + '-' + lineNumber)
                    .html($('#longCommentTemplate')
                    .render("Comment can't be empty!"))
                    .hide().fadeIn();
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

function expandReplyForm(fileId, lineNumber) {
    $('#replyFormButtons-' + fileId + '-' + lineNumber).slideDown(100);
    $("#add-reply-" + fileId + "-" + lineNumber).attr('rows', 3);
}

function cancelReply(fileId, lineNumber) {
    $('#replyFormButtons-' + fileId + '-' + lineNumber).hide();
    $("#add-reply-" + fileId + "-" + lineNumber).attr('rows', 1);
    $("#add-reply-" + fileId + "-" + lineNumber).val("");

}
function resetCommentForm(changesetId) {
    $('#add-comment-' + changesetId).val("");
    $('#add-comment-' + changesetId).attr('rows', 1);
    $('.longComment').remove();
    $('#commentFormButtons-' + changesetId).hide();
}

function showCommentsToChangeset(changeset) {
    $('#comments-' + changeset.identifier).html("");
    for (var i = 0; i < changeset.commentsToChangeset.length; i++) {
        var comment = $("#commentTemplate").render(changeset.commentsToChangeset[i]);
        $('#comments-' + changeset.identifier).append(comment);
    }
}
