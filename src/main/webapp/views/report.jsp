<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Date, java.util.ArrayList,gov.nysenate.openleg.model.Report,java.sql.*" %>
<%
String appPath = request.getContextPath();
@SuppressWarnings("unchecked")
ArrayList<Report> report= (ArrayList<Report>)request.getAttribute("reportList");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Open Legislation Error Reports</title>

    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="YES"/>

    <link rel="shortcut icon" href="<%=appPath%>/static/img/nys_favicon_0.ico" type="image/x-icon" />
    <link rel="alternate" type="application/rss+xml" title="RSS 2.0" href="<%=appPath%>/feed" />

    <link href="<%=request.getContextPath()%>/static/css/bootstrap.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/css/bootstrap-responsive.css" rel="stylesheet">

    <script type="text/javascript" src="<%=appPath%>/static/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/bootstrap-2.3.1.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/search.js"></script>
    <script type="text/javascript" src="<%=appPath%>/static/js/diff.js"></script>
    
    <style>
    #errorTable {
        width:1000px;
        margin: 0px auto;
    }
    </style>
</head>
<body>
    
    <table class="table table-hover" id="errorTable" >
        <tr><td><h3>Error Reports</h3></td></tr>
        <% for(Report r:report){
            Date date=r.getDate();
            %>
            <tr class="span12" style="background-color:white">
	            <td class="span12">
	                <a href="<%=request.getContextPath() %>/ErrorLog?id=<%=r.getId() %>">ERROR REPORT: <%=date%></a>
	            </td>
            </tr>
        <% } %>   
    </table>
</body>
</html>