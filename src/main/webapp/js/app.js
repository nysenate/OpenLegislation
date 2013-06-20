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
    
    
  });

}(window.jQuery);