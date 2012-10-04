function sum(collection, summedProperty) {
    summedProperty = summedProperty || function() { return this; };
    var result = 0;
    $(collection).each(function () {
        result += summedProperty.call(this);
    });
    return result;
}

function property(propertyName) {
    return function() {
        var property = this[propertyName];
        return $.isFunction(property) ? property.call(this) : property;
    };
}

function onNthCall(n, callback) {
    var remainingCalls = n;
    return function() {
        if (--remainingCalls == 0) {
            callback();
        }
    };
}

function toBoolean(toConvert) {
    return JSON.parse(toConvert);
}
