exports.config = {
	
	port: 5000,
	
	basePath: "..",

	excludedFiles: [
	  'jsqa', // JSQA
	  'js/node_modules', 'js/lib', // Server libs
	  'www/easysoa/lib', // Client libs
	  'www/easysoa/core/dbb/js/bookmarklet/bookmarklet-min.js' // Packed files
	  ],
	  
	jshintOptions : {
	   'ignoreTabsAndSpaceMix': 'true' // See http://www.jshint.com/options/ for more
	}

};
