<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Date, java.util.ArrayList,gov.nysenate.openleg.model.admin.Report,gov.nysenate.openleg.model.admin.ReportObservation,gov.nysenate.openleg.util.JSPHelper" %>
<!DOCTYPE html>
<html>
<head>
    <title><%=request.getParameter("title")%></title>

    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="YES"/>

    <link rel="shortcut icon" href="<%=JSPHelper.getLink(request, "/static/img/nys_favicon_0.ico")%>" type="image/x-icon" />

    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/vendor/jquery-1.10.2/jquery.min.js")%>"></script>
    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/vendor/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js")%>"></script>
    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/vendor/DataTables-1.9.4/js/jquery.dataTables.min.js")%>"></script>
    <script type="text/javascript" src="<%=JSPHelper.getLink(request, "/static/js/app.js")%>"></script>
    
    <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/static/vendor/Normalize-2.1.2/normalize.css")%>"/>
    <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/static/vendor/jquery-ui-1.10.3/themes/base/minified/jquery-ui.min.css")%>" />
    <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/static/vendor/DataTables-1.9.4/css/jquery.dataTables.css")%>" />
    <link rel="stylesheet" type="text/css" href="<%=JSPHelper.getLink(request, "/static/css/admin-style.css")%>" />
</head>
<body>
    <div id="menu">
        <div class="logo">OpenLeg Admin</div>
        <ul>
            <li class="<%=(request.getServletPath().startsWith("/admin/report") ? "menu-active" : "menu-inactive")%>"><a href="<%=JSPHelper.getLink(request, "/admin/reports/")%>">Reports</a></li>
            <li class="<%=(request.getServletPath().startsWith("/admin/update") ? "menu-active" : "menu-inactive")%>"><a href="<%=JSPHelper.getLink(request, "/admin/updates/")%>">Updates</a></li>
        </ul>
        <div style="clear:both"></div>
    </div>
    <div style="clear:both"></div>