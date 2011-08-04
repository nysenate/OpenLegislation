<%@ page language="java" import="org.json.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*"%><%
String contentType = (String) request.getAttribute("contentType");
response.setContentType(contentType == null ? "text/html" : contentType);

SenateResponse sr = (SenateResponse)request.getAttribute("results");
int resultCount = sr.getResults().size();

int total = (Integer)sr.getMetadataByKey("totalresults");

org.json.JSONStringer js = new org.json.JSONStringer();
	
String[] attribs = {"summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"};

JSONWriter mainObj = js.array();

Iterator<Result> it = sr.getResults().iterator();
Result r = null;

while (it.hasNext())
{

	JSONWriter locObj = mainObj.object();

	try
	{
	
		r = it.next();
		
			
		locObj.key("type");
		locObj.value(r.getOtype());
		
		locObj.key("id");
		locObj.value(r.getOid());
	
										
		locObj.key("title");
		locObj.value(r.getTitle());
		
		
		for (int i = 0; i < attribs.length; i++){
			if (r.getFields().get(attribs[i])!=null){ 
				locObj.key(attribs[i]);
				locObj.value(r.getFields().get(attribs[i]));
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