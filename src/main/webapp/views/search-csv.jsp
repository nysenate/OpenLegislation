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

	
String[] attribs = {"summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"};


Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;

out.println("type,id,score,title,other");

while (it.hasNext())
{

	try
	{
	
		sr = it.next();
		
		String tuple = "";
		tuple += sr.getType() + "," + sr.getId() + "," + sr.getScore() + "," + sr.getTitle().replaceAll(",","");		
		
		for (int i = 0; i < attribs.length; i++){
			if (sr.getFields().get(attribs[i])!=null){ 
				tuple += ", " + attribs[i] + ": " + sr.getFields().get(attribs[i]).replaceAll(",","");
			} 
		}
		
		out.println(tuple);
	
	}
	catch (Exception e)
	{
		//error with this bill
	}
	
	
}

%>