var projectName = ''

function showProject(projectId) {
    projectName = projectId
    $('#content').html("");
    $.getJSON(uri.changeset.getLastChangesets + '?' + $.param({projectName:projectName}), appendChangesetsBottom)
}

function onScrollThroughBottomAttempt() {
    loadMoreChangesets();
}

function loadMoreChangesets() {
    if (!changesetsLoading) {
        changesetsLoading = true;
        var controllerAction;
        if (projectName == '') {
            controllerAction = uri.changeset.getNextFewChangesetsOlderThan
        } else {
            controllerAction = uri.changeset.getNextFewChangesetsOlderThanFromSameProject
        }
        $.getJSON(controllerAction + '?' + $.param({changesetId:lastLoadedChangesetId}), appendChangesetsBottom)
    }
}

var lastLoadedChangesetId;
var changesetsLoading;

function appendChangesetsTop(changestets) {
    //TODO when there will be needed (when new changsets will be pushed from server to application)
}

function appendChangesetsBottom(changesetsByDay) {
    for (day in changesetsByDay) {
        //find or create day container
        var dayElement = getDayContainer(day);
        if (dayElement.length == 0) {
            //create new day element
            $('#content').append($("#dayTemplate").render({date:day}));
        }
        dayElement = getDayContainer(day);
        var changesetsForDay = changesetsByDay[day];
        for (i = 0; i < changesetsForDay.length; i++) {
            appendChangeset(changesetsForDay[i], dayElement);
            lastLoadedChangesetId = changesetsForDay[i].id;
        }
    }
    changesetsLoading = false;
}

function getDayContainer(date) {
    return $(".day[data-date=" + date + "]");
}

function appendChangeset(changeset, dayElement) {
    var shortIdentifier = changeset.identifier.substr(0, hashAbbreviationLength) + "...";
    changeset = $.extend({shortIdentifier:shortIdentifier}, changeset)
    dayElement.children('.changesets').append($("#changesetTemplate").render(changeset));
    var changesetElement = $(".changeset[data-identifier=" + changeset.identifier + "]");
    showCommentsToChangeset(changeset);
    $('#comment-form-' + changeset.identifier).append($("#commentFormTemplate").render({identifier:changeset.identifier}));
    appendAccordion(changeset);

    changesetElement.find('.changeset-date').tooltip({title:changeset.date, trigger:"hover", placement:"bottom"});
    changesetElement.find('.changeset-hash').tooltip({title:"click to copy", trigger:"hover", placement:"bottom"});
    changesetElement.find('.changeset-hash').zclip({
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

function appendAccordion(changeset) {
    for (var i = 0; i < changeset.changesetFiles.length; i++) {
        var projectFile = changeset.changesetFiles[i];
        var accordionRow = $("#accordionFilesTemplate").render({
            name:sliceName(projectFile.name, lineBoundary),
            changesetId:changeset.identifier,
            fileId:projectFile.id,
            textFormat:projectFile.textFormat,
            collapseId:(changeset.identifier + projectFile.id),
            howManyComments:projectFile.commentCount,
            fileChangeType:projectFile.changeType.name,
            textForChangeType:textForChangeType,
            iconForChangeType:iconForChangeType
        });
        $('#accordion-' + changeset.identifier).append(accordionRow);
    }

    $('#accordion-' + changeset.identifier + ' .accordion-body.collapse').on('show', function() {
        appendSnippetToFileInAccordion(this.dataset.changeset_id, this.dataset.file_id)
        showFile(this.dataset);
    }).on('shown', function () {
        $(this).parents('.changeset').ScrollTo({offsetTop:codeReview.initialFirstChangesetOffset});
    });
}

function updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetId, projectFileId) {
    renderCommentGroupsWithSnippets(projectFileId, changesetId, commentGroupsWithSnippetsForCommentedFile);
    $('#collapse-inner-' + changesetId + projectFileId).removeAttr('style');
    $('#accordion-group-' + changesetId + projectFileId + ' .commentsCount')
        .text(commentGroupsWithSnippetsForCommentedFile.commentsCount)
}

function appendSnippetToFileInAccordion(changesetIdentifier, projectFileId) {
    $.getJSON(uri.projectFile.getLineCommentsWithSnippetsToFile + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    }),
        function (commentGroupsWithSnippetsForFile) {
            renderCommentGroupsWithSnippets(projectFileId, changesetIdentifier, commentGroupsWithSnippetsForFile);
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
    else {
        $('#fileComments-' + fileId).html("<h5>This file has no comments.</h5>");
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

function hideChangesetDetails(identifier) {
    hideFile(identifier);
    $('#changesetDetails-' + identifier).hide(50);
    $('#more-button-' + identifier).show();
}

function hideChangesetDetailsAndScroll(identifier) {
    $('#accordion-' + identifier + ' .accordion-body.collapse').parents('.changeset').ScrollTo({
        onlyIfOutside:true,
        offsetTop:codeReview.initialFirstChangesetOffset,
        callback:function() {
            hideChangesetDetails(identifier)
        }
    });
}



