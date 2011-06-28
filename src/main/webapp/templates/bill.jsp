<%@ page language="java" import="java.util.Date, java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.bill.*,gov.nysenate.openleg.model.committee.*,gov.nysenate.openleg.model.calendar.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%!
	public String getVoterString(List<String> voters, String appPath) {
	 	StringBuffer buffer = new StringBuffer();
	 	buffer.append(wrapPerson(voters.get(0), appPath));
		for(int i = 1; i < voters.size(); i++) {
			buffer.append(", ").append(wrapPerson(voters.get(i), appPath));
		}
		return buffer.toString();
	}

	public String getSponsorString(List<Person> people, String appPath) {
		StringBuffer buffer = new StringBuffer();
	 	buffer.append(wrapPerson(people.get(0).getFullname(), appPath));
		for(int i = 1; i < people.size(); i++) {
			buffer.append(", ").append(wrapPerson(people.get(i).getFullname(), appPath));
		}
		return buffer.toString();
	}

	public String wrapPerson(String voter, String appPath) {
		return TextFormatter.append("<a href=\"", appPath, "/sponsor/", voter, "\" class=\"sublink\">", voter, "</a>");
	}
	
	public <T> ArrayList<T> defaultList(ArrayList<T> list) {
		if(list == null)
			return (ArrayList<T>) Collections.EMPTY_LIST;
		return list;
	}
%>
<%
	String appPath = request.getContextPath();

	Bill bill = (Bill)request.getAttribute("bill");
	
	ArrayList<Bill> rBills			= defaultList((ArrayList<Bill>)request.getAttribute("related-bill"));
	ArrayList<BillEvent> rActions	= defaultList((ArrayList<BillEvent>)request.getAttribute("related-action"));
	ArrayList<Meeting> rMeetings	= defaultList((ArrayList<Meeting>)request.getAttribute("related-meeting"));
	ArrayList<Calendar> rCals		= defaultList((ArrayList<Calendar>)request.getAttribute("related-calendar"));
	ArrayList<Vote> rVotes			= defaultList((ArrayList<Vote>)request.getAttribute("related-vote"));
	
	boolean active = bill.getActive();
  	
	String titleText = "(no title)";
	if (bill.getTitle()!=null)
		titleText = bill.getTitle();

	String senateBillNo = bill.getSenateBillNo();
	String year = null;
	
	SimpleDateFormat calendarSdf = new SimpleDateFormat("MMM d, yyyy");
	
	DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	
	String billSummary = bill.getSummary();
	String billMemo = bill.getMemo();
%>

<br/>
<h2>
	<%=senateBillNo%>: <%=bill.getTitle() == null ? "" : bill.getTitle() %>
</h2>
<br/>
    
<% if(!active) { %>
	<div class="amended">This bill has been amended.</div>
<% } %>
    
<div style="float:left;">
    
    <%	if (bill.getSameAs()!=null){ %>
			<b>Same as:</b>
		<% 
			StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
			String sameAs = null;
			String lastSameAs = "";
			String sameAsLink = null;
			Bill sameAsBill = null;
	
			while(st.hasMoreTokens()) {
				sameAs = st.nextToken().trim();
				sameAsLink = appPath + "/bill/" + sameAs + "-" + bill.getYear();
		
				if (sameAs.length() == 0)
					continue;
		
				if (sameAs.equals(lastSameAs))
					continue;
		
				lastSameAs = sameAs;
				%>
					<a href="<%=sameAsLink%>"><%=sameAs.toUpperCase() + "-" + bill.getYear()%></a>
				<%
				if (st.hasMoreTokens()) {%><%}
			} %>
			/
	<%	} 
    	
    	String sponsor = null;

		if (bill.getSponsor()!=null)
			sponsor = bill.getSponsor().getFullname();
		
		if (rBills.size() > 0) { 
			%>
				Versions: 
			<%
			for (Bill rBill:rBills) {
				%>
					<a href="/legislation/bill/<%=rBill.getSenateBillNo()%>"><%=rBill.getSenateBillNo()%></a> 
				<%
			}
		}

		if (sponsor == null)
			sponsor = "";
	%>
</div>

<div style="float:right;">
	<a href="<%=appPath%>/api/1.0/html-print/bill/<%=senateBillNo%>" target="_new">Print HTML Page</a> / 
	<a href="<%=appPath%>/api/1.0/lrs-print/bill/<%=senateBillNo%>" target="_new">Print Original Bill Format</a> / 
	<script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script> / 
	<a href="#discuss">Read or Leave Comments</a>
</div>

<br style="clear:both;"/>

<div id="content">

	<div class="billheader">
  		<%=billSummary == null ? "" : billSummary %>
		<hr/>
		<b>Sponsor: </b>
		<a href="<%=appPath%>/sponsor/<%=java.net.URLEncoder.encode(sponsor,"utf-8")%>"  class="sublink"><%=sponsor%></a> 

 
 		<% if(bill.getMultiSponsors() != null && bill.getMultiSponsors().size() > 0) { %>
			 / <b>Multi-sponsor(s):</b>
			<%= getSponsorString(bill.getMultiSponsors(), appPath) %>
 		<% } %>
 		
 		<% if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0) { %>
			 / <b>Co-sponsor(s):</b>
			<%= getSponsorString(bill.getCoSponsors(), appPath) %>
 		<% } %>
 		
		<% if (bill.getCurrentCommittee() != null) { %>
			 / <b>Committee:</b> <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a>
		<% } %>
		<br/>
		<%
			if (bill.getLawSection() != null) {
				%>
					<b>Law Section:</b> <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("lawsection:\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a>
	 			<%
	 		}
	
	 		if (bill.getLaw() != null) {
	 			%>
					 / <b>Law:</b> <%=bill.getLaw()%>
				<%
	 		}
	 	%>
	</div>
	
	<% if (rActions.size() > 0) { %>
		<h3><%=senateBillNo%> Actions</h3>
		<ul>
		<%
			ArrayList<BillEvent> events = BillCleaner.sortBillEvents(rActions);
			for (BillEvent be : events){	
				%>
					<li><%=df.format(be.getEventDate().getTime())%>: <%=BillCleaner.formatBillEvent(bill.getSenateBillNo(), be.getEventText(), appPath)%></li>
				<%
			}
		%>
		</ul>
	<% } %>

	<% if (rMeetings.size() > 0) { %>
		<h3><%=senateBillNo%> Meetings</h3>
		<%
			for (Iterator<Meeting> itMeetings = rMeetings.iterator(); itMeetings.hasNext();){
				Meeting meeting = itMeetings.next();
				%>
				<a href="<%=appPath%>/meeting/<%=meeting.luceneOid()%>" class="sublink"><%=meeting.luceneTitle()%></a><%if (itMeetings.hasNext()){%>,<%}
				
			}
		}
	%>
	
	<% 
		if (rCals.size() > 0) {
			%>
				<h3><%=senateBillNo%> Calendars</h3>
			<%
			for (Iterator<gov.nysenate.openleg.model.calendar.Calendar> itCals = rCals.iterator(); itCals.hasNext();) {
				gov.nysenate.openleg.model.calendar.Calendar cal = itCals.next();
				
				Supplemental sup = cal.getSupplementals().get(0);
		
				sup.setCalendar(cal);
				Date calDate = null;
				String type = "";
				if (cal.getType().equals("active")) {
					type = "Active List";
					calDate = sup.getSequence().getActCalDate();
				}
				else if (cal.getType().equals("floor")){
					type = "Floor Calendar";
					calDate = sup.getCalendarDate();
				}
		
				%>
					<a href="<%=appPath%>/calendar/<%=sup.luceneOid()%>" class="sublink"><%=type%><%=calDate == null ? "" : ": " +  calendarSdf.format(calDate)%></a>
				<%
				
				if (itCals.hasNext()) {
					%>
						, 
					<%
				}
			}
		}
	%>

	<%
		if(rVotes.size() > 0) {
			%>
				<h3><%=senateBillNo%> Votes</h3>
			<%
			
			for (Vote vote:rVotes) {
			   	String voteType = "Floor Vote";
			   	
				if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
					voteType = "Committee Vote";
		 		%>
		 		
				<div>
		  			<b>VOTE: <%=voteType.toUpperCase()%>:
		  			<% if(vote.getDescription() != null){ %>
		  				- <%=vote.getDescription()%>
		  			<% } %>
		 			 - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
		 			 
		  			<blockquote>
			  			<% if(vote.getAyes() != null && vote.getAyes().size() > 0) { %>
	 						<br/>
	 						<b>Ayes (<%=vote.getAyes().size()%>):</b>
		 					<%= getVoterString(vote.getAyes(), appPath) %>
			 			<% } %>
			 			<%if (vote.getAyeswr() != null && vote.getAyeswr().size() > 0) { %>
			 				<br/>
			 				<b>Ayes W/R (<%=vote.getAyeswr().size()%>):</b>
			 				<%= getVoterString(vote.getAyeswr(), appPath) %>
			 			<% } %>
				 		<%if (vote.getNays() != null && vote.getNays().size() > 0) { %>
				 			<br/>
				 			<b>Nays (<%=vote.getNays().size()%>):</b>
				 			<%= getVoterString(vote.getNays(), appPath) %>
			 			<% } %>
			 			<%if (vote.getAbstains()!=null && vote.getAbstains().size() > 0){ %>
			 				<br/>
			 				<b>Abstains (<%=vote.getAbstains().size()%>):</b>
			 				<%= getVoterString(vote.getAbstains(), appPath) %>
			 			<% } %>
			 			<%if (vote.getExcused()!=null && vote.getExcused().size() > 0){ %>
			 				<br/>
			 				<b>Excused (<%=vote.getExcused().size()%>):</b>
			 				<%= getVoterString(vote.getExcused(), appPath) %>
			 			<% } %>
		 			</blockquote>
		 		</div>
		 		<%
			}
		}
  	%>
	<% if(billMemo!=null && !billMemo.matches("\\s*")) { %>
		<h3><%=senateBillNo%> Memo</h3>
		<pre><%=billMemo%></pre>
	<% } %>

	<h3><%=senateBillNo%> Text</h3>
	<%
		if (bill.getFulltext()!=null) {
 
			String billText = TextFormatter.lrsPrinter(bill.getFulltext());
			billText = TextFormatter.removeBillLineNumbers (billText);
			%>
				<pre><%=billText %></pre>
		<% } else{ %>
			Not Available.
	<% } %>
	<br/>
</div>