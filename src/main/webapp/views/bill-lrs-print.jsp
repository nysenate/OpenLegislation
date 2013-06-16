<%@ page language="java" import="java.util.regex.Matcher,java.util.regex.Pattern,java.util.*,java.text.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%
    String appPath = request.getContextPath();
    Bill bill = (Bill)request.getAttribute("bill");
%>
<html>
    <head>
        <title><%=bill.getSenateBillNo()%> Text - NY Senate Open Legislation</title>
        <link rel="stylesheet" type="text/css" media="screen" href="<%=appPath%>/style-print.css"/>
    </head>
    <body>
        <div id="content">
            <pre><%=TextFormatter.originalTextPrintable(bill)%></pre>
        </div>
        <script type="text/javascript">
            setTimeout("window.print();",2000);
        </script>
    </body>
</html>
