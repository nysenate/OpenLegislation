<%@ page language="java" import="org.json.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*"  contentType="text/html" pageEncoding="utf-8" %><%

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

if (total < endIdx)
	endIdx = total;


org.json.JSONStringer js = new org.json.JSONStringer();
	
String[] attribs = {"summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"};

JSONWriter mainObj = js.array();

Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;

while (it.hasNext())
{

	JSONWriter locObj = mainObj.object();

	try
	{
	
		sr = it.next();
		
			
		locObj.key("type");
		locObj.value(sr.getType());
		
		locObj.key("id");
		locObj.value(sr.getId());
		
		locObj.key("score");
		locObj.value(sr.getScore()+"");
	
										
		locObj.key("title");
		locObj.value(sr.getTitle());
		
		
		for (int i = 0; i < attribs.length; i++){
			if (sr.getFields().get(attribs[i])!=null){ 
				locObj.key(attribs[i]);
				locObj.value(sr.getFields().get(attribs[i]));
			} 
		}
	
	}
	catch (Exception e)
	{
		//error with this bill
	}
	
	
	locObj.endObject();

}

mainObj.endArray();
%><%=mainObj.toString()%>