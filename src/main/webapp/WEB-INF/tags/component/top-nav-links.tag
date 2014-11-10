<%@tag description="Top navigation menu links" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="activeLink" required="true" %>
<%@ attribute name="ulClass" required="true" %>

<ul class="${ulClass}">
    <li class="<c:if test="${activeLink == 'bills'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/bills">Bills</a></li>
    <li class="<c:if test="${activeLink == 'agendas'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/agendas">Agendas</a></li>
    <li class="hide-for-medium-down <c:if test="${activeLink == 'calendars'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/calendars">Calendars</a></li>
    <li class="<c:if test="${activeLink == 'laws'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/laws">Laws</a></li>
    <li class="<c:if test="${activeLink == 'transcripts'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/transcripts">Transcripts</a></li>
    <li class="<c:if test="${activeLink == 'members'}"><c:out value="active"/></c:if>">
    <a target="_self" href="<%= request.getContextPath() %>/members">Members</a></li>
</ul>
