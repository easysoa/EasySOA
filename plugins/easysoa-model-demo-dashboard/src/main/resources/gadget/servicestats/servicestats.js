/**
 * -----------------------
 * Service stats rendering
 * -----------------------
 * Dataset exemple:
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

// HTML formatting

function formatStart() {
    var html = "";
    html += "<table class='dataList'>";
    html += "  <thead>";
    html += "    <tr>";
    html += "      <th>Service</th>";
    html += "      <th>Callcount</th>";
    html += "    </tr>";
    html += "  </thead>";
    html += "  <tbody>";
    return html;
}

function formatRow(serviceData, i) {
    var html = "<tr class=\"";
    if (i%2 == 0){
    	html += "dataRowEven";
    } else {
    	html += "dataRowOdd";
    }
    html += "\">";
    html += "  <td>";
    html += "    <a href=\"" + top.nxBaseUrl + "nxdoc/default/" + serviceData.id + "/view_documents\" target=\"_top\">";
    html +=        serviceData.title;
    html += "    </a>";
    html += "  </td>";
    html += "  <td>";
    html +=        serviceData.callcount;
    html += "  </td>";
    html += "</tr>";
	return html;
}

function formatEnd() {
    var html = "";
    html += "  </tbody>";
    html += "</table>";
	return html;
}

// Entry point

function renderStats(data, tag) {
	var htmlResult = formatStart();
	for (var i = 0; i < data.services.length; i++) {
		htmlResult += formatRow(data.services[i], i);
	}
	htmlResult += formatEnd();
	_gel("documentList").innerHTML = htmlResult;
}