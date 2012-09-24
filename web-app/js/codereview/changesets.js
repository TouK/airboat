function showProject(projectName) {
    $.observable(codeReview).setProperty('displayedProjectName', projectName);
    changesetsLoading = true;
    $.getJSON(uri.changeset.getLastChangesets + '?' + $.param({projectName:codeReview.displayedProjectName}),
        function(data) {clearDisplayAndAppendChangesetsBottom({changesets: data, shouldLoad: true, viewType: VIEW_TYPE.PROJECT, activeSelector: '#projectsDropdown'})});
}

function showFiltered(filterType) {
    $.observable(codeReview).setProperty('currentFilter', filterType);
    changesetLoading = true;
    $.getJSON(uri.changeset.getLastFilteredChangesets + '?' + $.param({filterType:codeReview.currentFilter}),
        function(data) {clearDisplayAndAppendChangesetsBottom({changesets: data, shouldLoad: true, viewType: VIEW_TYPE.FILTER, activeSelector: '#filtersDropdown'})});
}

function clearDisplayAndAppendChangesetsBottom(dataset) {
    shouldLoadChangesets = dataset.shouldLoad;
    currentViewType = dataset.viewType;
    $('#content').html("");
    setActive(dataset.activeSelector)
    appendChangesetsBottom(dataset.changesets);
}

function setActive(selector) {
    setAllInactive();
    $(selector + ' .dropdown-toggle').css('text-decoration', 'underline');
}

function setAllInactive() {
    $('.navbarToggle .dropdown-toggle').css('text-decoration', 'none');
}

function onScrollThroughBottomAttempt() {
    loadMoreChangesets();
}

function loadMoreChangesets() {
    if (!changesetsLoading && shouldLoadChangesets) {
        changesetsLoading = true;
        var controllerAction;
        var paramsMap = {changesetId:lastLoadedChangesetId};
        if (history.state.dataType == DATA_TYPE.PROJECT && codeReview.displayedProjectName == '') {
            controllerAction = uri.changeset.getNextFewChangesetsOlderThan;
        } else if (history.state.dataType == DATA_TYPE.PROJECT) {
            controllerAction = uri.changeset.getNextFewChangesetsOlderThanFromSameProject;
        } else if (history.state.dataType == DATA_TYPE.FILTER) {
            controllerAction = uri.changeset.getNextFewFilteredChangesetsOlderThan;
            paramsMap['filterType'] = codeReview.currentFilter;
        }
        $.getJSON(controllerAction + '?' + $.param(paramsMap), appendChangesetsBottom);
    }
}

var VIEW_TYPE = { SINGLE_CHANGESET:'changeset', PROJECT:'project', FILTER:'filter'};
var DATA_TYPE = { CHANGESET:'changeset', PROJECT:'project', FILTER:'filter'};

var lastLoadedChangesetId;
var changesetsLoading;
var shouldLoadChangesets;
var currentViewType;

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

$('.changeset-hash').livequery(function () {
    $(this)
        .hover(function () {
            $('.clippy-' + this.dataset.changeset_identifier)
                .clippy({
                    clippy_path:uri.libs.clippy.swf
                });
            showClippyAndTooltip.call(this);
        }, function () {
            var that = this
            setTimeout(function () {
                hideSpanForClippy(that);
                removeClippyObject(that);
            }, 5000)
        });
})

function hideSpanForClippy(that) {
    $('.hashForClippy-' + that.dataset.changeset_identifier).hide()
}

function showClippyAndTooltip() {
    $('.hashForClippy-' + this.dataset.changeset_identifier)
        .tooltip({title:"click to copy", trigger:"hover", placement:"bottom"})
        .show();
}

function removeClippyObject(that) {
    swfobject.removeSWF(that.dataset.changeset_identifier);
}

$('.changeset-date').livequery(function () {
    $(this).tooltip({title:this.dataset.date, trigger:"hover", placement:"bottom"});
})

function appendChangeset(changeset, dayElement) {

    changeset['shortIdentifier'] = changeset.identifier.substr(0, hashAbbreviationLength) + "...";
    changeset['allComments'] = function () {
        var projectFilesComments = 0
        $(this.projectFiles).each(function () {
            projectFilesComments += this.commentsCount
        })
        return this.comments.length + projectFilesComments
    }

    $(changeset.projectFiles).each(function () {
        $.extend(this, {
            changeset:changeset,
            collapseId:(changeset.identifier + this.id),
            name:sliceName(this.name),
            isDisplayed:false
        })
    })

    dayElement.children('.changesets').append($("<span id='templatePlaceholder'></span>"));
    $.link.changesetTemplate('#templatePlaceholder', changeset, {target:'replace'})

    $('#comment-form-' + changeset.identifier).append($("#commentFormTemplate").render(changeset));
}

/*TODO move it somewhere near the template definition*/
$('.accordion-body.collapse').livequery(function () {
    $(this)
        .on('show',function () {
            appendSnippetToFileInAccordion(this.dataset.changeset_id, this.dataset.file_id)
            showFile(this.dataset);
        }).on('shown', function () {
            $.observable(codeReview.getModel(this)).setProperty('isDisplayed', true)
        });
})

function updateAccordion(commentGroupsWithSnippetsForCommentedFile, changesetIdentifier, projectFileId) {
    renderCommentGroupsWithSnippets(changesetIdentifier, projectFileId, commentGroupsWithSnippetsForCommentedFile);
    var projectFile = codeReview.getModel('.changeset[data-identifier=' + changesetIdentifier + '] .projectFile[data-id=' + projectFileId + ']');
    $.observable(projectFile).setProperty('commentsCount', commentGroupsWithSnippetsForCommentedFile.commentsCount)
    $.observable(codeReview.getModel('.changeset[data-identifier=' + changesetIdentifier + ']')).setProperty('allComments')
}

function appendSnippetToFileInAccordion(changesetIdentifier, projectFileId) {
    $.getJSON(uri.projectFile.getLineCommentsWithSnippetsToFile + '?' + $.param({
        changesetIdentifier:changesetIdentifier, projectFileId:projectFileId
    }),
        function (commentGroupsWithSnippetsForFile) {
            renderCommentGroupsWithSnippets(changesetIdentifier, projectFileId, commentGroupsWithSnippetsForFile);
            $('#collapse-inner-' + changesetIdentifier + projectFileId).collapse('reset')
        }
    );
}

function renderCommentGroupsWithSnippets(changesetIdentifier, projectFileId, commentGroupsWithSnippetsForFile) {
    var fileType = commentGroupsWithSnippetsForFile.fileType;
    var commentGroupsWithSnippets = commentGroupsWithSnippetsForFile.commentGroupsWithSnippets;

    if (commentGroupsWithSnippets.length > 0) {
        $('#fileComments-' + changesetIdentifier + projectFileId).html("");

        for (j = 0; j < commentGroupsWithSnippets.length; j++) {
            renderCommentGroupWithSnippets(changesetIdentifier, projectFileId, commentGroupsWithSnippets[j], fileType);
        }
    }
    else {
        $('#fileComments-' + changesetIdentifier + projectFileId).html("<h5>This file has no comments.</h5>");
    }
}

function renderCommentGroupWithSnippets(changesetIdentifier, projectFileId, commentGroupWithSnippet, fileType) {
    var lineNumber = commentGroupWithSnippet.commentGroup[0].lineNumber;

    var snippet = $("#snippetTemplate").render({
        fileId:projectFileId,
        lineNumber:lineNumber,
        changesetId:changesetIdentifier
    });

    $('#fileComments-' + changesetIdentifier + projectFileId).append(snippet);

    $("#snippet-" + projectFileId + "-" + lineNumber)
        .html("<pre class='codeViewer'/></pre>")
        .children(".codeViewer")
        .text(commentGroupWithSnippet.snippet)
        .addClass("linenums:" + lineNumber)
        .addClass("language-" + fileType)
        .syntaxHighlight();

    renderCommentGroup(changesetIdentifier, projectFileId, commentGroupWithSnippet.commentGroup, lineNumber);
}

function renderCommentGroup(changesetIdentifier, projectFileId, commentGroup, lineNumber) {
    for (var k = 0; k < commentGroup.length; k++) {
        var comment = $("#commentTemplate").render(commentGroup[k]);
        $('#div-comments-' + changesetIdentifier + projectFileId + "-" + lineNumber).append(comment);
    }
}


function sliceName(name) {
    return name.toString().replace(/\//g, '/&#8203;');
}

function toggleChangesetDetails(identifier) {
    var changesetDetails = $('#changesetDetails-' + identifier);
    if (changesetDetails.is(':visible')) {
        changesetDetails.parents('.changeset').ScrollTo({
            offsetTop:codeReview.navbarOffset
        });
        changesetDetails.slideUp('slow', function () {
            hideFileAndScrollToChangesetTop(identifier)
        })
    } else {
        changesetDetails.slideDown();
    }
}

