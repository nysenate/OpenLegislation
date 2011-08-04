<%@ page language="java" import="org.json.*,java.util.*,java.util.Map.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.util.*"  contentType="text/html" pageEncoding="utf-8" %><%

String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

int pageIdx = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_IDX));
int pageSize = Integer.parseInt((String)request.getAttribute(OpenLegConstants.PAGE_SIZE));

int startIdx = (pageIdx - 1) * pageSize;
int endIdx = startIdx + pageSize;

SenateResponse sr = (SenateResponse)request.getAttribute("results");
int resultCount = sr.getResults().size();

int total = (Integer)sr.getMetadataByKey("totalresults");

if (total < endIdx)
	endIdx = total;

%><?xml version="1.0" encoding="utf-8"?>
<results total="<%=total%>">
<%

Iterator<Result> it = sr.getResults().iterator();
Result r = null;

String[] attribs = {"summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"};

while (it.hasNext())
{

		r = it.next();
		
		String title = r.getTitle();
		if (title == null)
			title = "";
		%>
		<result type="<%=r.getOtype() %>" id="<%=r.getOid()%>" title="<%=TextFormatter.clean(title)%>" 
		<%for (int i = 0; i < attribs.length; i++){if (r.getFields().get(attribs[i])!=null){ %> <%=attribs[i]%>="<%=TextFormatter.clean(r.getFields().get(attribs[i]))%>"<%} }%>
		 />
		<%
		
		

}

%>
</results>