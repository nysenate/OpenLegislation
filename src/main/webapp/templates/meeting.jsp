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

<h2 class='page-title'>Meeting details for <%=meeting.getCommitteeName()%></h2>
<div class="content-bg">
	<div class="title-block">
		<div class='item-actions'>
			<ul>
				<li><a href="#" onclick="window.print(); return false;">Print
						Page</a></li>
				<li><script type="text/javascript"
						src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
			</ul>
		</div>
		<h3 class='item-title'>${meeting.committeeName} - <%=df.format(meeting.getMeetingDateTime())%></h3>
    </div>
    
    <div id="subcontent">
		<div>
		<div>
           <span class="meta">Agenda:</span> 
           <%=calNo%>  
        </div>             
		<div>
           <span class="meta">Chair:</span> 
           <a href="<%=appPath%>/search/?term=chair:<%=java.net.URLEncoder.encode("\""+meeting.getCommitteeChair() + "\"", OpenLegConstants.ENCODING)%>">${meeting.committeeChair}</a>
        </div>  
        <% if(meeting.getLocation() != null) { %>
        <div>
           <span class="meta">Location:</span> 
           ${meeting.location} 
        </div>  
       <% } %>
       <% if (addendum != null) { %>
        <div>
           <span class="meta">Addendum:</span> 
           ${addendum.addendumId}
        </div>  
  		<div>
           <span class="meta">Published:</span> 
           ${addendum.publicationDateTime}
        </div>  
	    <div>
           <span class="meta">Published in Week of:</span> 
           ${addendum.weekOf}
        </div>  
	    <% } %>
	    </div>
     
         
	       <% if (meeting.getNotes() != null && meeting.getNotes().trim().length() > 0) { %>
	           <h3 id="section">Notes</h3>
	           ${meeting.notes}
	       <% } %>
 	    <h3 id="section">Bills on the Agenda</h3>
	    <% if(meeting.getBills().isEmpty()) { %>
            No bills listed.
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
	<div id="formatBox"><b>Formats:</b>
		<a href="${appPath}/api/1.0/json/meeting/<%=meeting.luceneOid()%>">JSON</a>
		<a href="${appPath}/api/1.0/xml/meeting/<%=meeting.luceneOid()%>">XML</a>
	</div>
</div>