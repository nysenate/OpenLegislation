<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, org.apache.commons.lang3.StringUtils, java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	DateFormat df = new SimpleDateFormat("MMM d, yyyy - h:mm a");

	Meeting meeting = (Meeting) request.getAttribute("meeting");
	String chair = meeting.getCommitteeChair();
%>
<div id="content">
<div class="content-bg">
	<h2 class='page-title'>Meeting details for <%=meeting.getCommitteeName()%></h2>
	<div class="item-meta">
        <div id="subcontent" class="emptytitle">
       		<div class="billmeta">
                <ul>
                    <li><span class="meta">Date: </span><span class="metadata">
                          <%=df.format(meeting.getMeetingDateTime())%>
				    </span></li>
 					<li><span class="meta">Chair: </span><span class="metadata">
           				<a href="<%=JSPHelper.getLink(request, "/search/?term=chair:"+java.net.URLEncoder.encode("\""+meeting.getCommitteeChair() + "\"", OpenLegConstants.ENCODING))%>">${meeting.committeeChair}</a>
 					</span></li>
 					<% if(meeting.getLocation() != null) { %>
	 					<li><span class="meta">Location: </span><span class="metadata">
	           				${meeting.location} 
						</span></li>
					<% } %>
				
				    <li><span class="meta">Published: </span><span class="metadata">
                        ${meeting.publishDate}
                    </span></li>
		        </ul>  
 			</div>
 			<div class='item-actions'>
				<ul>
	        		<li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
					<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
				</ul>
			</div>
        </div>
		<% if (meeting.getNotes() != null && meeting.getNotes().trim().length() > 0) { %>
               <h3 class="section" ><a id="Notes" href="#Notes" class="anchor ui-icon ui-icon-link"></a> Notes</h3>
		    <pre class="meeting-notes">${meeting.notes}</pre>
		<% } %>
	        <h3 class="section" ><a id="BillsontheAgenda" href="#BillsontheAgenda" class="anchor ui-icon ui-icon-link"></a> Bills on the Agenda</h3>
	    <% if(meeting.getBills().isEmpty()) { %>
            <div class="summary">No bills listed.</div>
        <% } else {
			Iterator<Bill> itBills = meeting.getBills().iterator();
			Bill bill = null;
			while (itBills.hasNext()) {
				bill = itBills.next();
					request.setAttribute("bill",bill); %>
					<div class="row">
						<a href="/legislation/bill/${bill.billId}">${bill.billId}: ${bill.title}</a>
               			 <br/>
               			 <span class="subrow">
                         <% if (bill.getSponsor()!=null) {
                                if (bill.getOtherSponsors().isEmpty()) { %>
                                    Sponsor: <%=JSPHelper.getSponsorLinks(bill, request) %>
                                <% } else { %>
                                    Sponsors: <%=JSPHelper.getSponsorLinks(bill, request) %>
                                <% } %>
                            <% } %>
                        	<br/>
                            ${bill.actClause}

                            <br/>
							<% if (bill.getVotes()!=null && bill.getVotes().size()>0) {
								Iterator<Vote> itVotes = bill.getVotes().iterator();
								while (itVotes.hasNext()) {
									Vote vote = itVotes.next();
									if (vote.getVoteType()==Vote.VOTE_TYPE_COMMITTEE && vote.getVoteDate().equals(meeting.getMeetingDateTime())) {
										request.setAttribute("vote",vote);

										%>Vote: Committee
										<%if (vote.getDescription()!=null){ %>(<%=vote.getDescription()%>)<%} %>
										<%=df.format(vote.getVoteDate())%>:

										<%if (vote.getAyes()!=null) { %> <%=vote.getAyes().size()%> Ayes <% } %>
										<%if (vote.getAyeswr()!=null){ %> / <%=vote.getAyeswr().size()%> Ayes W/R <% } %>
										<%if (vote.getNays()!=null){ %> / <%=vote.getNays().size()%> Nays <% } %>
										<%if (vote.getAbstains()!=null) { %> / <%=vote.getAbstains().size()%> Abstains<%} %>
										<%if (vote.getExcused()!=null) { %> / <%=vote.getExcused().size()%> Excused<%} %>
										<br/><%
									}
								}
							} %>
							</span>
						</div>
					 
			    <%
					 			    			}
					 			    		}
					 			    %>
    </div>
</div>
</div>
