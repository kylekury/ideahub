$(function() {
    var _bindAnchorLinks = function(){
        console.log("binding for " + location.hash);
        console.log("length is " + $('.page-scroll').length);
        $('.page-scroll').click(function() {
            var $anchor = $(this);
            $('html, body').stop().animate({
                scrollTop: $($anchor.attr('data-href')).offset().top
            }, 1500, 'easeInOutExpo');
        });
    };
    var _onhashchange = function(){
		if(location.hash != "#/home"){
			$("nav.navbar").addClass("navbar-shrink");
			return;
		}
		$("nav.navbar").removeClass("navbar-shrink");
        
	};
    _bindAnchorLinks();
    _onhashchange();
    window.onhashchange = _onhashchange;
    window.onload = _bindAnchorLinks;
});

// Closes the Responsive Menu on Menu Item Click
$('.navbar-collapse ul li a:not(.dropdown-toggle)').click(function() {
    $('.navbar-toggle:visible').click();
});