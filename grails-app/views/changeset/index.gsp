<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>

    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-tooltip.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-popover.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-dropdown.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.md5.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.syntaxhighlighter/jquery.syntaxhighlighter.js')}"
            type="text/javascript"></script>
    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <script src="${createLink(uri: '/libs/jquery.scrollTo-1.4.3.1.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.ba-throttle-debounce.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.sizes.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/jquery.floatWithin.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/airboat/utils.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/airboat/models.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/airboat/comments.js')}" type="text/javascript"></script>
    <link href=" ${createLink(uri: '/css/diffs.less')}" type="text/less" rel="stylesheet" media="screen"/>
    <script src="${createLink(uri: '/js/airboat/files.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/airboat/diffs.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/airboat/changesets.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/clippy/jquery.clippy.js')}" type="text/javascript"></script>
</head>

<body>
<div class="underNavbar">

<div class="test">

</div>

<div class="navbar navbar-fixed-top navbar-inverse">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="${createLink(uri: '/')}">
                <span class='highlighted'>
                    Airboat
                </span>
            </a>
            <span id='projectChooser'></span>
            <span id='filterChooser'></span>

            <ul class='nav pull-right'>
                <span id="loginStatus"></span>
                <li>
                    <a href="https://docs.google.com/spreadsheet/viewform?formkey=dElrejNuNVUzNEt3LTJZQnVCQ3RILWc6MQ#gid=0"
                       target="_blank">Feedback</a>
                </li>
            </ul>
        </div>
    </div>
</div>


<script type="text/javascript">
    $.SyntaxHighlighter.init({
        stripEmptyStartFinishLines:false,
        baseUrl:'${createLink(uri: '/libs/jquery.syntaxhighlighter')}',
        prettifyBaseUrl:'${createLink(uri: '/libs/prettify')}'
    });
</script>

<script id='scrollConfig' type="text/javascript">
    var scrollOffset = {top: -airboat.navbarOffset};
    var scrollDuration = 200;
</script>

<div class="padding">
    <div id="content"></div>
</div>

<script type="text/javascript">

    $('body').on('click', '.changeset .basicInfo, .changeset .details .lessButton', function () {
        var changeset = $(this).parents('.changeset').first();
        toggleChangesetDetails(changeset[0].dataset.identifier);
    });

    $('body').on('click', '.projectLink', function (e) {
        $(document).scrollTop(0);
        if (currentViewType != VIEW_TYPE.PROJECT || airboat.displayedProjectName != this.dataset.project) {
            showProject(this.dataset.project, HISTORY_OPERATION.PUSH);
        }
        $('#projectsDropdown').removeClass('open');
        return false;
    });

    $('body').on('click', '.filterLink', function (e) {
        $(document).scrollTop(0);
        var additionalInfo = '';
        if (this.dataset.filter == 'fileFilter') {
            additionalInfo = $('#fileFilterFile').val();
        }
        var filter = {filterType: this.dataset.filter, additionalInfo: additionalInfo};
        if (currentViewType != VIEW_TYPE.FILTER || airboat.currentFilter != filter) {
            showFiltered(filter, HISTORY_OPERATION.PUSH);
        }
        $('#filtersDropdown').removeClass('open');
        return false;
    });

    window.addEventListener('popstate', function (e) {
        if (e.state != null) {
            if (e.state.dataType == DATA_TYPE.CHANGESET) {
                renderChangeset(e.state);
            } else if (e.state.dataType == DATA_TYPE.PROJECT) {
                renderProject(e.state);
            } else if (e.state.dataType == DATA_TYPE.FILTER) {
                renderFilter(e.state);
            }
        }
    });

    function renderChangeset(state) {
        window.location.href = '?' + $.param({projectName: state.projectName, changesetId: state.changesetId});
        airboat.shouldLoadChangesets = false;
        setAllFiltersInactive();
    }

    function renderProject(state) {
        $(document).scrollTop(0);
        if (currentViewType != VIEW_TYPE.PROJECT || airboat.displayedProjectName != state.projectName) {
            showProject(state.projectName, HISTORY_OPERATION.NONE);
        }
    }

    function renderFilter(state) {
        $(document).scrollTop(0);
        if (currentViewType != VIEW_TYPE.FILTER || airboat.currentFilter != state.filterType) {
            showFiltered(state.filterType, HISTORY_OPERATION.NONE);
        }
    }

    function clearFilters() {
        showProject('', HISTORY_OPERATION.PUSH);
    }

    $.views.helpers({
        getGravatar:function (email, size) {
            var size = size || 50;
            return 'http://www.gravatar.com/avatar/' + $.md5(email) + '.jpg?' + $.param({
                s:size,
                d:'identicon'
            });
        }, colorForProjectName:function (projectName) {
            var md5hash = $.md5(projectName);
            return  colorFromMd5Hash(md5hash.substr(0, 12));
        }, iconForChangeType:function (changeType) {
            return iconForChangeType[changeType]
        }, textForChangeType:function (changeType) {
            return textForChangeType[changeType]
        }
    });

    var iconForChangeType = {
        ADD:'icon-plus',
        DELETE:'icon-minus',
        MODIFY:'icon-edit',
        RENAME:'icon-pencil',
        COPY:'icon-move'
    };

    var textForChangeType = {
        ADD:'added',
        DELETE:'deleted',
        MODIFY:'modified',
        RENAME:'renamed',
        COPY:'copied'
    };

    function colorFromMd5Hash(md5hash) {
        var colorCount = 18;
        var numberOfHuesInHSL = 360;
        var color = (numberOfHuesInHSL / colorCount) * (parseInt(md5hash, 16) % colorCount);
        return "hsl(" + color + ", 50%, 50%)"
    }

    $('[data-libs=tooltip]').livequery(function () {
        $(this).tooltip();
    });

    $().ready(function () {
        airboat.templates.compileAll('loginStatus', 'changeset', 'comment', 'projectChooser', 'filterChooser', 'diffAndFileListing');

        renderFiltersDropdowns();
        $.link.loginStatusTemplate('#loginStatus', airboat, {target:'replace'});

        if ('${type}' == DATA_TYPE.CHANGESET) {
            appendChangesetsBottom(${changeset});
            toggleChangesetDetails("${changesetId}");
            history.replaceState({dataType:'${type}', changeset: ${changeset ?: "''"}, changesetId:"${changesetId}", projectName:'${projectName}' }, null);
            airboat.shouldLoadChangesets = false;
            airboat.currentViewType = VIEW_TYPE.SINGLE_CHANGESET; // if there will be scrolling to changeset view type might be PROJECT
            setAllFiltersInactive();
        } else if ('${type}' == DATA_TYPE.PROJECT) {

            if (toBoolean(${singleProject})) {
                showProject("${projectName}", HISTORY_OPERATION.REPLACE);
            } else {
                showProject('', HISTORY_OPERATION.REPLACE);
            }
        } else if ('${type}' == DATA_TYPE.FILTER) {
            showFiltered({filterType:'${filter ? filter.filterType:''}', additionalInfo:'${filter ? filter.additionalInfo:''}'}, HISTORY_OPERATION.REPLACE);
        }

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });

        $(".colorbox").colorbox(airboat.colorboxSettings);
        $('.dropdown-toggle').dropdown();

        $('body').on('airboat-pageStructureChanged')
    });

    function renderFiltersDropdowns() {
        $.getJSON(uri.project.names, function (namesOfProjects) {
            airboat.projectNames = namesOfProjects;

            $.link.projectChooserTemplate('#projectChooser', airboat, {target:'replace'});
            $.link.filterChooserTemplate('#filterChooser', airboat, {target:'replace'});

            $('.clearFilters').on('click', function() {
                clearFilters();
                return false;
            });
        });
    }

    var loadingGritter;

    $(document)
            .ajaxStart(function () {
                loadingGritter =  $.gritter.add({
                    title: 'Loading',
                    text: 'New data is currently loaded.',
                    image: '${createLink(uri: '/css/images/328.gif')}',
                    sticky: true
                });
            }).ajaxStop(function () {
                $.gritter.remove(loadingGritter, {fade: true});
                $('body').trigger('airboat-pageStructureChanged'); //most probably
            });

    function onLoggedIn(username, isAdmin) {
        isAdmin = isAdmin ? true : false;
        $.colorbox.close();
        $.observable(airboat).setProperty('loggedInUserName', username);
        $.observable(airboat).setProperty('isAdmin', isAdmin);
        setUserPreferences(username);
    }

    function setUserPreferences(username) {
        var url = "${createLink(uri: '/user/fetchSkinOptions/')}" + username;
        $.getJSON(url, function (skinOptions) {
            var skinHref = "${createLink(uri: '/libs/bootstrap/less/')}" + skinOptions.skin + "/swatchmaker.less";
            var skinOptions = {username:username, href:skinHref };
            $.cookies.set('skin', skinOptions);
            $("#skin").attr("href", $.cookies.get('skin').href);
        });
    }
</script>

<script id='projectChooserTemplate' type='text/x-jsrender'>

    <ul class="nav">
        <li id="projectsDropdown" class="dropdown navbarToggle">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Project <span class='currentFilter'
                    data-link='displayedProjectName'></span><span class="clearFilters" style="display:none"> [x]</span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" data-target="#" data-project='' class='projectLink'>All projects</a>
                </li>
                {{for projectNames}}
                    <li><a href="javascript:void(0)" data-target="#"
                           data-project='{{:name}}' class='projectLink'>{{:name}}</a>
                    </li>
                {{/for}}
            </ul>
        </li>
    </ul>
</script>

<script id='filterChooserTemplate' type='text/x-jsrender'>
    <ul class="nav">
        <li id="filtersDropdown" class="dropdown navbarToggle">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Filters <span class='currentFilter'
                    data-link='currentFilter["filterType"]'></span><span class="clearFilters" style="display:none"> [x]</span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" data-target="#" data-filter='commentedChangesets'
                       class='filterLink'>Commented changesets</a>
                </li>
                <li data-link="visible{: loggedInUserName !== '' }"><a href="javascript:void(0)" data-target="#"
                                                                       data-filter='myCommentsAndChangesets'
                                                                       class='filterLink'>My comments and changesets</a>
                </li>
                <li>
                    <form class="navbar-form">
                        <input type="text" class="span2" id="fileFilterFile" placeholder='Search file...'>
                        <i class="icon-chevron-right filterLink" data-filter='fileFilter'></i>
                    </form>
                </li>
            </ul>
        </li>
    </ul>
</script>

<script id="loginStatusTemplate" type="text/x-jsrender">
    <li data-link="visible{: loggedInUserName !== '' }">
        %{--TODO use uri global variable when referencing a controller--}%
        <a data-link="href{: 'user/options/'}"><span data-link="loggedInUserName"></span></a>
    </li>
    <li data-link="visible{: isAdmin }">
        <g:link controller='user' action='admin'>Admin page</g:link>
    </li>
    <li data-link="visible{: loggedInUserName !== '' }">
        <g:link controller='logout'>Log out</g:link>
    </li>

    <li data-link="visible{: loggedInUserName === '' }">
        <g:link class='colorbox' url='login'>Login</g:link>
    </li>
    <li data-link="visible{: loggedInUserName === '' }">
        <g:link class='colorbox' url='register'>Register</g:link>
    </li>
</script>

<script id="dayTemplate" type="text/x-jsrender">
    <div class="day"
         data-date='{{:date}}'>
        <div class="row-fluid">
            <h3 class="dayLabel well-small span5">
                {{>date}}
            </h3>
        </div>

        <div class="changesets">
            <!-- here will be changestes for the given day -->
        </div>
    </div>
</script>

<script>
    $.fn.floatWithin.defaults.offset = 55;

    $('.changeset .left.column').livequery(function () {
        $(this).floatWithin('.changeset');
    });

    $('.changeset').livequery(function () {
        $(this)
                .on('click', '.closeButton', function (event) {
                    event.stopPropagation();
                    var changeset = $(this).parents('.changeset').first();
                    var changesetIdentifier = airboat.getModel(changeset).identifier;
                    var projectFile = $(this).parents('.projectFile').first();
                    var projectFileId = airboat.getModel(projectFile).id;
                    hideFileAndScrollToPreviousFileOrChangesetTop(changesetIdentifier, projectFileId);
                })
                .on('click', '.openAllFiles', function () {
                    var $changeset = $(this).parents('.changeset').first();
                    $.scrollTo(changeset,  scrollDuration, {offset: scrollOffset});
                    var changeset = airboat.getModel($changeset);
                    $(changeset.projectFiles).each(function (_, projectFile) {
                        showFile(projectFile);
                        showComments($('.left.column .projectFile[data-id="' + projectFile.id + '"]'))
                    });
                })
                .on('click', '.closeAllFiles', function () {
                    var changeset = $(this).parents('.changeset').first();
                    var changesetIdentifier = airboat.getModel(changeset).identifier;
                    closeAllFilesAndScrollToChangesetTop(changesetIdentifier)
                })
    });
</script>

<script id="changesetTemplate" type="text/x-jsrender">

    <div class='changeset row-fluid'
         data-identifier='{{:identifier}}' data-id='{{:id}}'>

        <div class="column left span5 well well-small">

            <div class="basicInfo row-fluid">
                <img class="pull-left" src='{{>~getGravatar(email)}}'/>

                <div class="pull-right">
                    <i class="icon-comment"></i>
                    <span class='commentsCount' data-link="commentsCount"></span>
                </div>

                <div class="nextToGravatar">

                    <div class="commitMessage"><h5>{{>commitMessage}}</h5></div>

                    <div class="commitFooter">
                        <span class='author'>{{>author}}</span> in
                        <span class="badge"
                              style="background-color: {{>~colorForProjectName(projectName)}}">{{>projectName}}</span>

                        <span class="pull-right changeset-date" data-date='{{:date}}'><i
                                class="icon-time"/> {{:date.substring(11)}}</span>

                        <span class="pull-right changeset-hash" data-changeset_identifier='{{:identifier}}'>
                            <span class='pull-right'>
                                {{>shortIdentifier}}
                            </span>
                            <span class="hashForClippy-{{:identifier}} pull-right">
                                <span class="clippy-{{:identifier}}" data-text="{{:identifier}}"></span>
                            </span>
                        </span>

                    </div>

                </div>

                <div class="clearfix"></div>
            </div>

            <div id="changesetDetails-{{>identifier}}" style="display:none;" class="details row-fluid margin-top-small" >

                <h5>Comments:</h5>

                <div class="comments" id="comments-{{>identifier}}">
                    {{for comments tmpl='#commentTemplate' ~archiveFunction='addToArchiveChangesetComment' /}}
                </div>

                {{for [#data] tmpl='#changesetCommentFormTemplate' /}}

                <h5 class="pull-left">Changed files:</h5>
                <h5 class='pull-right'>
                    <i class="openAllFiles icon-folder-open" title='Open all files' data-libs='tooltip'/>
                    <i class="closeAllFiles icon-folder-close" title='Close all files' data-libs='tooltip'/>
                </h5>
                <div class="clearfix"></div>

                <div class="projectFiles accordion margin-bottom-small" id="accordion-{{>identifier}}">

                    {{for projectFiles tmpl='#projectFileRowTemplate' /}}

                </div>

                <a class="wideButton lessButton">
                    <div class="center sizeOfIcon"><i class="icon-chevron-up"></i></div>
                </a>
            </div>
        </div>

        %{--FIXME work on structure here --}%
        <div class="fileListings span7">
        {{for projectFiles tmpl='#projectFileListingTemplate' /}}
        </div>
    </div>
</script>

<script id='projectFileListingTemplate' type="text/x-jsrender">
    <div class="projectFile fileListing well" style="display: none;" data-id={{:id}}>

        <h4 class="pull-left noTopMargin">{{:name}}</h4>

        <i class="closeButton icon-remove pull-right"> </i>
        <div class="clearfix"></div>

        <div class="diffAndFileListing margin-top-small">

        </div>
    </div>
</script>

<script type="text/javascript">
    $("body").on('change', '.fileListing input[name="showWholeFile"]', function() {
        var diffViewer = $(this).parents('.diffAndListingViewer')[0];
        var listing = airboat.getModel(diffViewer);
        $.observable(listing).setProperty('showWholeFile', this.checked);
        removeLineCommentPopover($(this).parents('.fileListing'));
    })
</script>

<script id="projectFileRowTemplate" type="text/x-jsrender">
    <div class="projectFile" data-id={{:id}}>
        {{for [#data] tmpl='#projectFileBodyTemplate' }}{{/for}}
    </div>
</script>

<script type="text/javascript">

    $('.changeset').livequery(function () {
        $(this)
                .on('click', '.left.column .projectFile .toggleCommentsAndListings',function (event) {
                    var $projectFile = $(this).parents('.projectFile');
                    var projectFile = airboat.getModel($projectFile[0]);

                    if (!projectFile.isDisplayed || !projectFile.commentsDisplayed) {
                        showFile(projectFile, function ($fileListing) {
                            $.scrollTo($fileListing, scrollDuration, {offset:scrollOffset});
                        });
                        showComments($projectFile);
                    } else {
                        hideComments($projectFile);
                        hideFileAndScrollToPreviousFileOrChangesetTop(projectFile.changeset.identifier, projectFile.id);
                    }

                    return false;
                })
                .on('click', '.left.column .toggleComments', function (event) {
                    var $projectFile = $(this).parents('.projectFile');
                    var projectFile = airboat.getModel($projectFile[0]);

                    if (!projectFile.commentsDisplayed) {
                        showComments($projectFile);
                    } else {
                        hideComments($projectFile)
                    }

                    return false;
                });
    });
</script>

<script type="text/javascript">
    $('.snippet [class|=language]').livequery(function () {

        $.SyntaxHighlighter.init({
            load:false,
            highlight:false,
            lineNumbers:true,
            stripInitialWhitespace:false,
            stripEmptyStartFinishLines:false
        });

        $(this).syntaxHighlight();
    });
</script>

<script id="projectFileBodyTemplate" type="text/x-jsrender">
    <a data-link="class{: 'toggleCommentsAndListings manualLinkText ' + (isDisplayed ? 'selected' : '') }">
        <i title="{{: ~textForChangeType(changeType.name) }}"
           class="{{: ~iconForChangeType(changeType.name) }}"></i>
        <span data-link="class{: isDisplayed ? '' : 'linkText' }">{{:name}}</span>
        <i class="closeButton icon-remove"
           data-link="style{: 'display:' + (isDisplayed ? 'inline-block' : 'none') }"> </i>
        <span class="toggleComments pull-right" data-link="visible{: commentsCount }" title='Show / hide comments' data-libs='tooltip'>
            <i class="icon-comment"></i><span class='commentsCount' data-link="commentsCount"></span>
        </span>
    </a>

    <div class="details" style="display:none;">
        {{for threadPositions ~fileType=fileType}}
        <div class='threadPosition' data-link="visible{: commentsCount }">
            {{for threads}}
            <div class="thread" data-id='{{:id}}' data-link="visible{: commentsCount }">
                <div class="comments">
                    {{for comments tmpl='#commentTemplate' ~archiveFunction='addToArchiveLineComment' /}}
                </div>

                {{for [#data] tmpl='#replyCommentFormTemplate' ~submitFunction='addReply' /}}
            </div>
            {{/for}}

            <div class="snippet">
                <pre class="language-{{:~fileType}} linenums:{{:lineNumber}}">{{>snippet}}</pre>
            </div>

            <hr>
        </div>
        {{/for}}
    </div>

</script>

<script id='diffAndFileListingTemplate' type="text/x-jsrender">
    <div class='diffAndListingViewer'>

        <div class='pullLeft'>
            <label class="checkbox">
                <input type="checkbox" name='showWholeFile' data-link="checked{:showWholeFile}">show whole file
            </label>
        </div>
        <div class='clearfix'/>

        %{--TODO get rid of this conditional display and make changest to undrelying collections instead--}%
        <div data-link="visible{:showWholeFile}" class="margin-top-small">
            {{for [wholeFileHunks] tmpl='#diffTemplate' ~fileType=fileType ~showWholeFile=true/}}
        </div>

        <div data-link="visible{:!showWholeFile}" class="margin-top-small">
            {{for [diffHunks] tmpl='#diffTemplate' ~fileType=fileType ~showWholeFile=false/}}
        </div>
    </div>
</script>

<script type="text/javascript">

    $('.diff [class|=language]').livequery(function () {

        $.SyntaxHighlighter.init({
            load:false,
            highlight:false,
            lineNumbers:true,
            stripInitialWhitespace:false,
            stripEmptyStartFinishLines:false
        });

        $(this).syntaxHighlight();

        $(this).find('li').each(function() {
            var $listingLine = $(this);
            if ($listingLine.parents('.removed').length == 0) {
                $listingLine.click(function () {
                    var fileListing = $listingLine.parents('.fileListing')[0];
                    var projectFile = airboat.getModel(fileListing);
                    checkCanAddLineCommentAndShowForm($listingLine, projectFile)
                })
            }
        })
    })
</script>

<script id="diffTemplate" type="text/x-jsrender">
    {{for #data ~count=#data.length }}
    <div class='diff row-fluid'>
        {{for diffSpans}}
        <div class='diffRow row-fluid'>
            {{if oldFile }}
            <div data-link="class{:oldFile ? 'removed' : '' }">
                <pre class="language-{{: ~fileType}} noLinenums">{{> oldFile.text }}</pre>
            </div>
            {{/if}}
            {{if context || newFile }}
            <div data-link="class{:newFile ? 'added' : '' }">
                <pre class="language-{{: ~fileType}} linenums:{{:newFileStartLine}}">{{> context ? context.text : newFile.text }}</pre>
            </div>
            {{/if}}
        </div>
        {{/for}}
    </div>
    {{if !~showWholeFile && #index < ~count - 1 }}
    <p>...</p>
    {{/if}}
    {{/for}}
</script>

<!-- FIXME reuse comment form template for both types of comments -->
<script id="addLineCommentFormTemplate" type="text/x-jsrender">
        <form>
            <textarea id="add-line-comment-{{>fileId}}" placeholder="Add comment..." class='span4' rows='3'></textarea>

            <div class="validationErrors"></div>

            <div class="btn-group pull-right">
                <button type="button" class="btn btn-primary"
                        onClick="addLineComment('{{:changesetId}}', '{{:fileId}}', '{{:lineNumber}}')">Add comment</button>
                <button type="button" class="btn btn-primary"
                        onClick="closeLineCommentForm('{{:changesetId}}', '{{:fileId}}')">Cancel</button>
            </div>
        </form>
        <div class='clearfix'/>
</script>

<script id="cannotAddLineCommentMessageTepmlate" type="text/x-jsrender">
    <p class="alert alert-block">
        Adding comments to this version of file is disabled - a newer changeset with this file exists in this project.
    </p>
</script>

<script id="commentFormTemplate" type="text/x-jsrender">

    <form class="margin-bottom-small">
        <textarea onfocus="expandCommentForm($(this.parentElement))" placeholder="{{:~actionPrompt}}..."
                  class="span12" rows="1"></textarea>

        <div class="validationErrors"/>

        <div class="buttons btn-group pull-right" style="display: none;">
            <button type="button" class="btn btn-primary btnWarningBackground"
                    onClick="{{:~submitFunction}}($(this).parents('form').first())">{{:~actionPrompt}}</button>
            <button type="button" class="btn btn-primary"
                    onClick="resetCommentForm($(this).parents('form').first())">Cancel</button>
        </div>

        <div class="clearfix"></div>
    </form>

</script>

<script id="changesetCommentFormTemplate" type="text/x-jsrender">
    {{for [#data] tmpl='#commentFormTemplate' ~submitFunction='addComment' ~actionPrompt='Add comment'/}}
</script>

<script id="replyCommentFormTemplate" type="text/x-jsrender">
    {{for [#data] tmpl='#commentFormTemplate' ~submitFunction='addReply' ~actionPrompt='Add reply'/}}
</script>

<script id="commentTemplate" type="text/x-jsrender">

    <div class="comment" data-identifier='{{>id}}'>
        <img src="{{>~getGravatar(author, 35)}}"/>

        <div class="nextToGravatar">
            <a onclick="{{:~archiveFunction}}($(this).parents('.comment').first())" class='pull-right' href="javascript:void(0)">
                <i class='icon-remove-circle'></i>
            </a>
            <div class="comment-content">{{>text}}</div>

            <div class="comment-footer">
                <span class="comment-date pull-right"><i class="icon-time"/> {{:dateCreated}}</span>
                <span class="author pull-left">{{>author}}</span>
            </div>
        </div>

    </div>
</script>

<script id="errorCommentTemplate" type="text/x-jsrender">

    <div class="alert alert-block">
        {{: #data }}
    </div>
</script>

<script id="noMoreChangesetsTemplate" type="text/x-jsrender">

    <div class="row-fluid">
        <div class='offset4 span4 well margin-top-small'><g:message code='noMoreChangesets'/></div>
    </div>
</script>

</div>
</body>
</html>
