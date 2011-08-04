<%@ page language="java" import="javax.xml.namespace.QName,gov.nysenate.openleg.util.SessionYear,gov.nysenate.openleg.util.OpenLegConstants,org.apache.abdera.model.Entry,org.apache.abdera.Abdera,org.apache.abdera.model.Feed,org.json.*,java.util.*,java.util.Map.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/xml" pageEncoding="utf-8" %><%
	String responseContentType = (String) request.getAttribute("contentType");
	response.setContentType(responseContentType == null ? "text/html" : responseContentType);

	String[] attribs = {"billno","sponsor","cosponsors","notes","when","sameas","committee","status","location","session-type","chair","summary"};
	
	SenateResponse sr = (SenateResponse)request.getAttribute("results");
	int resultCount = sr.getResults().size();
	
	int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
	int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));
	
	int total = (Integer)sr.getMetadataByKey("totalresults");
		
	String term = (String)request.getAttribute("term");
	
	String base = "http://directory.nysenate.gov";
	String appPath = request.getContextPath();
	String uri = appPath + "/api/atom/search/" + java.net.URLEncoder.encode(term, "utf8");
	
	String url = base + uri;
	
	Abdera abdera = new Abdera();
	Feed feed = abdera.newFeed();
	
	feed.setId("tag:" + base + "," + SessionYear.getSessionYear() + ":" + uri);
	feed.setTitle(term + " - Search - NY Senate Open Legislation");
	
	feed.setUpdated(new Date());
	feed.addLink(base);
	feed.addLink(url, "self");
	
	if(pageIdx > 1) {
		feed.addLink(url + "/" + (pageIdx-1), "previous");
	}
	if((pageIdx * pageSize) < total) {
		feed.addLink(url + "/" + (pageIdx+1), "next");
	}
	
	for(Result r:sr.getResults()) {
		Entry entry = feed.addEntry();
		
		String contentType = r.getOtype();
		String contentId = r.getOid();
		String resultTitle = r.getTitle();
                               
		if (contentType.equals("vote")) {
			contentType = "bill";
			contentId = (String)r.getFields().get("billno");
		}

		if (contentType.equals("action")) {
			contentType = "bill";
			contentId = (String)r.getFields().get("billno");
		}
               
		if (contentType.equals("transcript")) {
			contentType = "transcript";
		}
                
		if (contentType.equals("bill")) {
			resultTitle = contentType.toUpperCase() + " - " + contentId + " - " + resultTitle;
		}
		else {
			resultTitle = contentType.toUpperCase() + " - " + resultTitle;
		}
             
		String resultUri = appPath + "/" + contentType + "/" + contentId;
		
		StringBuilder resultSummary = new StringBuilder();
           
		for (int i = 0; i < attribs.length; i++){
			if (r.getFields().get(attribs[i])!=null){ 
				resultSummary.append(attribs[i].trim());
				resultSummary.append(":");
				resultSummary.append(r.getFields().get(attribs[i]).trim());
				resultSummary.append("; ");
			} 
		}
              
              
		if (r.getSummary() != null && r.getSummary().trim().length()>0) {
			resultSummary.append("Summary:");
			resultSummary.append(r.getSummary().trim());
			resultSummary.append(";");
		}
		
		entry.setId("tag:" + base + "," + SessionYear.getSessionYear() + ":" + resultUri);
		entry.setTitle(resultTitle);
		entry.setSummary(resultSummary.toString().replaceAll(";$",""));
		entry.setUpdated(new Date(r.getLastModified()));
		entry.setEdited(new Date(r.getLastModified()));
		entry.addLink(base + resultUri);
		entry.complete();
	}
	
	out.print(abdera.getWriter().write(feed));
%>