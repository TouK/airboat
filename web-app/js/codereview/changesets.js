var projectName = ''

function showProject(projectId) {
    projectName = projectId
    $('#content').html("");
    $.getJSON(uri.changeset.getLastChangesets + '?' + $.param({projectName:projectName}), appendChangesets)
}

function onScrollThroughBottomAttempt() {
    loadMoreChangesets();
}

function loadMoreChangesets() {
    if (!changesetsLoading) {
        changesetsLoading = true;
        $.getJSON(uri.changeset.getNextFewChangesetsOlderThan + '?' + $.param({projectName:projectName, changesetId:lastChangesetId}), appendChangesets)
    }
}

var lastChangesetId;
var changesetsLoading;

function appendChangesets(changesets) {
    if (changesets.length > 0) {
        lastChangesetId = $(changesets).last()[0].identifier //TODO find a better way
        for (i = 0; i < changesets.length; i++) {
            appendChangeset(changesets[i]);
        }
    }
    changesetsLoading = false;
}

function appendChangeset(changeset) {
    var shortIdentifier = changeset.identifier.substr(0, hashAbbreviationLength) + "...";
    changeset = $.extend({shortIdentifier:shortIdentifier}, changeset)
    $('#content').append($("#changesetTemplate").render(changeset));
    showCommentsToChangeset(changeset.identifier);
    $('#comment-form-' + changeset.identifier).append($("#commentFormTemplate").render({identifier:changeset.identifier}));
    appendAccordion(changeset.identifier, null);

    $('#hash-' + changeset.identifier).tooltip({title:changeset.identifier + ", click to copy", trigger:"hover"});
    $('#hash-' + changeset.identifier).zclip({
        path:uri.libs.zclip.swf,
        copy:changeset.identifier,
        afterCopy:function () {
        }
    });
}

iconForChangeType = {
    ADD:'icon-plus',
    DELETE:'icon-minus',
    MODIFY:'icon-edit',
    RENAME:'icon-pencil',
    COPY:'icon-move'
}

textForChangeType = {
    ADD:'added',
    DELETE:'deleted',
    MODIFY:'modified',
    RENAME:'renamed',
    COPY:'copied'
}

function appendAccordion(changesetId, fileIdentifier) {
    $('#accordion-' + changesetId).html("");

    $.getJSON(uri.changeset.getFileNamesForChangeset + changesetId, function (data) {

        for (i = 0; i < data.length; i++) {
            var accordionRow = $("#accordionFilesTemplate").render({
                name:sliceName(data[i].name, lineBoundary),
                changesetId:changesetId,
                fileId:data[i].id,
                collapseId:(changesetId + data[i].id),
                howManyComments:data[i].lineComments.length,
                fileChangeType:data[i].changeType.name,
                textForChangeType:textForChangeType,
                iconForChangeType:iconForChangeType
            });

            $('#accordion-' + changesetId).append(accordionRow);
            appendSnippetToFileInAccordion(data[i].id, changesetId)
        }

        $('#accordion-' + changesetId + ' .accordion-body.collapse').on('shown', function () {
            $(this).parents('.changeset').ScrollTo({offsetTop:codeReview.initialFirstChangesetOffset});
            showFile(this.dataset.changeset_id, this.dataset.file_id, this.dataset.file_change_type, this.dataset.file_name_slice);
        })
    });
}
function updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetId, projectFileId) {
    renderCommentGroupsWithSnippets(projectFileId, changesetId, commentGroupsWithSnippetsForCommentedFile);
    $('#collapse-inner-' + changesetId + projectFileId).removeAttr('style');
    $('#accordion-group-' + changesetId + projectFileId + ' .commentsCount')
        .text(commentGroupsWithSnippetsForCommentedFile.commentsCount)
}

function appendSnippetToFileInAccordion(projectFileId, changesetId) {
    $.getJSON(uri.projectFile.getLineCommentsWithSnippetsToFile + projectFileId,
        function (commentGroupsWithSnippetsForFile) {
            renderCommentGroupsWithSnippets(projectFileId, changesetId, commentGroupsWithSnippetsForFile);
        }
    );
}

function renderCommentGroupsWithSnippets(fileId, changesetId, commentGroupsWithSnippetsForFile) {
    var fileType = commentGroupsWithSnippetsForFile.fileType;
    var commentGroupsWithSnippets = commentGroupsWithSnippetsForFile.commentGroupsWithSnippets;

    if (commentGroupsWithSnippets.length > 0) {
        $('#fileComments-' + fileId).html("");

        for (j = 0; j < commentGroupsWithSnippets.length; j++) {
            renderCommentGroupWithSnippets(commentGroupsWithSnippets[j], fileId, changesetId, fileType);
        }
    }
}

function renderCommentGroupWithSnippets(commentGroupWithSnippet, fileId, changesetId, fileType) {
    var lineNumber = commentGroupWithSnippet.commentGroup[0].lineNumber;

    var snippet = $("#snippetTemplate").render({
        fileId:fileId,
        lineNumber:lineNumber,
        changesetId:changesetId
    });


    $('#fileComments-' + fileId).append(snippet);

    $("#snippet-" + fileId + "-" + lineNumber)
        .html("<pre class='codeViewer'/></pre>")
        .children(".codeViewer")
        .text(commentGroupWithSnippet.snippet)
        .addClass("linenums:" + lineNumber)
        .addClass("language-" + fileType)
        .syntaxHighlight();

    renderCommentGroup(commentGroupWithSnippet.commentGroup, fileId, lineNumber);
}

function renderCommentGroup(commentGroup, fileId, lineNumber) {
    for (var k = 0; k < commentGroup.length; k++) {
        var comment = $("#commentTemplate").render(commentGroup[k]);
        $('#div-comments-' + fileId + "-" + lineNumber).append(comment);
    }
}

function divideNameWithSlashesInTwo(name) {
    var splitted, newName;
    splitted = name.split("/");
    newName = splitted.slice(0, Math.ceil(splitted.length / 2)).join("/");
    newName += "/ ";
    newName += splitted.slice(Math.ceil(splitted.length / 2), splitted.length).join("/");
    return newName;
}

function sliceName(name, lineWidth) {
    var newName = "";
    var boundary = lineWidth;
    var splitted = name.split("/");
    var i;
    for (i = 0; i < splitted.length; i++) {
        if (newName.length + splitted[i].length >= boundary) {
            boundary += lineWidth;
            newName += " ";
            newName += splitted[i] + "/";
        }
        else {
            newName += splitted[i] + "/";
        }
    }
    return newName.substr(0, newName.length - 1);
}

function showChangesetDetails(identifier) {
    $('#changesetDetails-' + identifier).show(50);
    $("#more-button-" + identifier).hide();
}

function showLessAboutChangeset(identifier) {
    hideFile(identifier);
    $('#changesetDetails-' + identifier).hide(50);
    $('#more-button-' + identifier).show();
}

function showLessAboutChangesetAndScroll(identifier) {
    $('#accordion-' + identifier + ' .accordion-body.collapse').parents('.changeset').ScrollTo({
        onlyIfOutside: true,
        offsetTop:codeReview.initialFirstChangesetOffset,
        callback: function() { showLessAboutChangeset(identifier) }
    });
}



