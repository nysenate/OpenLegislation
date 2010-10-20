<%@ page language="java" import="org.codehaus.jackson.*,org.codehaus.jackson.map.*,javax.jdo.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.xstream.*" contentType="text/html" pageEncoding="utf-8"%><%String appPath = request.getContextPath();
String term = (String)request.getAttribute("term");
String type = (String)request.getAttribute("type");
String format = "json";
String pageIdx = (String)request.getAttribute("pageIdx");
String pageSize = (String)request.getAttribute("pageSize");

SenateResponse senResponse = (SenateResponse)request.getAttribute("results");

senResponse.getMetadata();

for (Result result: senResponse.getResults())
{
	ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	String rData = result.getData();
	rData = rData.substring(rData.indexOf(":")+1);
	rData = rData.substring(0,rData.lastIndexOf("}"));
	Object senObject = mapper.readValue(rData, Transcript.class);
	request.setAttribute(type, senObject);
	String senObjectPath = "/views/" + type + "-" + format + ".jsp";
	%>
	<jsp:include page="<%=senObjectPath%>"></jsp:include>
	<%
}

%>
