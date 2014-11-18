<%@tag description="Top navigation menu links" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="ulClass" required="true" %>

<ul class="${ulClass}">
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/admin/stats">Stats</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/admin/report">Reports</a>
    </li>
    <li>
        <a target="_self" href="<%= request.getContextPath() %>/admin/manage">Manage Users</a>
    </li>
</ul>
