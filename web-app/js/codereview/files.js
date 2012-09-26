function showFile(dataset) {
    var changesetIdentifier = dataset.changeset_id;
    var projectFileId = dataset.file_id;
    var changeType = dataset.file_change_type;
    var textFormat = dataset.text_format;
    var fileNameSlice = dataset.file_name_slice;

    hideDisplayedFile(changesetIdentifier)

    var fileListingsUrl = uri.projectFile.getFileListings + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    });

    var fileListing = $(
        '.changeset[data-identifier=' + changesetIdentifier + '] .fileListings' +
        ' .fileListing[data-project-file-id=' + projectFileId + ']'
    );

    $.getJSON(fileListingsUrl, function (listings) {
        listings.diffHunks = extractHunks(listings.diff)
        listings.wholeFileHunks = addAdditionalContexts(listings.diffHunks, listings.fileContent)
        listings.showWholeFile = false;

        daHunks = listings.diffHunks

        fileListing.children('.diffAndFileListing').html('<div id="templatePlaceholder"></div>')
        $.link.diffAndFileListingTemplate('#templatePlaceholder', listings, {target: 'replace'})
        fileListing.show();
    });
}

function toBoolean(toConvert) {
    return JSON.parse(toConvert);
}

function hideFileAndScrollToChangesetTop(changesetId) {
    hideDisplayedFile(changesetId);
    var changesetDetails = $('.changeset[data-identifier=' + changesetId + '] .details');
    changesetDetails.parents('.changeset').ScrollTo({
        offsetTop:codeReview.navbarOffset
    });
    return false
}

function hideDisplayedFile(changesetId) {
    var $fileListings = $('.changeset[data-identifier=' + changesetId + '] .fileListings .fileListing');
    $fileListings.hide();
    $(codeReview.getModel('.changeset[data-identifier=' + changesetId + ']').projectFiles).each(function () {
        $.observable(this).setProperty('isDisplayed', false)
    })
    removeLineCommentPopovers($fileListings)
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