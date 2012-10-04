(function ($) {

    var floatings = []

    function makeFloating(element, floatWithinParentSelector) {
        var $element = $(element);
        if (floatWithinParentSelector) {
            $element.data('float-within', floatWithinParentSelector)
        }
        $element.css('position', 'relative')
        floatings.push($element)
    };

    $.fn.floatWithin = function(floatWithinParentSelector) {
        return this.each(function(i, elem) { makeFloating(elem, floatWithinParentSelector) });
    }

    $.fn.floatWithin.defaults = {
        animationDuration: 500
        , debouncingPeriod: 200
        , stickToBottomOffset: 25
        , offset: 0
    }


    function contentStaticTop(element) {
        return element.position().top + element.margin().top + element.border().top + element.padding().top
    }

    function contentHeight(element) {
        const padding = element.padding();
        return element.innerHeight() - padding.top - padding.bottom
    }

    function maxRelativeTopWithin(container, floating) {
        return contentHeight(container) - floating.outerHeight(true)
    }

    function getNewTop(scrollTopRelative, maxRelativeTop, stickToBottomOffset) {
        var newTop
        if (scrollTopRelative < 0) {
            newTop = 0
        } else if (scrollTopRelative > maxRelativeTop - stickToBottomOffset) {
            newTop = maxRelativeTop
        } else {
            newTop = scrollTopRelative
        }
        return newTop;
    }

    function floatWithinContainer(floating) {
        floating.stop()
        var animationDuration = $.fn.floatWithin.defaults.animationDuration
        var stickToBottomOffset = $.fn.floatWithin.defaults.stickToBottomOffset
        var container = floating.parents(floating.data('float-within')).first()
        var scrollTopRelative = $(window).scrollTop() - contentStaticTop(container);
        var maxRelativeTop = maxRelativeTopWithin(container, floating);
        var newTop = getNewTop(scrollTopRelative  + $.fn.floatWithin.defaults.offset, maxRelativeTop, stickToBottomOffset);
        floating.animate({'top':newTop}, animationDuration);
    }

    function onScrolled() {
        $(floatings).each(function () {
            floatWithinContainer(this)
        })
    }

    $(function () {
        $('*[data-float-within]').each(function (i, elem) {
            makeFloating(elem)
        })
        $(window).scroll($.debounce($.fn.floatWithin.defaults.debouncingPeriod, onScrolled))
    })

})(jQuery)
