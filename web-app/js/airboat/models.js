function Changeset(data) {
    var that = this;

    $.extend(this, data, {
        projectFiles: [],
        shortIdentifier: data.identifier.substr(0, hashAbbreviationLength) + "..."
    });

    this.commentsCount = function () {
        return this.comments.length + sum(this.projectFiles, property('commentsCount'));
    };

    this.addComment = function (comment) {
        $.observable(this.comments).insert(this.comments.length, comment);
        $.observable(this).setProperty('commentsCount');
    };

    this.addProjectFile = function(projectFileData) {
        var projectFile = new ProjectFile(projectFileData);
        projectFile.changeset = this;
        onChange(projectFile, 'commentsCount', triggerChange(that, 'commentsCount'));
        $.observable(this.projectFiles).insert(this.projectFiles.length, projectFile);
    };

    $(data.projectFiles).each(function (_, projectFileData) {
        that.addProjectFile(projectFileData);
    });
}

function ProjectFile(data) {
    var that = this;

    $.extend(this, data, {
        name:sliceName(data.name),
        isDisplayed:false,
        threadPositionsLoaded: false,
        _commentsCount: data.commentsCount,
        threadPositions: []
    });

    this.updateCommentThreads = function(threadPositionsData) {
        var threadPositions = $.map(threadPositionsData, function (data) {
            var threadPosition = new ThreadPosition(data);
            onChange(threadPosition, 'commentsCount', triggerChange(that, 'commentsCount'));
            return threadPosition;
        });
        $.observable(this.threadPositions).refresh(threadPositions);
        $.observable(this).setProperty('threadPositionsLoaded', true);
        $.observable(this).setProperty('commentsCount');
    };

    this.commentsCount = function () {
        if (this.threadPositionsLoaded) {
            return sum(this.threadPositions, property('commentsCount'));
        } else {
            return this._commentsCount;
        }
    };
}

function sliceName(name) {
    return name.toString().replace(/\//g, '/&#8203;');
}

function ThreadPosition(data) {
    var that = this;

    $.extend(this, data, {
        threads: $.map(data.threads, function(data) {
            var thread = new Thread(data);
            onChange(thread, 'commentsCount', triggerChange(that, 'commentsCount'));
            return thread;
        })
    });

    this.commentsCount = function() {
        return sum(this.threads, property('commentsCount'));
    };
}

function triggerChange(changedObject, changedPropertyName) {
    return function () {
        $.observable(changedObject).setProperty(changedPropertyName);
    };
}

function Thread(data) {
    $.extend(this, data);

    this.commentsCount = function () {
        return this.comments.length;
    };

    this.addComment = function (commentData) {
        $.observable(this.comments).insert(this.comments.length, commentData);
        $.observable(this).setProperty('commentsCount');
    };

}
