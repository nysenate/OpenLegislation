$(document).ready(function(){

	getJson("/legislation/report.json",doParsing);

	function wrapNumber(num) {
		return "<span class=\"number\">" + num + "</span>";
	}

	function doParsing(json) {
		var startDate = formatDate(json.startDate)
		var endDate = formatDate(json.endDate);

		$('#head').html("<h1>OpenLegislation Data Report (" + startDate + " to " + endDate + ")</h1>");

		var html="";
		for(x in json.typeReports) {
			var type = json.typeReports[x].type;
			var total = json.typeReports[x].total;
			var occurred = json.typeReports[x].occurred;
			var updated = json.typeReports[x].updated;

			html += "<h3>" + 
				(occurred != 0 
						? wrapNumber(occurred) + " " + type + "s occurred last week, " + wrapNumber(updated)
								: wrapNumber(updated) + " " + type + "s")
						+ " were updated and there is a total of " + wrapNumber(total);
		}
		$('#numbers').html("<div class=\"wrapper\">" + html + "</div>");

		html="";
		for(x in json.senatorBills) {
			var senator = x;
			var bills = json.senatorBills[x];

			html += "<div class=\"box\" onclick=\"window.open('/legislation/sponsor/" + senator.replace(/ /g,"+") + "?filter=oid:s*','_blank');\">";
			html +=		"<div class=\"boxLabel\">";
			html +=			senator;
			html +=		"</div>";
			html +=		"<div class=\"boxData\">";
			html +=			bills;
			html +=		"</div>";
			html += "</div>";
		}
		$('#senators').html("<div class=\"wrapper\">" + html + "</div><br clear=\"all\">");

		html="";
		for(x in json.committeeBills) {
			var committee = x;
			var bills = json.committeeBills[x];
			html += "<div class=\"box\" onclick=\"window.open('/legislation/committee/" + committee.replace(/ /g,"+") + "','_blank');\">";
			html +=		"<div class=\"boxLabel\">";
			html +=			committee;
			html +=		"</div>";
			html +=		"<div class=\"boxData\">";
			html +=			bills;
			html +=		"</div>";
			html += "</div>";
		}
		$('#committees').html("<div class=\"wrapper\">" + html + "</div><br clear=\"all\">");

		html = "";
		for(x in json.reportedBills) {
			var bill = json.reportedBills[x].bill;
			var heat = json.reportedBills[x].heat;
			var modified = json.reportedBills[x].modified;
			var lastActionDate = json.reportedBills[x].lastActionDate;
			var missingFields = json.reportedBills[x].missingFields;

			var missing = "";
			for(y in missingFields) {
				if(missing != "") {
					missing +=  ", ";
				}
				missing += missingFields[y];
			}
		
			html += "<div class=\"problemBill\" style=\"background:" 
									+ getColor(heat) 
									+ "\" onclick=\"window.open('/legislation/bill/" + bill + "','_blank');\">";
			html += 	"<div class=\"problemBillNumber\">";
			html +=			"<div class=\"billNumber\">";
			html +=				bill;
			html +=				"<div class=\"modified\">";
			html +=					"Modified on " + formatDate(modified);
			html +=				"</div>";
			html +=			"</div>";
			html += 	"</div>";
			html += 	"<div class=\"problemBillText\">";
			html +=			"<div class=\"billData\">";
			html +=				"Missing: " + missing;
			html +=			"</div>";
			html +=			"<div class=\"billData\">";
			html +=				"Last Action date: " + (lastActionDate == -1 ? "unavailable" : formatDate(lastActionDate));
			html +=			"</div>";
			html += 	"</div>";
			html += "</div>";
		}
		$('#bills').html("<div class=\"wrapper\">" + html + "</div>");
	}

	function getColor(heat) {
		if(heat == 10) {
			return "#ee2200";
		}
		if(heat > 8) {
			return "#ff4433"
		}
		if(heat > 6) {
			return "#ff8877";
		}
		if(heat > 4) {
			return "#ffbbaa";
		}
		else {
			return "#ffeedd";
		}
	}

	function formatDate(ms) {
		var date = new Date(ms);
		var month = date.getMonth();
		month++;
		
		return month + "/" + date.getDate() + "/" + date.getFullYear();
	};

	function getJson(url, callback) {
		$.getJSON(url, function(data) {
			callback(data);
		});
	}
});