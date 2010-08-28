<%@ page language="java" import="javax.jdo.*,java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.xstream.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %>
<%

String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
 
String appPath = request.getContextPath();

String term = (String)request.getAttribute("term");
String type = (String)request.getAttribute("type");
String format = (String)request.getAttribute("format");

%>
 <cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>" scope="application">
<%

out.write(XStreamBuilder.writeResponse(format,new SearchEngine2().search(term,format,0,1,null,false)));

%>

</cache:cache>
