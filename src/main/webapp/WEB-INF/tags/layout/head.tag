<%@tag description="Head template" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" required="true" description="The title of the page" %>
<%@ tag import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ tag import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>
<%
  String refTypeMap = SpotCheckRefType.getRefJsonMap();
  String refTypeDisplayMap = SpotCheckRefType.getDisplayJsonMap();
  String refTypeContentTypeMap = SpotCheckRefType.getRefContentTypeJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
%>
<!doctype html>
<html id="ng-app" class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title}</title>
    <link rel="shortcut icon" type="image/png" href="${ctxPath}/static/favicon.ico"/>

    <!-- The base is needed for angular HTML5 routing -->
    <base href="<%= (request.getContextPath().isEmpty()) ? "/" : request.getContextPath() %>">

    <!-- Favicon -->
    <link rel="shortcut icon" type="image/x-icon" href="${ctxPath}/static/favicon.ico"/>

    <!-- Third Party CSS -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/dest/lib.min.css"/>

    <!-- Main CSS -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/dest/openleg.min.css"/>

    <!-- Third Party JS -->
    <script type="application/javascript" src="<%= request.getContextPath()%>/static/js/dest/vendor.min.js"></script>

    <!-- GA -->
    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', '${gaTrackingId}', 'auto');
        ga('send', 'pageview');

    </script>

    <script>
        window.ctxPath = "<%= request.getContextPath() %>";
        window.apiPath = window.ctxPath + "/api/3";
        window.adminApiPath = window.apiPath + "/admin";
        window.referenceTypeMap = <%= refTypeMap %>;
        window.referenceTypeDisplayMap = <%= refTypeDisplayMap %>;
        window.referenceContentTypeMap = <%= refTypeContentTypeMap %>;
        window.mismatchMap = <%= mismatchMap %>;
    </script>

    <!-- Page specific css and pre-load js can be added below by the consumer -->
    <jsp:doBody/>
</head>