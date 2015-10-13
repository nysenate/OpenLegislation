<%@tag description="Body Template" pageEncoding="UTF-8"%>
<%@attribute name="appName" required="true" description="Module that defines the angular app" type="java.lang.String" %>

<body ng-app="${appName}">
<div id="wrapper">
    <jsp:doBody/>

