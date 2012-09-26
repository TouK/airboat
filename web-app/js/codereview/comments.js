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

function closeLineCommentForm(changesetIdentifier, projectFileId) {
    var $fileListing = $(
        '.changeset[data-identifier=' + changesetIdentifier + ']' +
        ' .fileListing[data-project-file-id=' + projectFileId + ']'
    );
    removeLineCommentPopover($fileListing)
}

function addLineComment(changesetIdentifier, projectFileId, lineNumber) {
    var text = $('#add-line-comment-' + projectFileId).val();

    $.post(uri.lineComment.addComment,
        { changesetIdentifier: changesetIdentifier, projectFileId:projectFileId, text:text, lineNumber:lineNumber},
        function (threadGroupsWithSnippetsForCommentedFile) {
            if (threadGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(threadGroupsWithSnippetsForCommentedFile, changesetIdentifier, projectFileId);
                closeLineCommentForm(changesetIdentifier, projectFileId);
            } else if (threadGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            } else if(threadGroupsWithSnippetsForCommentedFile.errors.code == "blank"){
                $('.addLongCommentMessage')
                    .html($('#longCommentTemplate')
                    .render("Comment can't be empty"))
                    .hide().fadeIn();
            }
        },
        "json"
    );
}

function addReply(threadId, changesetIdentifier, projectFileId){
    var text = $('.changeset[data-identifier=' + changesetIdentifier + ']').find('.addThreadReply[data-identifier=' + threadId + ']').val();

    $.post(uri.lineComment.addReply,
        { threadId: threadId, text:text, changesetIdentifier: changesetIdentifier, projectFileId: projectFileId},
        function (threadGroupsWithSnippetsForCommentedFile) {
            if (threadGroupsWithSnippetsForCommentedFile.errors == null) {
                updateAccordion(threadGroupsWithSnippetsForCommentedFile, changesetIdentifier, projectFileId);
            }
            else if (threadGroupsWithSnippetsForCommentedFile.errors.code == "maxSize.exceeded") {
                $('.threadReplyInfo[data-identifier=' + threadId + ']')
                    .html($('#longCommentTemplate')
                    .render(" Your comment is too long!"))
                    .hide().fadeIn();
            }
            else if (threadGroupsWithSnippetsForCommentedFile.errors.code == "blank") {
                $('.threadReplyInfo[data-identifier=' + threadId + ']')
                    .html($('#longCommentTemplate')
                    .render("Comment can't be empty!"))
                    .hide().fadeIn();
            }
        },
        "json"
    );

}

function removeLineCommentPopover($fileListings) {
    $fileListings.find('[class|=language] li').popover('destroy');
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

function expandReplyForm(threadId, changesetId) {
    $('.changeset[data-identifier=' + changesetId + ']').find('.threadReplyFormButtons[data-identifier=' + threadId + ']').slideDown(100);
    $('.changeset[data-identifier=' + changesetId + ']').find('.addThreadReply[data-identifier=' + threadId + ']').attr('rows', 3);
}

function cancelReply(threadId, changesetId) {
    $('.changeset[data-identifier=' + changesetId + ']').find('.threadReplyFormButtons[data-identifier=' + threadId + ']').hide();
    $('.changeset[data-identifier=' + changesetId + ']').find('.addThreadReply[data-identifier=' + threadId + ']').attr('rows', 1);
    $('.changeset[data-identifier=' + changesetId + ']').find('.addThreadReply[data-identifier=' + threadId + ']').val("");
}

