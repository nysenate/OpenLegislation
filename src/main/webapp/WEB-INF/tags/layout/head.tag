<%@tag description="Head template" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" required="true" description="The title of the page" %>

<!doctype html>
<html id="ng-app" ng-app="open" class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${title}</title>
    <!-- Main CSS -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/app.css"/>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/icons.css"/>
    <!-- Modernizr -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/modernizr/modernizr.js"></script>
    <!-- JQuery -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/jquery/dist/jquery.min.js"></script>
    <!-- Foundation -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/foundation/js/foundation.min.js"></script>
    <!-- Angular JS -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/angular/angular.min.js"></script>
    <!-- Angular Routes -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/angular-route/angular-route.min.js"></script>
    <!-- Angular Resource -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/angular-resource/angular-resource.min.js"></script>
    <!-- Angular Foundation -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/angular-foundation/mm-foundation-tpls.min.js"></script>
    <!-- ngTable -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/ng-table/ng-table.js/"></script>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/bower_components/ng-table/ng-table.min.css"/>
    <!-- Moment -->
    <script type="application/javascript" src="<%= request.getContextPath() %>/static/bower_components/moment/min/moment.min.js"></script>

    <script>
        window.ctxPath = "<%= request.getContextPath() %>";
        window.apiPath = window.ctxPath + "/api/3";
        $(document).foundation();
    </script>

    <!-- Page specific css and pre-load js can be added below by the consumer -->
    <jsp:doBody/>
</head>