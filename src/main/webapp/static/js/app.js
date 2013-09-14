// See - http://api.jquery.com/jQuery/#jQuery-callback
jQuery(function($) {
    $('.anchor').each(function() {
        // Keep a local reference to this element for our callback functions
        var anchor = $(this);

        // Stop clicks on anchors from triggering events on parent elements
        anchor.click(function (event) {
            event.stopPropagation();
        });

        // Show the anchor link when hovering over parent elements
        anchor.parent().hover(
            function() {
                anchor.css('opacity', '0.6');
            },
            function() {
                anchor.css('opacity', '0.0');
            }
        );
    });
});
