<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.regex.*, java.util.Hashtable, java.util.TreeSet, java.util.HashMap, java.util.Date, java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!
    public <T> ArrayList<T> defaultList(ArrayList<T> list) {
		if(list == null)
			return (ArrayList<T>) Collections.EMPTY_LIST;
		return list;
	}
	
	public String formatBillEvent(String bill, String event, String appPath) {
		if(event.matches("(?i).*(amended|print number).*")) {
				//event.contains("AMENDED") || event.contains("PRINT NUMBER")) {
			Pattern p = Pattern.compile("^(.*?)(\\d{2,5}\\w?)(.*?)$");
			Matcher m = p.matcher(event);
			
			if(m.find()) {
				return TextFormatter.append(m.group(1), 
					"<a href=\"", 
						appPath, 
						"/bill/", 
						bill.substring(0, 1),  m.group(2),  "-", bill.split("-")[1], 
					"\">", 
					m.group(2), 
					"</a>", m.group(3));
			}
		}
		else if(event.matches("(?i).*substituted.*")) {
				//event.contains("SUBSTITUTED")) {
			Pattern p = Pattern.compile("^(.*?)(\\w\\d{2,5}\\w?)(.*?)$");
			Matcher m = p.matcher(event);
			
			if(m.find()) {
				return TextFormatter.append(m.group(1), 
					"<a href=\"", 
						appPath, 
						"/bill/", 
						m.group(2) + "-" + bill.split("-")[1], 
					"\">", 
					m.group(2), 
					"</a>", m.group(3));
			}
		}
		return event;
	}
	
	public ArrayList<Action> sortBillEvents(List<Action> billEvents) {
		Hashtable<String, Action> table = new Hashtable<String, Action>();
		TreeSet<Action> set = new TreeSet<Action>(new Action.ByEventDate());
		
		for(Action be:billEvents) {
			if(table.contains(be)) continue;
			
			table.put(Long.toString(be.getDate().getTime()), be);
			set.add(be);
		}
		
		table.clear();
		
		return new ArrayList<Action>(set);
	}%>
	<%
	String appPath = request.getContextPath();

	Bill bill = (Bill)request.getAttribute("bill");

	ArrayList<Bill> rBills			= defaultList((ArrayList<Bill>)request.getAttribute("related-bill"));
	ArrayList<Action> rActions	= defaultList((ArrayList<Action>)request.getAttribute("related-action"));
	ArrayList<Meeting> rMeetings	= defaultList((ArrayList<Meeting>)request.getAttribute("related-meeting"));
	ArrayList<Calendar> rCals		= defaultList((ArrayList<Calendar>)request.getAttribute("related-calendar"));
	ArrayList<Vote> rVotes			= defaultList((ArrayList<Vote>)request.getAttribute("related-vote"));

	String titleText = "(no title)";
	if (bill.getTitle()!=null)
		titleText = bill.getTitle();

	String senateBillNo = bill.getSenateBillNo();
	String year = null;
	
	SimpleDateFormat calendarSdf = new SimpleDateFormat("MMM d, yyyy");
	
	DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	
	String billMemo = bill.getMemo().replace("-\n", "").replace("\n\n", "<br/><br/>").replace("\n", " ");
%>
<div id="content">
    <div class="content-bg">
        <div class="page-title">
            <% if (bill.getActive() == false) { %>
               <span class="amended">This bill has been amended</span>
            <% } %>
            <h2>
	        <% if (bill.isResolution()) { %>
	            Resolution ${bill.senateBillNo}
	        <% } else { %>
	            Bill ${bill.senateBillNo}
	        <% } %>
	        </h2>
        </div>

		<div class="title-block">
			<div class='item-actions'>
				<ul>
                    <li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
                    <li><a href="<%=appPath%>/api/1.0/lrs-print/bill/<%=bill.getSenateBillNo()%>" class="hidemobile" target="_new">Print Original Text</a></li>
                    <li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
                    <li><a href="#Comments">Read or Leave Comments</a></li>
				</ul>
			</div>
		
		<h3 class='item-title'>${bill.title}</h3>
	   	<% if (bill.getTitle()+"." != bill.getSummary()) { %>
	   	<div class="summary"><p>${bill.summary}</p></div>
	   	<% } %>
	</div>

    <h3  class="section" ><a id="BillDetails" href="#BillDetails" class="anchor ui-icon ui-icon-link"></a>Details</h3>
    <div class="item-meta">
        <div id="subcontent billmeta">
	       <div class="billmeta">
	       		<ul>
	       		
                <% if (bill.getSameAs() != null && !bill.getSameAs().trim().isEmpty()) { %>
	       				<li><span class="meta">Same as:</span><span class="metadata">
                        <%
						StringTokenizer st = new StringTokenizer(bill.getSameAs(),",");
						String sameAs = null;
						String lastSameAs = "";
						String sameAsLink = null;
						Bill sameAsBill = null;
			
						while(st.hasMoreTokens()) {
							sameAs = st.nextToken().trim().toUpperCase();
							sameAsLink = appPath + "/bill/" + sameAs;
					        %><a href="<%=sameAsLink%>"><%=sameAs%></a><%
						}
					%></span></li><%
			    }

                if (rBills.size() > 0) { %>
	       				<li><span class="meta">Versions</span><span class="metadata">
                         <% for (Bill rBill:rBills) { %>
				           <a href="/legislation/bill/<%=rBill.getSenateBillNo()%>"><%=rBill.getSenateBillNo()%></a> 
				        <% } %>
 					</span></li><%
				}
                
                %>
                <li>
                    <% if (bill.getOtherSponsors().isEmpty()) { %>
                        <span class="meta">Sponsor:</span><span class="metadata"><%=JSPHelper.getSponsorLinks(bill, appPath) %></span>
                    <% } else { %>
                        <span class="meta">Sponsors:</span><span class="metadata"><%=JSPHelper.getSponsorLinks(bill, appPath) %></span>
                    <% }
                    %>
                    </li>
                        <%
                    if(bill.getMultiSponsors() != null && bill.getMultiSponsors().size() > 0) { %>
                    <li>
                        <span class="meta">Multi-sponsor(s):</span>
                        <span class="metadata"><%=JSPHelper.getMultiSponsorLinks(bill, appPath)%>
                    </li><%
                }
       
                if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0) { %>
                    <li>
                        <span class="meta">Co-sponsor(s):</span>
                        <span class="metadata"><%=JSPHelper.getCoSponsorLinks(bill, appPath)%></span>
                    </li><%
                }

                if (bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) { %>
                    <li>
                        <span class="meta">Committee:</span>
                        <span class="metadata"><a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a></span>
                    </li>
                <% }

                if (bill.getLawSection() != null && !bill.getLawSection().equals("")) { %>
                    <li>
                        <span class="meta">Law Section:</span> <span class="metadata"><a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("lawsection:\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a>
                    </span></li>
	 			<% }
	 				 				
		 		if (bill.getLaw() != null && bill.getLaw() != "") { %>
                    <li>
                        <span class="meta">Law:</span> <span class="metadata"><%=bill.getLaw()%></span>
                    </li>
				<% } %>
				</ul>
            </div>
            <% if (rActions.size() > 0) { %>
                <h3 class="section"> <a id="Actions" href="#Actions" class="anchor ui-icon ui-icon-link"></a> Actions</h3>
                <div class="section-list"><ul>
                <%
                ArrayList<Action> events = sortBillEvents(rActions);
                for (Action be : events) { %>
					<li><%=df.format(be.getDate().getTime())%>: <%=formatBillEvent(bill.getSenateBillNo(), be.getText(), appPath)%></li>
				<% } %>
                </ul></div>
            <% } %>

	<% if (rMeetings.size() > 0) { %>
		<h3  class="section" ><a id="Meetings" href="#Meetings" class="anchor ui-icon ui-icon-link"></a> Meetings</h3>
		<div class="section-list"><ul>
		<%
			for (Iterator<Meeting> itMeetings = rMeetings.iterator(); itMeetings.hasNext();){
				Meeting meeting = itMeetings.next();
				Date meetingDate = meeting.getMeetingDateTime();
				%>
				<li><a href="<%=appPath%>/meeting/<%=meeting.luceneOid()%>" class="sublink"><%=meeting.getCommitteeName() + (meetingDate == null ? "" : ": " + calendarSdf.format(meetingDate))%></a></li>
		<% } %>
       </ul></div>
      <% } %>
	
	<% 
		if (rCals.size() > 0) {
			%>
			<h3  class="section" ><a id="Calendars" href="#Calendars" class="anchor ui-icon ui-icon-link"></a> Calendars</h3>
			<div class="section-list"><ul>
			
			<%
			for (Iterator<Calendar> itCals = rCals.iterator(); itCals.hasNext();) {
				Calendar cal = itCals.next();
				
				Supplemental sup = cal.getSupplementals().get(0);
		
				sup.setCalendar(cal);
				Date calDate = null;
				String type = "";
				if (cal.getType().equals("active")) {
					type = "Active List";
					
					if (sup.getSequences() != null && sup.getSequences().size() != 0) {
						
						calDate = sup.getSequences().get(0).getActCalDate();
					}
				}
				else if (cal.getType().equals("floor")){
					type = "Floor Calendar";
					calDate = sup.getCalendarDate();
				}
		
				%>
					<li><a href="<%=appPath%>/calendar/<%=cal.luceneOid()%>" class="sublink"><%=type%><%=calDate == null ? "" : ": " +  calendarSdf.format(calDate)%></a></li>
				<% 
			}
			%>
          </ul></div>
        <% } %>

	<%
		if(rVotes.size() > 0) {
			%>
				<h3 class="section" ><a id="Votes" href="#Votes" class="anchor ui-icon ui-icon-link"></a> Votes</h3>
			<%
			
			for (Vote vote:rVotes) {
			   	String voteType = "Floor Vote";
			   	
				if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
					voteType = "Committee Vote";
		 		%>
		 		
				<div class="votes">
		  			<b>VOTE: <%=voteType.toUpperCase()%>:
                    <% if(vote.getDescription() != null && !vote.getDescription().isEmpty()){ %>
		  				- <%=vote.getDescription()%>
		  			<% } %>
		 			 - <%=DateFormat.getDateInstance(DateFormat.MEDIUM).format(vote.getVoteDate())%></b>
		 			 
		  			<blockquote class="vote-block">
			  			<% if(vote.getAyes() != null && vote.getAyes().size() > 0) { %>
	 						<b>Ayes (<%=vote.getAyes().size()%>):</b>
		 					<%=JSPHelper.getPersonLinks(vote.getAyes(), appPath) %>
                            <br/>
			 			<% } %>
			 			<%if (vote.getAyeswr() != null && vote.getAyeswr().size() > 0) { %>
			 				<b>Ayes W/R (<%=vote.getAyeswr().size()%>):</b>
			 				<%=JSPHelper.getPersonLinks(vote.getAyeswr(), appPath) %>
			 				<br/>
			 			<% } %>
				 		<%if (vote.getNays() != null && vote.getNays().size() > 0) { %>
				 			<b>Nays (<%=vote.getNays().size()%>):</b>
				 			<%=JSPHelper.getPersonLinks(vote.getNays(), appPath) %>
				 			<br/>
			 			<% } %>
			 			<%if (vote.getAbstains()!=null && vote.getAbstains().size() > 0){ %>
			 				<br/>
			 				<b>Abstains (<%=vote.getAbstains().size()%>):</b>
			 				<%=JSPHelper.getPersonLinks(vote.getAbstains(), appPath) %>
			 			<% } %>
                        <%if (vote.getAbsent()!=null && vote.getAbsent().size() > 0){ %>
                            <b>Absent (<%=vote.getAbsent().size()%>):</b>
                            <%=JSPHelper.getPersonLinks(vote.getAbsent(), appPath) %>
                            <br/>
                        <% } %>
			 			<%if (vote.getExcused()!=null && vote.getExcused().size() > 0){ %>
			 				<b>Excused (<%=vote.getExcused().size()%>):</b>
			 				<%=JSPHelper.getPersonLinks(vote.getExcused(), appPath) %>
			 				<br/>
			 			<% } %>
		 			</blockquote>
		 		</div>
		 		<%
			}
		}
  	%>
	<% if(billMemo!=null && !billMemo.matches("\\s*")) { %>
		<div class="pagebreak"></div>
		<h3 class="section"><a id="Memo" href="#Memo" class="anchor ui-icon ui-icon-link"></a> Memo</h3>
		<pre class='memo'><%=billMemo%></pre>
	<% } %>
	<br/>
	<div class="pagebreak"></div>
	<h3 class="section" ><a id="Text" href="#Text" class="anchor ui-icon ui-icon-link"></a> Text</h3>
	<pre><%=TextFormatter.htmlTextPrintable(bill)%></pre>
	<br/>
</div>
</div>

