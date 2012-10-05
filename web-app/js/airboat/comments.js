function addComment($form) {
    var text = $form.find('textarea').val();
    var changeset = airboat.parentModel($form, '.changeset');

    $.post(uri.userComment.addComment,
        { changesetIdentifier:changeset.identifier, text:text },
        function (comment) {
            if (comment.errors) {
                renderCommentErrors(threadGroupsWithSnippetsForCommentedFile.errors, $form);
            } else {
                changeset.addComment(comment);
                resetCommentForm($form);
            }
        },
        "json"
    );
}

function addReply($form) {
    var text = $form.find('textarea').val();
    var changeset = airboat.parentModel($form, '.changeset');
    var projectFile = airboat.parentModel($form, '.projectFile');
    var thread = airboat.parentModel($form, '.thread');

    $.post(uri.lineComment.addReply,
        { threadId: thread.id, text:text, changesetIdentifier: changeset.identifier, projectFileId: projectFile.id},
        function (comment) {
            if (comment.errors) {
                renderCommentErrors(threadPositions.errors, $form);
            } else {
                thread.addComment(comment);
            }
        },
        "json"
    );

}

function checkCanAddLineCommentAndShowForm($listingLine, projectFile) {
    var url = uri.lineComment.checkCanAddComment + '?' + $.param({
        changesetIdentifier:projectFile.changeset.identifier, projectFileId:projectFile.id
    });
    $.ajax({url:url}).done(function (response) {
        if (response.canAddComment) {
            createAndShowCommentFormPopover($listingLine, projectFile);
        } else {
            var cannotAddCommentMessage = $('#cannotAddLineCommentMessageTepmlate').render(response);
            $.colorbox({html:cannotAddCommentMessage});
        }
    })
}

function createAndShowCommentFormPopover($listingLine, projectFile) {
    var commentForm = $("#addLineCommentFormTemplate").render({
        changesetId:projectFile.changeset.identifier,
        fileId:projectFile.id,
        lineNumber:getLineNumber($listingLine)
    });
    removeLineCommentPopover($listingLine.parents('.fileListing'));

    $listingLine.parents('.fileListing').first().data('$commentedLine', $listingLine);
    $listingLine.popover({
        content:commentForm,
        placement:"left",
        trigger:"click",
        template:'<div class="popover lineCommentFormPopover"><div class="arrow"></div><div class="popover-inner"><div class="popover-content"><p></p></div></div></div>'
    });
    $listingLine.popover('show');
}

function getLineNumber($listingLine) {
    var diffSpanStartLine = airboat.getModel($listingLine[0]).newFileStartLine;
    var lineIndex = $listingLine.parents('pre').find('li').index($listingLine);
    return diffSpanStartLine + lineIndex;
}

function closeLineCommentForm(changesetIdentifier, projectFileId) {
    var $fileListing = $(
        '.changeset[data-identifier=' + changesetIdentifier + ']' +
        ' .fileListing.projectFile[data-id=' + projectFileId + ']'
    );
    removeLineCommentPopover($fileListing);
}

function addLineComment(changesetIdentifier, projectFileId, lineNumber) {
    var textarea = $('#add-line-comment-' + projectFileId);
    var text = textarea.val();
    var $form = textarea.parents('form')[0];
    var projectFile = airboat.getModel('.changeset[data-identifier=' + changesetIdentifier + '] .projectFile[data-id=' + projectFileId + ']');

    $.post(uri.lineComment.addComment,
        { changesetIdentifier: changesetIdentifier, projectFileId:projectFileId, text:text, lineNumber:lineNumber},
        function (threadPositions) {
            if (threadPositions.errors) {
                renderCommentErrors(threadPositions.errors, $form);
            } else {
                projectFile.updateCommentThreads(threadPositions);
                closeLineCommentForm(changesetIdentifier, projectFileId);
            }
        },
        "json"
    );
}

function removeLineCommentPopover($fileListings) {
    $fileListings.each(function () {
        var commentedLine = $(this).data('$commentedLine');
        if (commentedLine) {
            commentedLine.popover('destroy');
        }
    });
}

function renderCommentErrors(error, $form) {
    var $validationErrors = $form.find('.validationErrors');
    if (error.code == "maxSize.exceeded") {
        $validationErrors
            .html($('#errorCommentTemplate')
            .render(" Your comment is too long."))
            .hide().fadeIn();
    }
    else if (error.code == "blank") {
        $validationErrors
            .html($('#errorCommentTemplate')
            .render("Comment can't be empty."))
            .hide().fadeIn();
    }
}

function expandCommentForm($form) {
    $form.find('.buttons').slideDown(100);
    $form.find('textarea').attr('rows', 3);
}

function resetCommentForm($form) {
    $form.find('textarea').val('').attr('rows', 1);
    $form.find('.buttons').hide();
    $form.find('.validationErrorsToChangeset').html("").hide();
}
