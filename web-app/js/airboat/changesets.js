function showProject(projectName) {
    $.observable(airboat).setProperty('displayedProjectName', projectName);
    changesetsLoading = true;
    $.getJSON(uri.changeset.getLastChangesets + '?' + $.param({projectName:airboat.displayedProjectName}),
        function (data) {
            clearDisplayAndAppendChangesetsBottom({data:data, shouldLoad:true, viewType:VIEW_TYPE.PROJECT, activeSelector:'#projectsDropdown'})
        });
}

function showFiltered(filterType) {
    $.observable(airboat).setProperty('currentFilter', filterType);
    airboat.changesetLoading = true;
    $.getJSON(uri.changeset.getLastFilteredChangesets + '?' + $.param({filterType:airboat.currentFilter}),
        function (data) {
            clearDisplayAndAppendChangesetsBottom({data:data, shouldLoad:true, viewType:VIEW_TYPE.FILTER, activeSelector:'#filtersDropdown'})
        });
}

function clearDisplayAndAppendChangesetsBottom(dataset) {
    shouldLoadChangesets = dataset.shouldLoad;
    currentViewType = dataset.viewType;
    $('#content').html("");
    setFilterActive(dataset.activeSelector);
    appendChangesetsBottom(dataset.data.changesets);
    decideImportInfoAndLoadingState(dataset.data, 21); //as in Constants.FIRST_CHANGESET_LOAD_SIZE
}

function appendNextChangesetsBottom(dataset) {
    appendChangesetsBottom(dataset.changesets);
    decideImportInfoAndLoadingState(dataset, 10); //as in Constants.NEXT_CHANGESET_LOAD_SIZE
}

function decideImportInfoAndLoadingState(data, maxChangesetSize) {
    showImportGritter(data.isImporting);
    if (countChangesets(data.changesets) < maxChangesetSize) {
        $('#content').append($('#noMoreChangesetsTemplate').render());
        shouldLoadChangesets = false;
    }
}

var importGritter;
function showImportGritter(isImporting) {
    var importInfo;
    var shouldHide = false;
    if (currentViewType == VIEW_TYPE.PROJECT && airboat.displayedProjectName != '') {
        importInfo = 'Import is in progress, older changesets may not by imported yet.';
        shouldHide = true;
    } else {
        importInfo = 'Import is in progress, some changesets may not be displayed. To see all changesets wait' +
            ' a while and refresh page or go into single project view.'
    }
    if (importGritter != null && (shouldHide || isImporting)) {
        $.gritter.remove(importGritter, {fade:false});
    }
    if (isImporting) {
        importGritter = $.gritter.add({
            title:'Import in progress',
            text:importInfo,
            sticky:true
        });
    }
}

function countChangesets(changesetsByDay) {
    var counter = 0;
    for (var day in changesetsByDay) {
        counter += changesetsByDay[day].length;
    }
    return counter;
}

function setFilterActive(selector) {
    setAllFiltersInactive();
    $(selector + ' .dropdown-toggle').css('text-decoration', 'underline');
}

function setAllFiltersInactive() {
    $('.navbarToggle .dropdown-toggle').css('text-decoration', 'none');
}

function onScrollThroughBottomAttempt() {
    loadMoreChangesets();
}

function loadMoreChangesets() {
    if (!changesetsLoading && shouldLoadChangesets) {
        changesetsLoading = true;
        var controllerAction = getControllerAction();
        $.getJSON(controllerAction, function (data) {
            appendNextChangesetsBottom(data)
        });
    }
}

function getControllerAction() {
    if (history.state.dataType == DATA_TYPE.PROJECT && airboat.displayedProjectName == '') {
        return uri.changeset.getNextFewChangesetsOlderThan + '?' + $.param({changesetId:lastLoadedChangesetId});
    } else if (history.state.dataType == DATA_TYPE.PROJECT) {
        return uri.changeset.getNextFewChangesetsOlderThanFromSameProject  + '?' + $.param({changesetId:lastLoadedChangesetId});
    } else if (history.state.dataType == DATA_TYPE.FILTER) {
        return uri.changeset.getNextFewFilteredChangesetsOlderThan  + '?' + $.param({changesetId:lastLoadedChangesetId, filterType: airboat.currentFilter});
    }
}

var lastLoadedChangesetId;
var changesetsLoading;
var shouldLoadChangesets;
var currentViewType;

function appendChangesetsBottom(changesetsByDay) {
    for (var day in changesetsByDay) {
        //find or create day container
        var dayElement = getDayContainer(day);
        if (dayElement.length == 0) {
            //create new day element
            $('#content').append($("#dayTemplate").render({date:day}));
        }
        dayElement = getDayContainer(day);
        var changesetsForDay = changesetsByDay[day];
        for (var i = 0; i < changesetsForDay.length; i++) {
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
            var that = this;
            setTimeout(function () {
                hideSpanForClippy(that);
                removeClippyObject(that);
            }, 5000)
        });
});

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
});

function onChange(observed, observedProperty, callback) {
    $(observed).on('propertyChange', function (_, changeDetails) {
        if (changeDetails.path == observedProperty) {
            callback();
        }
    });
}

function appendChangeset(changesetData, dayElement) {
    var changeset = new Changeset(changesetData)
    dayElement.children('.changesets').append($("<span id='templatePlaceholder'></span>"));
    $.link.changesetTemplate('#templatePlaceholder', changeset, {target:'replace'});
}

function toggleChangesetDetails(identifier) {
    var changesetDetails = $('#changesetDetails-' + identifier);
    if (changesetDetails.is(':visible')) {
        changesetDetails.slideUp('slow', function () {
            closeAllFilesAndScrollToChangesetTop(identifier);
        });
    } else {
        changesetDetails.slideDown();
    }
}

function closeAllFilesAndScrollToChangesetTop(identifier) {
    $('.changeset[data-identifier=' + identifier + '] .left.column .projectFile').each(function () {
        hideComments($(this));
    });
    hideFileListings($('.changeset[data-identifier=' + identifier + '] .fileListing'), function() {
        $.scrollTo('.changeset[data-identifier=' + identifier + ']', scrollDuration, { offset: scrollOffset })
    });
}

