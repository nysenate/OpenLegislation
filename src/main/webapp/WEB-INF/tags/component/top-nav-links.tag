<%@tag description="Top navigation menu links" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="ulClass" required="true" %>

<ul class="${ulClass}">
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/bills">Bills</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/agendas">Agendas</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/calendars">Calendars</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/laws">Laws</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/transcripts">Transcripts</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/members">Members</a>
    </li>
</ul>
