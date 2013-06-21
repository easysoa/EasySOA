var myMessages = [ 'info', 'warning', 'error', 'success' ];
var current;

function createApplication() {
	$.post("/rest/createApplication", $("#create").serialize());
}

function changeValue(input, value) {
	$(input).val(value);
}

function centerThis(element) {

	var windowHeight = $(window).height();
	var windowWidth = $(window).width();
	var elementHeight = $(element).height();
	var elementWidth = $(element).width();

	var elementTop, elementLeft;

	if (windowHeight <= elementHeight) {
		elementTop = $(window).scrollTop();
	} else {
		elementTop = ((windowHeight - elementHeight) / 2)
				+ $(window).scrollTop();
	}

	if (windowWidth <= elementWidth) {
		elementLeft = $(window).scrollLeft();
	} else {
		elementLeft = ((windowWidth - elementWidth) / 2)
				+ $(window).scrollLeft();
	}

	$(element).css({
		'top' : elementTop,
		'left' : elementLeft
	});
}

function hideAllMessages() {
	var messagesHeights = new Array(); // this array will store height for each

	for (i = 0; i < myMessages.length; i++) {
		if(myMessages[i] != current){
			$('#' + myMessages[i] + "-message").css('display', 'none'); 
		}
	}
}

function showMessage(type,title,message) {
	current = type;
	$('#' + type + '-message > h3').html(title);
	$('#' + type + '-message > p').html(message);
	$('#' + type + '-message').css("display","block");
	$('#' + type + '-message').click(function() {
		hideMessage(type);
	});
}

function hideMessage(type){
	$('#' + type + "-message").css('display', 'none'); 
}


