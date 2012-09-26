function showFile(dataset) {
    var changesetIdentifier = dataset.changeset_id;
    var projectFileId = dataset.file_id;
    var changeType = dataset.file_change_type;
    var textFormat = dataset.text_format;
    var fileNameSlice = dataset.file_name_slice;

    var fileListingsUrl = uri.projectFile.getFileListings + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    });

    var fileListing = $(
        '.changeset[data-identifier=' + changesetIdentifier + '] .fileListings' +
        ' .fileListing.projectFile[data-id=' + projectFileId + ']'
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

function hideFileAndScrollToChangesetTop(changesetId, projectFileId) {
    hideDisplayedFile(changesetId, projectFileId);
    var changesetDetails = $('.changeset[data-identifier=' + changesetId + '] .details');
    changesetDetails.parents('.changeset').ScrollTo({
        offsetTop:codeReview.navbarOffset
    });
    return false;
}

function hideDisplayedFile(changesetId, projectFileId) {
    var selector = '.changeset[data-identifier=' + changesetId + ']' +
        ' .fileListings .fileListing.projectFile[data-id=' + projectFileId + ']';
    var $fileListing = $(selector);
    $fileListing.hide();
    $.observable(codeReview.getModel(selector)).setProperty('isDisplayed', false)
    removeLineCommentPopover($fileListing)
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
