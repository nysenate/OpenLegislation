<%@tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeLink" required="true" %>

<div class="fixed">
    <nav class="top-bar" data-topbar>
        <ul class="title-area">
            <li class="name">
                <h1>
                    <img src="./static/img/NYSS_seal.jpg"/>
                    <a style="display:inline-block" href="#">OPEN | <span style='color:#008cba'>2.0</span></a>
                </h1>
            </li>
        </ul>
        <section class="top-bar-section">
            <!-- Left Nav Section -->
            <ul class="left">
                <li class="<c:if test="${activeLink == 'content'}"><c:out value="active"/></c:if>">
                    <a href="<%= request.getContextPath() %>/content">Content</a></li>
                <li class="<c:if test="${activeLink == 'report'}"><c:out value="active"/></c:if>">
                    <a href="<%= request.getContextPath() %>/report">Reports</a></li>
                <li><a class="" href="#">Updates</a></li>
                <li><a class="" href="#">Analytics</a></li>
                <li><a class="" href="#">Tools</a></li>
            </ul>
        </section>
    </nav>
</div>