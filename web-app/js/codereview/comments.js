function addComment(changesetId, changesetIdentifier) {

    var text = $('#add-comment-' + changesetIdentifier).val();
    if (text == "") {
        return false
    }

    $.post(uri.userComment.addComment,
        { changesetIdentifier:changesetIdentifier, text:text },
        function (comment) {
            if (comment.errors == null) {
                var changeset = codeReview.getModel('.changeset[data-id=' + changesetId + ']');
                var changesetComments = changeset.comments
                $.observable(changesetComments).insert(changesetComments.length, comment)
                $.observable(changeset).setProperty('allComments')

                resetCommentForm(changesetIdentifier);
                $('.addLongCommentMessageToChangeset').html("");
            } else {
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

function addLineComment(changesetIdentifier, projectFileId, lineNumber) {
    var text = $('#add-line-comment-' + projectFileId).val();

    $.post(uri.lineComment.addComment,
        { changesetIdentifier: changesetIdentifier, projectFileId:projectFileId, text:text, lineNumber:lineNumber},
        function (commentGroupsWithSnippetsForCommentedFile) {
            if (commentGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetIdentifier, projectFileId);
                hideAndClearLineCommentFrom(changesetIdentifier, projectFileId);
            } else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            } else if(commentGroupsWithSnippetsForCommentedFile.errors.code == "blank"){
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render("Comment can't be empty"))
                    .hide().fadeIn();
            }
        },
        "json"
    );
}

function addReply(changesetIdentifier, projectFileId, lineNumber){
    var text = $("#add-reply-" + projectFileId + "-" + lineNumber).val();

    $.post(uri.lineComment.addReply,
        { changesetIdentifier: changesetIdentifier, projectFileId:projectFileId, text:text, lineNumber:lineNumber},
        function (commentGroupsWithSnippetsForCommentedFile) {
            if (commentGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetIdentifier, projectFileId);
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('#reply-info-'+ projectFileId+'-'+lineNumber)
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            }
            else if (commentGroupsWithSnippetsForCommentedFile.errors.code == "blank") {
                $('#reply-info-'+ projectFileId+'-'+lineNumber)
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
