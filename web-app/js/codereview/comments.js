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
            else {
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            }
        },
        "json"
    );
}

function addReply(fileId, changesetId, lineNumber){
    var text = $("#add-reply-" + fileId + "-" + lineNumber).val();

    $.post(uri.lineComment.addComment,
        { text:text, lineNumber:lineNumber, fileId:fileId},
        function (commentGroupsWithSnippetsForCommentedFile) {
            if (commentGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetId, fileId);
            }
            else {
                $('#div-comments-'+ fileId+'-'+lineNumber +' .addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
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
    $('#replyFormButtons-' + fileId +'-' + lineNumber).slideDown(100);
    $("#add-reply-" + fileId + "-" + lineNumber).attr('rows', 3);
}

function cancelReply(fileId,lineNumber) {
    $("#add-reply-" + fileId + "-" + lineNumber).text("");
    $('#replyFormButtons-' + fileId +'-' + lineNumber).hide();
    $("#add-reply-" + fileId + "-" + lineNumber).text("");
    $("#add-reply-" + fileId + "-" + lineNumber).attr('rows', 1);

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
