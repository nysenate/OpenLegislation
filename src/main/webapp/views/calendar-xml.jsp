<%@ page language="java" import="java.util.Iterator,java.io.ByteArrayOutputStream,java.util.Collection,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache" %><%

String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
 
%><cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>"  scope="application"><%

CachedContentManager.fillCache(request);
Calendar calendar = (Calendar)request.getAttribute("calendar");

JAXBContext context = JAXBContext.newInstance(Calendar.class);

Marshaller m = context.createMarshaller();
m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

ByteArrayOutputStream baos = new ByteArrayOutputStream();
m.marshal(calendar, baos);

 %>
 <%=baos.toString()%></cache:cache>