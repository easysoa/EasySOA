/*
 * jQuery Frontend script
 */
 
var obj = null;

$(document).ready(function() {
	$('#main_menu #root > li').hover(function() {
		$(this).find('ul').css('display', 'block');
	},
	  function () {
		$(this).find('ul').css('display', 'none');
	  }
	);
	
	$("#main_menu #root a.level1").hover(function() {
		alert($(this).style.display)
		$(this).find('.menu_over').css('visibility', 'visible');
		$(this).find('.menu_out').css('visibility', 'hidden');
	}, function() {
		$(this).find('.menu_over').css('visibility', 'hidden');
		$(this).find('.menu_out').css('visibility', 'visible');
	});
});