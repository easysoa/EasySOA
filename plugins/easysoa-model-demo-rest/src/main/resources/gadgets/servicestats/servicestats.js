/**
 * Dataset exemple:
 *
 *  {
 *  "services":
 *  [
 *    {
 *      "id": "655ac3a0-3599-46a8-8992-11bcf491c84c",
 *      "title": "BillService",
 *      "callcount": 50
 *    },
 *    {
 *      "id": "2a367cdf-ad4a-4a92-b6cf-aa13f2d77aca",
 *      "title": "TipService",
 *      "callcount": 15
 *    }
 *  ]}
 */

function renderStart() {
	return "<table>" +
			"<tr><td>Service</td><td>Nombre d'appels</td></tr>";
}

function renderRow(serviceData) {
	var rowHtml = "<tr><td>";
	
	rowHtml += "<a href=\"#\" onclick=\"viewDocument(" + serviceData.id + ")\">";
	rowHtml += serviceData.title;
	rowHtml += "</td><td>";
	rowHtml += serviceData.callcount;
	rowHtml += "</td></tr>";
	
	return rowHtml;
}

function renderEnd() {
	return "<table>";
}

function render(data, tag) {

	var htmlResult = "<table>";
	for (var i = 0; i < data.services; i++) {
		htmlResult += renderRow(data.services[i]);
	}
	htmlResult += "</table>";

	tag.html(htmlResult);
}