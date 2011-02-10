<%@ page language="java" import="gov.nysenate.openleg.model.calendar.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%><%


Calendar calendar = (Calendar)request.getAttribute("calendar");
 %>
 <%=OriginalApiConverter.doXml(calendar)%>