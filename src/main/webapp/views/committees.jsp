<%@ page language="java" import="gov.nysenate.services.model.*, java.util.ArrayList" pageEncoding="UTF-8"%>
<%
    String appPath = request.getContextPath();
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

<em>Committee Listings are available for: </em><a href="<%=appPath%>/committees/2013">2013</a> | <a href="<%=appPath%>/committees/2011">2011</a>
<div id="content">
    <h2><%=request.getAttribute("sessionStart")%>-<%=request.getAttribute("sessionEnd")%> Committee Listings</h2>
    <h3>Standing Committees</h3>
    <ul id="committees">
        <% if (committees.size()==0) { %>
            Committee Assignments for the current session are not yet available.
        <% } else { %>
        <% for(Committee committee:committees) { %>
            <li class="committee"">
                <div class="committee-left">
                    <h2><a href="<%=appPath%>/committee/<%=committee.getName()%>"><%=committee.getName()%></a></h2>
                </div>
                <div class="committee-right">
                    <div class="members">
                        <table>
                        <% if (committee.getMembers().size()==0) { %>
                            Committee member information is not yet available.
                        <% } else { %>
                            <tr>
                                <td class="member-label">Chair</td>
                                <td>
                                <ul>
                                    <% for(Member member:committee.getChairs()) { %>
                                        <li>
                                            <a href="<%=appPath%>/sponsor/<%=member.getShortName()%>"><%=member.getName() %></a>,
                                        </li>
                                    <% } %>
                                </ul>
                                </td>
                            </tr>
                            <tr>
                                <td class="member-label">Members</td>
                                <td>
                                <ul>
                                    <% for(Member member:committee.getMembers()) { %>
                                        <li>
                                            <a href="<%=appPath%>/sponsor/<%=member.getShortName()%>"><%=member.getName() %></a>,
                                        </li>
                                    <% } %>
                                </ul>
                                </td>
                            </tr>
                            <% } %>
                        </table>
                    </div>
                </div>
            </li>
        <% } %>
        <% } %>
    </ul>
</div> 

<jsp:include page="/footer.jsp"/>
