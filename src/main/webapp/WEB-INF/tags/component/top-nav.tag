<%@tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeLink" required="true" %>

<div class="fixed">
    <nav class="top-bar" data-topbar>
        <ul class="title-area">
            <li class="name">
                <h1>
                    <img src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                    <a style="display:inline-block" href="#"><span style='color:#008cba'>Open </span> Legislation</a>
                </h1>
            </li>
        </ul>
        <section class="top-bar-section">
            <!-- Left Nav Section -->
            <ul class="left">
                <li class="<c:if test="${activeLink == 'content'}"><c:out value="active"/></c:if>">
                    <a target="_self" href="<%= request.getContextPath() %>/content">Content</a></li>
                <li class="<c:if test="${activeLink == 'report'}"><c:out value="active"/></c:if>">
                    <a target="_self" href="<%= request.getContextPath() %>/report">Reports</a></li>
                <li class="<c:if test="${activeLink == 'updates'}"><c:out value="active"/></c:if>">
                    <a target="_self" href="<%= request.getContextPath() %>/updates">Updates</a></li>
                <li class="<c:if test="${activeLink == 'analytics'}"><c:out value="active"/></c:if>">
                    <a target="_self" href="<%= request.getContextPath() %>/analytics">Analytics</a></li>
                <li class="<c:if test="${activeLink == 'documentation'}"><c:out value="active"/></c:if>">
                    <a target="_self" href="<%= request.getContextPath() %>/documentation">Documentation</a></li>

            </ul>
        </section>
    </nav>
</div>