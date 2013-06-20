!function ($) {

  $(function(){

  //  var $window = $(window);

    // Disable certain links in docs
    $('section [href^=#]').click(function (e) {
      e.preventDefault();
    });
    
    $('.section').hover( function () {
    	$(this).children('.anchor').css('opacity', '0.6');
	},
	function () {
		$(this).children('.anchor').css('opacity', '0.0');
	});
    
    $('.row').hover( function () {
    	$(this).children('.anchor').css('opacity', '0.6');
	},
	function () {
		$(this).children('.anchor').css('opacity', '0.0');
	});
    
    
    
    pageTitle = $('.page-title').text();
    pageDescription = $('.title-block .item-title').text();
    $('head').append('<meta http-equiv="twitter:title" content="'+pageTitle+'" /> ');
    $('head').append('<meta http-equiv="twitter:description" content="'+pageDescription+'" /> ');
    $('head').append('<meta http-equiv="twitter:site" content="@nysenate" /> ');
    $('head').append('<meta http-equiv="twitter:card" content="summary" /> ');
    var url = window.location.pathname;
    $('head').append('<meta http-equiv="twitter:url" content="'+url+'" /> ');
   });

}(window.jQuery);