function showProject(projectName, historyOperation) {
    changesetsLoading = true;
    $.getJSON(uri.changeset.getLastChangesets + '?' + $.param({projectName:projectName}),
        function (data) {
            clearDisplayAndAppendChangesetsBottom({data:data,
                shouldLoad:true,
                viewType:VIEW_TYPE.PROJECT,
                activeSelector:'#projectsDropdown',
                historyOperation: historyOperation,
                currentFilter: {filterType:'', additionalInfo:''},
                displayedProject: projectName})
        });
}

function showFiltered(filter, historyOperation) {
    airboat.changesetLoading = true;
    $.getJSON(uri.changeset.getLastFilteredChangesets + '?' + $.param({filterType:filter.filterType, additionalInfo:filter.additionalInfo}),
        function (data) {
            clearDisplayAndAppendChangesetsBottom({data:data,
                shouldLoad:true,
                viewType:VIEW_TYPE.FILTER,
                activeSelector:'#filtersDropdown',
                historyOperation: historyOperation,
                currentFilter: filter,
                displayedProject: ''})
        });
}

var HISTORY_OPERATION = {NONE: 'none', PUSH: 'pushState', REPLACE: 'replaceState'};
function clearDisplayAndAppendChangesetsBottom(dataset) {
    shouldLoadChangesets = dataset.shouldLoad;
    currentViewType = dataset.viewType;
    $('#content').html("");
    $.observable(airboat).setProperty('displayedProjectName', dataset.displayedProject);
    $.observable(airboat).setProperty('currentFilter', dataset.currentFilter);
    if (dataset.historyOperation == HISTORY_OPERATION.REPLACE) {
        history.replaceState({dataType: dataset.viewType, projectName: airboat.displayedProjectName, filterType: airboat.currentFilter.filterType, additionalInfo:airboat.currentFilter.additionalInfo}, null);
    } else if (dataset.historyOperation == HISTORY_OPERATION.PUSH) {
        history.pushState({dataType: currentViewType, projectName:airboat.displayedProjectName, filterType: airboat.currentFilter.filterType, additionalInfo:airboat.currentFilter.additionalInfo}, null, getHref());
    }
    if (currentViewType == VIEW_TYPE.PROJECT && airboat.displayedProjectName == "") {
        setAllFiltersInactive();
    } else {
        setFilterActive(dataset.activeSelector);
    }
    appendChangesetsBottom(dataset.data.changesets);
    decideImportInfoAndLoadingState(dataset.data, 21); //as in Constants.FIRST_CHANGESET_LOAD_SIZE
}

function getHref() {
   if (currentViewType == VIEW_TYPE.PROJECT) {
       return airboat.displayedProjectName == '' ? '?' : '?' + $.param({projectName:airboat.displayedProjectName});
   } else if (currentViewType = VIEW_TYPE.FILTER) {
       return '?' + $.param({filterType:airboat.currentFilter.filterType, additionalInfo:airboat.currentFilter.additionalInfo});
   }
   return '';
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
    $(selector + ' .currentFilter').css('text-decoration', 'underline');
    $(selector + ' .clearFilters').css('display', 'inline');
}

function setAllFiltersInactive() {
    $('.navbarToggle .currentFilter').css('text-decoration', 'none');
    $('.navbarToggle .clearFilters').css('display', 'none');
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
        return uri.changeset.getNextFewFilteredChangesetsOlderThan  + '?' + $.param({changesetId:lastLoadedChangesetId, filterType: airboat.currentFilter.filterType, additionalInfo:airboat.currentFilter.additionalInfo});
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

