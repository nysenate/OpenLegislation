<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, org.apache.commons.lang3.StringUtils, java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String appPath = request.getContextPath();
	DateFormat df = new SimpleDateFormat("MMM d, yyyy - h:mm a");

	Meeting meeting = (Meeting) request.getAttribute("meeting");
	String chair = meeting.getCommitteeChair();

	//Temp Hack
	String calNo = "";
	Addendum addendum = null;
	if (meeting.getAddendums().size() > 0) {
	    addendum = meeting.getAddendums().get(meeting.getAddendums().size() - 1);
	    calNo = addendum.getAgenda().getNumber()+"";
	}
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
 					<% if(calNo != null && !calNo.trim().isEmpty()) { %>
 					<li><span class="meta">Agenda: </span><span class="metadata">
           				<%=calNo%>  
 					</span></li>
 					<% } %>
 					<li><span class="meta">Chair: </span><span class="metadata">
           				<a href="<%=appPath%>/search/?term=chair:<%=java.net.URLEncoder.encode("\""+meeting.getCommitteeChair() + "\"", OpenLegConstants.ENCODING)%>">${meeting.committeeChair}</a>
 					</span></li>
 					<% if(meeting.getLocation() != null) { %>
 					<li><span class="meta">Location: </span><span class="metadata">
           				${meeting.location} 
					</span></li>
					<% } %>
					
 					<% if (addendum != null) { %>
 					<li><span class="meta">Addendum: </span><span class="metadata">
           				${addendum.addendumId}
 					</span></li>
 					<li><span class="meta">Published: </span><span class="metadata">
           				${addendum.publicationDateTime}
 					</span></li>
 					<li><span class="meta">Published in Week of: </span><span class="metadata">
           				${addendum.weekOf}
 					</span></li>
 					<% } %>
 					
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
 	          	<div class="summary">${meeting.notes}</div>
	       <% } %>
 	    <h3 class="section" ><a id="BillsontheAgenda" href="#BillsontheAgenda" class="anchor ui-icon ui-icon-link"></a> Bills on the Agenda</h3>
	    <% if(meeting.getBills().isEmpty()) { %>
            <div class="summary">No bills listed.</div>
       <% } else {
			Iterator<Bill> itBills = meeting.getBills().iterator();
			Bill bill = null;
			while (itBills.hasNext()) {
				bill = itBills.next();
				try {
					request.setAttribute("bill",bill); %>
					<div class="row">
						<a href="/legislation/bill/${bill.senateBillNo}">${bill.senateBillNo}: ${bill.title}</a>
               			 <br/>
               			 <span class="subrow">
                         <% if (bill.getSponsor()!=null) {
                                if (bill.getOtherSponsors().isEmpty()) { %>
                                    Sponsor: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
                                <% } else { %>
                                    Sponsors: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
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
					 
			    <% } catch (Exception e) {
					System.err.println("couldn't render bill: " + bill.getSenateBillNo());
				}
			}
		}%>
</div>
</div>
