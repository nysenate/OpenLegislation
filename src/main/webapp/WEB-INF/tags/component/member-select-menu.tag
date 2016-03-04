<%@tag description="Senator/Assembly Member Select Menu Options" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="showSenators" required="true" %>
<%@ attribute name="showAssembly" required="true" %>

<option value="">Any</option>
<c:if test="${showSenators}">
  <optgroup label="Senators">
    <c:forEach var="m" items="${senatorList}">
      <option value="${m.memberId}">${m.getLastName()}, ${m.getFirstName()}</option>
    </c:forEach>
  </optgroup>
</c:if>
<c:if test="${showAssembly}">
  <optgroup label="Assembly Members">
    <c:forEach var="m" items="${assemblyMemList}">
      <option value="${m.memberId}">${m.getLastName()}, ${m.getFirstName()}</option>
    </c:forEach>
  </optgroup>
</c:if>