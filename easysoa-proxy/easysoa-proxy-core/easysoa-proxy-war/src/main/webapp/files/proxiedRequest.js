
// Common javascript stuff
function getElementsByClass(searchClass, node, tag) {
    var classElements = new Array();
    if ( node == null )
        node = document;
    if ( tag == null )
        tag = '*';
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
    for (i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
            classElements[j] = els[i];
            j++;
        }
    }
    return classElements;
}

function submitForm(form, url, wsdlUrl, serviceName, binding, operation, responseMessage, outputFields) {
	
    // Preparing the request
    // For each form field : add it in the url
    var jsonParams = "";
    for(i=0; i < document.forms[form].elements.length; i++){
        // we only takes fields marked as enabled
        if(document.forms[form].elements[i].disabled == false && document.forms[form].elements[i].type != "submit"){
            if(jsonParams == ""){
                jsonParams = jsonParams + "{\"paramName\":\"" + document.forms[form].elements[i].name + "\",\"paramValue\":\"" + document.forms[form].elements[i].value +"\"}";
            } else {
                jsonParams = jsonParams + ", {\"paramName\":\"" + document.forms[form].elements[i].name + "\",\"paramValue\":\"" + document.forms[form].elements[i].value +"\"}";
            }
        }
    }
    
    // Build JSON Data structure
    var jsonParameters = "request={\"wsRequest\": {";
    jsonParameters = jsonParameters + " \"service\": \"" + serviceName + "\", \"binding\": \"" + binding + "\", \"operation\": \"" + operation + "\",";
    jsonParameters = jsonParameters + " \"wsdlUrl\": \"" + wsdlUrl + "\"";
    jsonParameters = jsonParameters + "},";
    jsonParameters = jsonParameters + "\"formParameters\":[";
    jsonParameters = jsonParameters + jsonParams;
    jsonParameters = jsonParameters + "]}";
    
    // Temporary message
    for(j=0; j<outputFields.length; j++){
         outputFields[j].value = "Sending request...";
    }
    
    // Setting error function
    jQuery.ajaxSetup({
            error: function(jqXHR, exception) {
            	// TODO : hard coded error message, must be returned by the fuse intent
            	// The returned status code is 0 when the request is blocked by the fuse intent
            	// Maybe because the intent throws only an exception and not an error response 
            	if (jqXHR.status == 0) {
                    //alert('Not connect.\n Verify Network.');
                    alert("[AUTOREARMFUSE INTENT] Too much requests detected for the time period, this resquest has been blocked !");
                } else if (jqXHR.status == 404) {
                    alert('Requested page not found. [404]');
                } else if (jqXHR.status == 500) {
                    //alert('Internal Server Error [500].');
                	alert("[AUTOREARMFUSE INTENT] Too much requests detected for the time period, this resquest has been blocked !");
                } else if (exception === 'parsererror') {
                    alert('Requested JSON parse failed.');
                } else if (exception === 'timeout') {
                    alert('Time out error.');
                } else if (exception === 'abort') {
                    alert('Ajax request aborted.');
                } else {
                    alert('Uncaught Error.\n' + jqXHR.responseText);
                }
       			for(j=0; j<outputFields.length; j++){
       				var fieldName = outputFields[j].name;
       				outputFields[j].value = "";
       			}
            }
        });
    
    // Request
    //jQuery.getJSON(url+'?callback=?',
    jQuery.get(url+'?callback=?',
        jsonParameters,
        function(responseData, textStatus, jqXHR) {
    		var cleanedResponse = responseData.substr(2, responseData.length-4);
    		//alert(cleanedResponse);
    		var jsonResponse = JSON.parse(cleanedResponse);
   			for(j=0; j<outputFields.length; j++){
   				var fieldName = outputFields[j].name;
   				outputFields[j].value = eval("jsonResponse.Body." + responseMessage + "." + fieldName);
   			}
        }
    );
    /*.error(function(status, xhr) {
    	alert("There is a problem, the service is down");
    	for(j=0; j<outputFields.length; j++){
            var fieldName = outputFields[j].name;
            outputFields[j].value = "Error -- " + xhr.responseText;
       }
    });*/
   
}
