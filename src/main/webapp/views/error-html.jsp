<%@ page language="java" import="java.util.*, java.io.*" pageEncoding="UTF-8"%>
<%
String msg = (String)request.getAttribute("err");
%>
<jsp:include page="/header.jsp"/>


 <div id="content">

<h3>An error occured</h3>

<%=msg%>
	<div style="height:200px">&nbsp;</div>

 </div>
   
 <jsp:include page="/footer.jsp"/>
   
    