<%@tag description="Top navigation menu links" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeLink" required="true" %>
<%@ attribute name="ulClass" required="true" %>

<ul class="${ulClass}">
    <li class="<c:if test="${activeLink == 'about'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/about">About</a></li>
    <li class="<c:if test="${activeLink == 'content'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/content">Content</a></li>
    <li class="hide-for-medium-down <c:if test="${activeLink == 'report'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/report">Reports</a></li>
    <li class="<c:if test="${activeLink == 'updates'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/updates">Updates</a></li>
    <li class="<c:if test="${activeLink == 'analytics'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/analytics">Analytics</a></li>
    <li class="<c:if test="${activeLink == 'documentation'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/documentation">Api Docs</a></li>
</ul>
