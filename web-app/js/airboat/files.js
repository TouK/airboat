function showFile(projectFile, callback) {
    var fileListingsUrl = uri.projectFile.getFileListings + '?' + $.param({
        changesetIdentifier: projectFile.changeset.identifier, projectFileId:projectFile.id
    });

    var $fileListing = $(
        '.changeset[data-identifier=' + projectFile.changeset.identifier + '] .fileListings' +
        ' .fileListing.projectFile[data-id=' + projectFile.id + ']'
    );
    $.observable(projectFile).setProperty('isDisplayed', true);


    $.getJSON(fileListingsUrl, function (listings) {
        listings.diffHunks = extractHunks(listings.diff);
        listings.wholeFileHunks = addAdditionalContexts(listings.diffHunks, listings.fileContent);
        listings.showWholeFile = false;

        $fileListing.children('.diffAndFileListing').html('<div id="templatePlaceholder"></div>');
        $.link.diffAndFileListingTemplate('#templatePlaceholder', listings, {target:'replace'});
        $fileListing.slideDown();
        if (callback) {
            callback($fileListing);
        }
    });
}

function hideFileAndScrollToPreviousFileOrChangesetTop(changesetId, projectFileId) {
    var changeset = $('.changeset[data-identifier=' + changesetId + ']');
    var projectFile = changeset.find('.fileListing.projectFile[data-id=' + projectFileId + ']');
    var previousFile = projectFile.prevAll(':visible').first();
    hideFileListings(projectFile, function () {
        if (previousFile.size() != 0) {
            $.scrollTo(previousFile, scrollDuration, {offset: scrollOffset});
        } else {
            $.scrollTo(changeset, scrollDuration, {offset: scrollOffset});
        }
    });
    return false;
}

function hideFileListings($fileListings, callback) {
    $fileListings.each(function () {
        $.observable(airboat.getModel(this)).setProperty('isDisplayed', false);
    });
    removeLineCommentPopover($fileListings);
    $fileListings.slideUp(onNthCall($fileListings.size(), callback));
}

function showComments($projectFile) {
    var projectFile = airboat.getModel($projectFile[0]);
    projectFile.commentsDisplayed = true;
    if (projectFile.commentsCount() != 0) {
        loadCommentThreadsWithSnippets(projectFile);
        $projectFile.find('.details').slideDown();
    }
}

function loadCommentThreadsWithSnippets(projectFile) {
    if (!projectFile.threadPositionsLoaded) {
        $.getJSON(uri.projectFile.getThreadPositionAggregatesForFile + '?' + $.param({
            changesetIdentifier: projectFile.changeset.identifier, projectFileId:projectFile.id
        }),
            function (threadPositions) {
                projectFile.updateCommentThreads(threadPositions);
            }
        );
    }
}

function hideComments($projectFile) {
    var projectFile = airboat.getModel($projectFile[0]);
    projectFile.commentsDisplayed = false;
    $projectFile.find('.details').slideUp();
}
