$(document).ready(function() {
			
	var serviceList = $("#serviceList");
	
	displayError = function(data) {
		data = "Error: "+JSON.stringify(data);
		serviceList.append('<tr><td style="color: darkred">'+data+'</td></tr>');
		$("#loading").hide();
	};
	
	makeRow = function(content) {
		return '<tr><td>'+content+'</td></tr>';
	};
	
	makeLink = function(title, url) {
		return '<a href="'+url+'">'+title+'</a>';
	};
	
	jQuery.ajax({
		url: '/light/serviceList',
		success: function (data, textStatus, jqXHR) {
			$("#loading").hide();
			var result = $.parseJSON(jqXHR.responseText);
			if (result.success) {
				if (result.data.length > 0) {
					for (service in result.data) {
						rowContent = makeLink(result.data[service].title, result.data[service].lightUrl);
						rowContent += ' <span style="font-size: 10pt; color: #555">('+result.data[service].url+')</span>';
						serviceList.append(makeRow(rowContent));
					}
				}
				else {
					serviceList.append(makeRow('No services known. Why not look for some services using the <a href="/easysoa/core/dbb/index.html">Discovery by browsing</a> tool?'));
				}
			}
			else {
				displayError(result.data);
			}
		},
		error: displayError
	});

});
