<%@ page language="java" import="gov.nysenate.services.model.*, java.util.*, org.apache.commons.lang.StringUtils" pageEncoding="UTF-8"%>
<%
    String appPath = request.getContextPath();
    @SuppressWarnings("unchecked")
    ArrayList<Committee> committees = (ArrayList<Committee>)request.getAttribute("committees");
%>
<jsp:include page="/header.jsp">
    <jsp:param name="title" value="Committees"/>
</jsp:include>
<style>
#committees {
    list-style-type: none;
}
.committee {
    padding: 3px;
    padding-top: 12px;
    border-bottom: 1px dashed #CCC;
    margin: 0px;
}
.committee:hover {
    background-color: #FFC;
}
.members {
    padding-left: 15px;
}
.members ul, .members li {
    list-style-type: none;
    display: inline;
}
.members ul li {
    margin-right: 5px;
}
.member-label {
    width:90px;
    font-size: 18px;
    color: #036;
    vertical-align:text-top;
    text-align: left;
}

td {
    text-indent: 0.0em;
}
</style>
<div id="content">
    <div class='formats'>
        <em>Committee Listings are available for: </em><a href="<%=appPath%>/committees/2015">2015</a> | <a href="<%=appPath%>/committees/2013">2013</a> | <a href="<%=appPath%>/committees/2011">2011</a>
    </div>
	<div class="content-bg">
        <h2 class='page-title'><%=request.getAttribute("sessionStart")%>-<%=request.getAttribute("sessionEnd")%> Standing Committees</h2>
        <div style="clear:both"></div>
	    <% if (committees.size()==0) { %>
            Committee Assignments for the current session are not yet available.
        <% } else { %>
	        <ul id="committees">
		        <% for(Committee committee:committees) { %>
		            <li class="committee">
		                <div class="committee-left">
		                    <h2><a href="<%=appPath%>/committee/<%=committee.getName()%>"><%=committee.getName()%></a></h2>
		                </div>
		                <div class="committee-right">
		                    <div class="members">
                            <% if (committee.getMembers().size()==0) { %>
                                Committee member information is not yet available.
                            <% } else { %>
		                        <table>
                                <%
                                if (committee.getChairs().size() > 0) {
                                    Member chair = committee.getChairs().get(0);
                                    %>
		                            <tr>
		                                <td class="member-label">Chair</td>
		                                <td><a href="<%=appPath%>/sponsor/<%=chair.getShortName()%>"><%=chair.getName() %></a></td>
		                            </tr>
	                            <% }
                                else {
                                    %>
                                    <tr>
                                        <td class="member-label">Chair</td>
                                        <td>Committee chair information is not yet available.</td>
                                    </tr>
                                <% }

                                ArrayList<String> links = new ArrayList<String>();
                                for(Member member:committee.getMembers()) {
                                    links.add("<a href=\""+appPath+"/sponsor/"+member.getShortName()+"\">"+member.getName()+"</a>");
                                } %>
		                            <tr>
		                                <td class="member-label">Members</td>
		                                <td><%=StringUtils.join(links, ", ")%></td>
		                            </tr>
		                        </table>
		                    <% } %>
		                    </div>
		                </div>
		            </li>
		        <% } %>
	        </ul>
        <% } %>
	</div>
</div>
<jsp:include page="/footer.jsp"/>
