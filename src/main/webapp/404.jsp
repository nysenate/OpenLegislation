<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.jdo.*" pageEncoding="UTF-8"%>
<%

String term = null;

String path = pageContext.getErrorData().getRequestURI();

System.out.println("404 called for: " + path);

term = path.substring(path.lastIndexOf("/")+1);

response.sendRedirect("/legislation/search/?term=" + java.net.URLEncoder.encode(term));


%>
