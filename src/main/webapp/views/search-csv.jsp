<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/plain" pageEncoding="utf-8" %><%

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

if (total < endIdx)
	endIdx = total;

ArrayList<String> attribs = new ArrayList<String>(Arrays.asList("billno", "chair", "committee", "cosponsors", "location", "sameas", "session-type", "sponsor", "status", "summary", "when", "year"));

Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;

ArrayList<String> columns = new ArrayList<String>();
columns.addAll(Arrays.asList("type", "id", "score", "title"));

ArrayList<String> rows = new ArrayList<String>();

while (it.hasNext()) {
	
	try {		
		sr = it.next();
		
		String tuple = "";
		tuple += sr.getType() 
					+ "," + sr.getId().replaceAll(",","") 
					+ "," + sr.getScore() 
					+ "," + sr.getTitle().replaceAll(",","");
		
		sr.getFields().remove("type");
		
		for(int i = 4; i < columns.size(); i++) {
			String key = columns.get(i);
			if(sr.getFields().get(key) != null) {
				tuple += "," + sr.getFields().get(key).replaceAll(",","");
			}
			else {
				
				tuple += ",";
			}
			sr.getFields().remove(key);
		}
		
		for(String key:sr.getFields().keySet()) {
			columns.add(key);
			tuple += "," + sr.getFields().get(key);
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