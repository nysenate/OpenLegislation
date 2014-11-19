<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Admin Menu"/>
<open-layout:body pageId="adminMenu">
    <div>
        <p>
            Welcome to the admin menu! <br>
            <a href="./legislation/logout"> Logout </a>
        </p>
    </div>
</open-layout:body>