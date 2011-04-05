<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/plain" pageEncoding="utf-8" %><%

response.setContentType("text/csv");
response.setHeader("Content-disposition","attachment;filename=search-" + new Date().getTime() +".csv");

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SenateResponse sr = (SenateResponse)request.getAttribute("results");
int resultCount = sr.getResults().size();

int total = (Integer)sr.getMetadataByKey("totalresults");

if (total < endIdx)
	endIdx = total;

ArrayList<String> attribs = new ArrayList<String>(Arrays.asList("billno", "chair", "committee", "cosponsors", "location", "sameas", "session-type", "sponsor", "status", "summary", "when", "year"));

Iterator<Result> it = sr.getResults().iterator();
Result r = null;

ArrayList<String> columns = new ArrayList<String>();
columns.addAll(Arrays.asList("type", "id", "title"));

ArrayList<String> rows = new ArrayList<String>();

while (it.hasNext()) {
	
	try {		
		r = it.next();
		
		String tuple = "";
		tuple += r.getOtype() 
					+ ",\"" + r.getOid().replaceAll("\"","\"\"") + "\""
					+ ",\"" + r.getTitle().replaceAll("\"","\"\"") + "\"";
		
		r.getFields().remove("type");
		
		for(int i = 4; i < columns.size(); i++) {
			String key = columns.get(i);
			if(r.getFields().get(key) != null) {
				tuple += ",\"" + r.getFields().get(key).replaceAll("\"","\"\"") + "\"";
			}
			else {
				
				tuple += ",";
			}
			r.getFields().remove(key);
		}
		
		for(String key:r.getFields().keySet()) {
			columns.add(key);
			tuple += ",\"" + r.getFields().get(key).replaceAll("\"","\"\"") + "\"";
		}
		rows.add(tuple);
	}
	catch (Exception e) {
		//error with this document
	}
}

String header = "";
for(String column:columns) {
	header += column + ",";
}
header = header.replaceAll(",$","");

out.print(header + "\n");

for(String row:rows) {
	out.println(row);
}

%>