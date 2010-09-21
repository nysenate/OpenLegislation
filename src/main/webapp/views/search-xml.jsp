<%@ page language="java" import="org.json.*,java.util.*,java.util.Map.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*"  contentType="text/html" pageEncoding="utf-8" %><%

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SearchResultSet srs = (SearchResultSet)request.getAttribute("results");
int resultCount = srs.getResults().size();

int total = srs.getTotalHitCount();

if (total < endIdx)
	endIdx = total;

%><?xml version="1.0" encoding="utf-8"?>
<results total="<%=total%>">
<%

Iterator<SearchResult> it = srs.getResults().iterator();
SearchResult sr = null;
String score = "";

String[] attribs = {"summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"};

while (it.hasNext())
{

		sr = it.next();
		
		score = sr.getScore() + "";
		
		
		%>
		<result type="<%=sr.getType() %>" id="<%=sr.getId()%>" score="<%=score%>" title="<%=TextFormatter.clean(sr.getTitle())%>" 
		<%for (int i = 0; i < attribs.length; i++){if (sr.getFields().get(attribs[i])!=null){ %> <%=attribs[i]%>="<%=TextFormatter.clean(sr.getFields().get(attribs[i]))%>"<%} }%>
		 />
		<%
		
		

}

%>
</results>