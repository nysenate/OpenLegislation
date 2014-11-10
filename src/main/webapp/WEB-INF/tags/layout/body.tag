<%@tag description="Body Template" pageEncoding="UTF-8"%>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>
<%@ attribute name="pageId" required="true" description="Identifier for the page" %>

<body>
<div id="wrapper">
    <open-component:top-nav activeLink="${pageId}"/>
    <section id="content">
        <jsp:doBody/>
    </section>
