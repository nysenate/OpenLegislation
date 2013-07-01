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
    
//    pageTitle = $('.page-title').text();
//    pageDescription = $('.title-block .item-title').text();
//    $('head').append('<meta http-equiv="twitter:title" content="'+pageTitle+'" /> ');
//    $('head').append('<meta http-equiv="twitter:description" content="'+pageDescription+'" /> ');
//    $('head').append('<meta http-equiv="twitter:site" content="@nysenate" /> ');
//    $('head').append('<meta http-equiv="twitter:card" content="summary" /> ');
//    var url = window.location.pathname;
//    $('head').append('<meta http-equiv="twitter:url" content="'+url+'" /> ');
});
