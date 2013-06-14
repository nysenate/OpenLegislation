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
<h2 class='page-title'>Meeting details for <%=meeting.getCommitteeName()%></h2>
<div class="content-bg">
	<h3 class='item-title'>${meeting.committeeName} - <%=df.format(meeting.getMeetingDateTime())%></h3>
    <div style="float: right">
	   <script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script>
    </div>
    <div id="subcontent">
		<div>
			<b>Agenda:</b> <%=calNo%> / <b>Chair:</b>
			
			<a href="<%=appPath%>/search/?term=chair:<%=java.net.URLEncoder.encode("\""+meeting.getCommitteeChair() + "\"", OpenLegConstants.ENCODING)%>">${meeting.committeeChair}</a>
			<% if(meeting.getLocation() != null) { %>
				/ <b>Location:</b> ${meeting.location}
			<% } %>
	        <% if (addendum != null) { %>
	            <b>Addendum:</b> ${addendum.addendumId} / <b>Published:</b> ${addendum.publicationDateTime} / <b>Week of:</b> ${addendum.weekOf}
	        <% } %>
	    </div>
        <div id="committeeNotes">
	       <% if (meeting.getNotes() != null && meeting.getNotes().trim().length() > 0) { %>
	           <h3>Notes</h3>
	           ${meeting.notes}
	       <% } %>
    </div>
	<div>
	    <h3>Bills on the Agenda</h3>
	    <% if(meeting.getBills().isEmpty()) { %>
            No bills listed.
       <% } else {
			Iterator<Bill> itBills = meeting.getBills().iterator();
			Bill bill = null;
			while (itBills.hasNext()) {
				bill = itBills.next();
				try {
					request.setAttribute("bill",bill); %>
					<div class="billSummary" onmouseover="this.style.backgroundColor='#FFFFCC'" onmouseout="this.style.backgroundColor='#FFFFFF'" onclick="location.href='/legislation/bill/${bill.senateBillNo}'">
						<a href="/legislation/bill/${bill.senateBillNo}">${bill.senateBillNo}: ${bill.title}</a>
						<div style="font-size:90%;color:#777777;">
                            <% if (bill.getSponsor()!=null) {
                                if (bill.getOtherSponsors().isEmpty()) { %>
                                    Sponsor: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
                                <% } else { %>
                                    Sponsors: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
                                <% } %>
                            <% } %>
                        
                            ${bill.actClause}

                            <ul>
							<% if (bill.getVotes()!=null && bill.getVotes().size()>0) {
								Iterator<Vote> itVotes = bill.getVotes().iterator();
								while (itVotes.hasNext()) {
									Vote vote = itVotes.next();
									if (vote.getVoteType()==Vote.VOTE_TYPE_COMMITTEE && vote.getVoteDate().equals(meeting.getMeetingDateTime())) {
										request.setAttribute("vote",vote);

										%><li>Vote: Committee
										<%if (vote.getDescription()!=null){ %>(<%=vote.getDescription()%>)<%} %>
										<%=df.format(vote.getVoteDate())%>:

										<%if (vote.getAyes()!=null) { %> <%=vote.getAyes().size()%> Ayes <% } %>
										<%if (vote.getAyeswr()!=null){ %> / <%=vote.getAyeswr().size()%> Ayes W/R <% } %>
										<%if (vote.getNays()!=null){ %> / <%=vote.getNays().size()%> Nays <% } %>
										<%if (vote.getAbstains()!=null) { %> / <%=vote.getAbstains().size()%> Abstains<%} %>
										<%if (vote.getExcused()!=null) { %> / <%=vote.getExcused().size()%> Excused<%} %>
										</li><%
									}
								}
							} %>
							</ul>
						</div>
					</div>
			    <% } catch (Exception e) {
					System.err.println("couldn't render bill: " + bill.getSenateBillNo());
				}
			}
		}%>
	</div>
	<div id="formatBox"><b>Formats:</b>
		<a href="${appPath}/api/1.0/json/meeting/<%=meeting.luceneOid()%>">JSON</a>
		<a href="${appPath}/api/1.0/xml/meeting/<%=meeting.luceneOid()%>">XML</a>
	</div>
</div>
</div>
