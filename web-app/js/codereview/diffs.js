

function addAdditionalContexts(hunks, fullListingText) {

    function addContextHunk(newFileStartLine, newFileEndLine, oldFileStartLine) {
        if (newFileStartLine < newFileEndLine) {
            var contextHunk = new ContextHunk(oldFileStartLine, newFileStartLine)
            for (var line = newFileStartLine; line < newFileEndLine; line++) {
                contextHunk.addLine(listingLines[line - 1])
            }
            contextHunks.push(contextHunk)
        }
    }
    var contextHunks = []
    var listingLines = fullListingText.split('\n')

    addContextHunk(1, hunks[0].newFileStartLine, 1)
    for (var i = 0; i + 1 < hunks.length; i++) {
        addContextHunk(hunks[i].newFileEndLine() + 1, hunks[i + 1].newFileStartLine, hunks[i].oldFileEndLine() + 1)
    }
    addContextHunk(hunks[hunks.length - 1].newFileEndLine() + 1, listingLines.length, hunks[hunks.length - 1].oldFileEndLine() + 1)

    return merge(hunks, contextHunks, compareBy(function (hunk) {
        return hunk.newFileStartLine
    }))
}

function compareBy(sizeFunction) {
    return function (a, b) {
        return sizeFunction(a) - sizeFunction(b)
    }
}

function merge(xs, ys, comparator) {
    return xs.concat(ys).sort(comparator)
}

function extractHunks(diffText) {
    var hunksText = diffText.replace(/^[^@]*/m, '');
    var hunksData = extractHunksData(hunksText);
    var hunks = $.map(hunksData, createHunk);
    return hunks;
}

function extractHunksData(hunksText) {
    var hunksData = [];
    var hunkMatch;
    while (hunkMatch = hunkPattern.exec(hunksText)) {
        hunksData.push({
            oldFileStartLine: parseInt(hunkMatch[1], 10),
            newFileStartLine: parseInt(hunkMatch[2], 10),
            description:hunkMatch[3],
            text:hunkMatch[4]
        })
    }
    return hunksData
}

var hunkPattern = /@@[^-]*-(\d+)[^+]*\+(\d+)[^@]*@@([^\n]*)\n(([^@]|@[^@])*)/g;

function createHunk(hunkData) {
    return new Hunk(hunkData)
}

function Hunk(hunkData) {
    $.extend(this, hunkData);
    this.diffSpans = createDiffSpans(this)

    this.newFileEndLine = function() {
        return this.diffSpans[this.diffSpans.length - 1].newFileEndLine
    }

    this.oldFileEndLine = function() {
        return this.diffSpans[this.diffSpans.length - 1].oldFileEndLine
    }
}

function ContextHunk(oldFileStartLine, newFileStartLine) {
    this.isAdditionalContext = true
    this.newFileStartLine = newFileStartLine
    this.oldFileStartLine = oldFileStartLine
    var contextSpan = new DiffSpan(oldFileStartLine, newFileStartLine);
    this.diffSpans = [contextSpan]

    this.addLine = function (line) {
        contextSpan.appendLine('context', line)
    }
}

function createDiffSpans(hunk) {
    var currentSpan = new DiffSpan(hunk.oldFileStartLine, hunk.newFileStartLine);
    var diffSpans = [currentSpan];
    var hunkLines = hunk.text.replace(/\n+$/g, ' ').split('\n')
    $(hunkLines).each(function (_, hunkLine) {
        var newSpan = currentSpan.appendOrFork(hunkLine)
        if (newSpan !== currentSpan) {
            diffSpans.push(newSpan)
            currentSpan = newSpan
        }
    });
    return diffSpans
}

function DiffSpan(oldFileStartLine, newFileStartLine) {
    this.oldFileStartLine = oldFileStartLine;
    this.newFileStartLine = newFileStartLine;
    this.oldFileEndLine = oldFileStartLine - 1;
    this.newFileEndLine = newFileStartLine - 1;

    this.appendOrFork = function (diffLine) {
        var type = getType(diffLine);
        if (this.isOfCompatibleType(type)) {
            this.appendLine(type, diffLine.substring(1));
            return this
        } else {
            return new DiffSpan(this.oldFileEndLine + 1, this.newFileEndLine + 1).appendOrFork(diffLine)
        }
    };

    this.isOfCompatibleType = function (type) {
        return (!this.context && !this.oldFile && !this.newFile) ||
            (type == 'meta' && (this.context || this.newFile)) ||
            (type == 'context' && this.context) ||
            ((type == 'oldFile' || type == 'newFile') && (this.oldFile || this.newFile))
    };

    this.appendLine = function (type, hunkLine) {
        if (!this[type]) {
            this[type] = { text: '' }
        }
        this[type].text += hunkLine + "\n";
        if (type == 'context' || type == 'oldFile') {
            this.oldFileEndLine++
        }
        if (type == 'context' || type == 'newFile') {
            this.newFileEndLine++
        }
    };
}

function getType(hunkLine) {
    switch (true) {
        case hunkLine[0] == '-': return 'oldFile';
        case hunkLine[0] == '+': return 'newFile';
        case hunkLine[0] == ' ': return 'context';
        case hunkLine == '\\ No newline at end of file': return 'meta'
        default: $.error('The line "' + hunkLine + '" is not a hunk line')
    }
}
