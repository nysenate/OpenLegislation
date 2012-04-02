<%@ page language="java" import="gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.serialize.*" contentType="text/html" pageEncoding="utf-8"%><%


Calendar calendar = (Calendar)request.getAttribute("calendar");
 %>
 <%=OriginalApiConverter.doXml(calendar)%>