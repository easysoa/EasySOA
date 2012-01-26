
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
    
    // Request
    jQuery.getJSON(url+'?callback=?',
        jsonParameters,
        function(responseData) {
           for(j=0; j<outputFields.length; j++){
                var fieldName = outputFields[j].name;
                outputFields[j].value = eval("responseData.Body." + responseMessage + "." + fieldName);
           }
        }
    )
    .error(function() {
       for(j=0; j<outputFields.length; j++){
            var fieldName = outputFields[j].name;
            outputFields[j].value = "Error.";
       }
    });

}
