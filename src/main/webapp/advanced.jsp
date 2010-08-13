<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>

<jsp:include page="header.jsp"/>
<%
//nothing
%>
 <div id="content">
 Sorry no results were found. <a href="index.jsp">Please try again.</a>
 <br/><br/>
 <div style="background:#eee;width:100%"><form method="get" action="/openleg/search.jsp">
		Search by bill number, sponsor, committee or keyword:
		<input type="text" name="term" value=""/>
		
		
		<input type="submit" value="go"/>
	</form>
	</div>
 </div>
   
 <jsp:include page="footer.jsp"/>
