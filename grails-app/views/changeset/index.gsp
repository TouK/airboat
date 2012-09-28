<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>

    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-collapse.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-tooltip.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-popover.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/bootstrap/bootstrap-dropdown.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.md5.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.syntaxhighlighter/jquery.syntaxhighlighter.js')}"
            type="text/javascript"></script>
    <link href=" ${createLink(uri: '/css/jquery.syntaxhighlighter-fontOverride.css')}"
          type="text/css" rel="stylesheet" media="screen"/>

    <script src="${createLink(uri: '/libs/jquery.scrollto.min.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/libs/jquery.ba-throttle-debounce.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/libs/jquery.sizes.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/jquery.floatWithin.js')}" type="text/javascript"></script>

    <script src="${createLink(uri: '/js/codereview/comments.js')}" type="text/javascript"></script>
    <link href=" ${createLink(uri: '/css/diffs.less')}" type="text/less" rel="stylesheet" media="screen"/>
    <script src="${createLink(uri: '/js/codereview/files.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/diffs.js')}" type="text/javascript"></script>
    <script src="${createLink(uri: '/js/codereview/changesets.js')}" type="text/javascript"></script>

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
                    CodeReview
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

<div class="padding">
    <div id="content"></div>
</div>

<div class="alert alert-info" id="loading" style='display: none;'>
    <div class="well-small"><img id="loading-image" src="${createLink(uri: '/css/images/ajax-loader.gif')}"/> Loading...
    </div>
</div>

<script type="text/javascript">

    $('body').on('click', '.changeset .basicInfo, .changeset .details .lessButton', function () {
        var changeset = $(this).parents('.changeset').first();
        toggleChangesetDetails(changeset[0].dataset.identifier);
    });

    $('body').on('click', '.projectLink', function (e) {
        if (currentViewType != VIEW_TYPE.PROJECT || codeReview.displayedProjectName != this.dataset.project) {
            showProject(this.dataset.project);
            var href = this.dataset.project == '' ? '?' : '?' + $.param({projectName:this.dataset.project});
            history.pushState({dataType:DATA_TYPE.PROJECT, projectName:codeReview.displayedProjectName}, null, href);
        } else {
            $(document).scrollTop(0);
        }
        $('#projectsDropdown').removeClass('open');
        return false;
    });

    $('body').on('click', '.filterLink', function (e) {
        if (currentViewType != VIEW_TYPE.FILTER || codeReview.currentFilter != this.dataset.filter) {
            showFiltered(this.dataset.filter);
            history.pushState({dataType:DATA_TYPE.FILTER, filterType:codeReview.currentFilter}, null, '?' + $.param({filter:this.dataset.filter}));
        } else {
            $(document).scrollTop(0);
        }
        $('#filtersDropdown').removeClass('open');
        return false;
    });

    window.addEventListener('popstate', function (e) {
        if (e.state != null) {
            if (e.state.dataType == DATA_TYPE.CHANGESET) {
                window.location.href = '?' + $.param({projectName:e.state.projectName, changesetId:e.state.changesetId});
                codeReview.shouldLoadChangesets = false;
                setAllInactive();
            } else if (e.state.dataType == DATA_TYPE.PROJECT) {
                if (currentViewType != VIEW_TYPE.PROJECT || codeReview.displayedProjectName != e.state.projectName) {
                    showProject(e.state.projectName);
                } else {
                    $(document).scrollTop(0);
                }
            } else if (e.state.dataType == DATA_TYPE.FILTER) {
                if (currentViewType != VIEW_TYPE.FILTER || codeReview.currentFilter != e.state.filterType) {
                    showFiltered(e.state.filterType);
                } else {
                    $(document).scrollTop(0);
                }
            }
        }
    });

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

    $.ScrollTo.configure({
        offsetTop:codeReview.navbarOffset,
        duration:200
    });

    $('[data-libs=tooltip]').livequery(function () {
        $(this).tooltip();
    })

    $().ready(function () {
        codeReview.templates.compileAll('loginStatus', 'changeset', 'comment', 'projectChooser', 'filterChooser', 'diffAndFileListing');

        $.link.loginStatusTemplate('#loginStatus', codeReview, {target:'replace'});
        $.link.projectChooserTemplate('#projectChooser', codeReview, {target:'replace'});
        $.link.filterChooserTemplate('#filterChooser', codeReview, {target:'replace'});

        if ('${type}' == DATA_TYPE.CHANGESET) {
            appendChangesetsBottom(${changeset});
            toggleChangesetDetails("${changesetId}");
            history.replaceState({dataType:'${type}', changeset: ${changeset ?: "''"}, changesetId:"${changesetId}", projectName:'${projectName}' }, null);
            codeReview.shouldLoadChangesets = false;
            codeReview.currentViewType = VIEW_TYPE.SINGLE_CHANGESET; // if there will be scrolling to changeset view type might be PROJECT
            setAllInactive();
        } else if ('${type}' == DATA_TYPE.PROJECT) {

            if (toBoolean(${singleProject})) {
                showProject("${projectName}");
            } else {
                showProject('');
            }
            history.replaceState({dataType:'${type}', projectName:codeReview.displayedProjectName}, null);

        } else if ('${type}' == DATA_TYPE.FILTER) {
            showFiltered('${filterType}');
            history.replaceState({dataType:'${type}', filterType:codeReview.currentFilter }, null)
        }

        $(window).scroll(function () {
            if ($(window).scrollTop() == $(document).height() - $(window).height()) {
                onScrollThroughBottomAttempt()
            }
        });

        $(".colorbox").colorbox(codeReview.colorboxSettings);
        $('.dropdown-toggle').dropdown();

        $('body').on('codeReview-pageStructureChanged')
    });

    $(document)
            .ajaxStart(function () {
                $('#loading').show();
            }).ajaxStop(function () {
                $('#loading').hide();
                $('body').trigger('codeReview-pageStructureChanged'); //most probably
            });

    function onLoggedIn(username, isAdmin) {
        isAdmin = isAdmin ? true : false;
        $.colorbox.close();
        $.observable(codeReview).setProperty('loggedInUserName', username);
        $.observable(codeReview).setProperty('isAdmin', isAdmin);
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
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Project <span
                    data-link='displayedProjectName'></span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" data-target="#" data-project='' class='projectLink'>All projects</a>
                </li>
                <g:each in="${projects}" var="project">
                    <li><a href="javascript:void(0)" data-target="#"
                           data-project='${project.name}' class='projectLink'>${project.name}</a>
                    </li>
                </g:each>
            </ul>
        </li>
    </ul>
</script>

<script id='filterChooserTemplate' type='text/x-jsrender'>
    <ul class="nav">
        <li id="filtersDropdown" class="dropdown navbarToggle">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Filters <span
                    data-link='currentFilter'></span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" data-target="#" data-filter='commentedChangesets'
                       class='filterLink'>Commented changesets</a>
                </li>
                <li data-link="visible{: loggedInUserName !== '' }"><a href="javascript:void(0)" data-target="#"
                                                                       data-filter='myCommentsAndChangesets'
                                                                       class='filterLink'>My comments and changesets</a>
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
                    event.stopImmediatePropagation();
                    var changeset = $(this).parents('.changeset').first();
                    var changesetIdentifier = codeReview.getModel(changeset).identifier;
                    var projectFile = $(this).parents('.projectFile').first();
                    var projectFileId = codeReview.getModel(projectFile).id;
                    hideFileAndScrollToPreviousFileOrChangesetTop(changesetIdentifier, projectFileId);
                })
                .on('click', '.openAllFiles', function () {
                    var $changeset = $(this).parents('.changeset').first();
                    $changeset.ScrollTo();
                    var changeset = codeReview.getModel($changeset);
                    $(changeset.projectFiles).each(function (_, projectFile) {
                        showFile(changeset.identifier, projectFile.id)
                    });
                })
                .on('click', '.closeAllFiles', function () {
                    var changeset = $(this).parents('.changeset').first();
                    var changesetIdentifier = codeReview.getModel(changeset).identifier;
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
                    <span class='commentsCount' data-link="allComments">{{>allComments}}</span>
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

            <div id="changesetDetails-{{>identifier}}" style="display:none;" class="details row-fluid margin-top-small">

                <h5>Comments:</h5>

                <div class="comments" id="comments-{{>identifier}}">
                    {{for comments tmpl='#commentTemplate' /}}
                </div>

                <div id="comment-form-{{>identifier}}"></div>

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

        <i class="closeButton icon-remove pull-right"> </i>
        <br/>

        <div class="diffAndFileListing">

        </div>
    </div>
</script>

<script type="text/javascript">
    $("body").on('change', '.fileListing input[name="showWholeFile"]', function() {
        var diffViewer = $(this).parents('.diffAndListingViewer')[0];
        var listing = codeReview.getModel(diffViewer);
        $.observable(listing).setProperty('showWholeFile', this.checked);
        removeLineCommentPopover($(this).parents('.fileListing'));
    })
</script>

<script id="projectFileRowTemplate" type="text/x-jsrender">
    <div class="projectFile accordion-group" id="accordion-group-{{>collapseId}}"
         data-id={{:id}}>
        {{for [#data] tmpl='#accordionFileBodyTemplate'}}{{/for}}
    </div>
</script>

<script id="accordionFileBodyTemplate" type="text/x-jsrender">

    <div class="accordion-heading">
        <div class="row-fluid">

            <a data-link="class{: 'accordion-toggle manualLinkText ' + (isDisplayed ? 'selected' : '') }"
               data-toggle="collapse" data-parent="#accordion-{{>changeset.identifier}}"
               href="#collapse-inner-{{>collapseId}}">
                <i title="{{: ~textForChangeType(changeType.name) }}"
                   class="{{: ~iconForChangeType(changeType.name) }}"></i>
                <span data-link="class{: isDisplayed ? '' : 'linkText' }">{{:name}}</span>
                <i class="closeButton icon-remove"
                   data-link="style{: 'display:' + (isDisplayed ? 'inline-block' : 'none') }"> </i>
                <span class="pull-right" data-link="visible{: commentsCount != 0 }">
                    <i class="icon-comment"></i><span class='commentsCount' data-link="commentsCount"></span>
                </span>
            </a>
        </div>
    </div>

    <div id='collapse-inner-{{>collapseId}}' class="details accordion-body collapse">
        <div class="accordion-inner" id="accordion-inner-{{>id}}">
            <div id="fileComments-{{>collapseId}}"></div>
        </div>
    </div>
</script>

<script id="snippetTemplate" type="text/x-jsrender">
    <div class='oneLineComments' data-lineNumber='{{>lineNumber}}'>
        <div class="threads"></div>

        <div class='codeSnippet'></div><hr/>
    </div>
</script>

<script id="threadTemplate" type="text/x-jsrender">
    <div class="threadComments" data-identifier='{{>threadId}}'></div>
    <textarea class="addThreadReply span 12" placeholder="Reply..."
              onfocus="expandReplyForm('{{>threadId}}', '{{>changesetId}}')" data-identifier='{{>threadId}}'
              rows="1"></textarea>

    <div class="addLongCommentMessage threadReplyInfo" data-identifier='{{>threadId}}'></div>

    <div class="btn-group pull-right threadReplyFormButtons" data-identifier='{{>threadId}}'
         style="display: none; margin-bottom:10px">
        <button type="button" class="btn btn-primary threadReplyButton" data-identifier='{{>threadId}}'
                onClick="addReply('{{>threadId}}', '{{>changesetId}}', '{{>projectFileId}}')">Reply</button>
        %{--FIXME this function NEEEDS both changesetIdentifier and projectFileId to work in all cases--}%
        %{--amend parameters and corresponding markup--}%
        <button type="button" class="btn btn-primary"
                onClick="cancelReply('{{>threadId}}', '{{>changesetId}}')">Cancel</button>
    </div>

    <div class="clearfix"></div>
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
        <div data-link="visible{:showWholeFile}">
            {{for [wholeFileHunks] tmpl='#diffTemplate' ~fileType=fileType ~showWholeFile=true/}}
        </div>

        <div data-link="visible{:!showWholeFile}">
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
                    var projectFile = codeReview.getModel(fileListing);
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

            <div class="addLongCommentMessage"></div>

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
        <textarea onfocus="expandCommentForm($(this.parentElement))" placeholder="Add comment..."
                  class="span12" rows="1"></textarea>

        <div class="addLongCommentMessageToChangeset"></div>

        <div class="buttons btn-group pull-right" style="display: none;">
            <button type="button" class="btn btn-primary btnWarningBackground"
                    onClick="addComment($(this).parents('form').first(), '{{:identifier}}')">Add comment</button>
            <button type="button" class="btn btn-primary"
                    onClick="resetCommentForm($(this).parents('form').first())">Cancel</button>
        </div>
    </form>


    <div class="clearfix"></div>
</script>

<script id="commentTemplate" type="text/x-jsrender">

    <div class="comment">
        <img src="{{>~getGravatar(author, 35)}}"/>

        <div class="nextToGravatar">
            <div class="comment-content">{{>text}}</div>

            <div class="comment-footer">
                <span class="comment-date pull-right"><i class="icon-time"/> {{:dateCreated}}</span>
                <span class="author pull-left">{{>author}}</span>
            </div>
        </div>

    </div>
</script>

<script id="longCommentTemplate" type="text/x-jsrender">

    <div class="alert alert-block">
        {{: #data }}
    </div>
</script>
</div>
</body>
</html>
