<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,org.json.*,gov.nysenate.openleg.model.*"  pageEncoding="utf-8" contentType="text/plain"%><%

CachedContentManager.fillCache(request);

Transcript transcript = (Transcript)request.getAttribute("transcript");

org.json.JSONStringer js = new org.json.JSONStringer();
	
JSONWriter mainObj = js.array();

	JSONWriter locObj = mainObj.object();

	try
	{
	
		locObj.key("id");
		locObj.value(transcript.getId());
										
		locObj.key("timestamp");
		locObj.value(transcript.getTimeStamp());
		
		locObj.key("location");
		locObj.value(transcript.getLocation());
		
		locObj.key("session");
		locObj.value(transcript.getType());
		
		locObj.key("text");
		locObj.value(transcript.getTranscriptText());
	}
	catch (Exception e)
	{
		//error with this bill
	}
	
	
	locObj.endObject();


mainObj.endArray();
%><%=mainObj.toString()%>