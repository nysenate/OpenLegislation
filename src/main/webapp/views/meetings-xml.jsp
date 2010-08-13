<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.model.committee.*,javax.xml.bind.*" contentType="text/xml" pageEncoding="utf-8"%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="cache"%><%

String cacheKey = (String)request.getAttribute("path");
int cacheTime = OpenLegConstants.DEFAULT_CACHE_TIME;
 
%><cache:cache key="<%=cacheKey%>" time="<%=cacheTime %>"  scope="application"><%
 
CachedContentManager.fillCache(request);

Committee committee = (Committee)request.getAttribute("meetings");

JAXBContext context = JAXBContext.newInstance(Committee.class);

Marshaller m = context.createMarshaller();
m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
m.marshal(committee, response.getOutputStream());

 %>
 </cache:cache>