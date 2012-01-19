/** Compress me as is to bookmarklet-min.js with UglifyJS (http://marijnhaverbeke.nl/uglifyjs) */

function loadJS(url) {
	var newScript = document.createElement("script");
	newScript.type = "text/javascript";
	newScript.src = url;
	document.getElementsByTagName("head")[0].appendChild(newScript);
}

loadJS("http://localhost:8083/easysoa/core/js/bookmarklet/discovery.js");

setTimeout(function() {
	if (!window.EASYSOA_WEB) {
		alert("Error: EasySOA seems unreachable");
	}
}, 500);

void(0);
