<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.regex.*, java.util.Hashtable, java.util.TreeSet, java.util.HashMap, java.util.Date, java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
	}

	public String removeBillLineNumbers (String input) {
		StringBuffer resp = new StringBuffer();
		
		input = input.replace("S E N A T E","SENATE");
		input = input.replace("A S S E M B L Y","ASSEMBLY");
		
		StringTokenizer st = new StringTokenizer (input,"\n");
		String line = null;
		int breakIdx = -1;
		
		String startChar = null;
		boolean isLineNum = false;
		
		while (st.hasMoreTokens()) {
			line = st.nextToken();

			line = line.replace(" S ","<br/><br/>S ");
			line = line.replace(" Section ","<br/><br/>Section ");
			line = line.replace("AN ACT ","<br/><br/>AN ACT ");
			line = line.replace("THE  PEOPLE ","<br/><br/>THE PEOPLE ");
			line = line.replace("_","");
			
			breakIdx = 6; //line.indexOf(' ');

			if (breakIdx != -1) {
				startChar = line.substring(0,breakIdx).trim();
			
				try {
					Integer.parseInt(startChar);
					isLineNum = true;
				}
				catch (NumberFormatException nfe) {
					isLineNum = false;
				}
				
				if (isLineNum)
					line = line.substring(breakIdx+1);
				if (line.endsWith(":"))
					line = line + "<br/>";
				
				resp.append(' ');

		        resp.append(line);
		        resp.append("\n");
		      }
		      else {
		        resp.append(' ');
		        resp.append(line);
		        resp.append("<br/>");
		      }
		    }
		    
		    String output =  resp.toString();
		    
		    return output;
	}%>
	<%
	String appPath = request.getContextPath();

	Bill bill = (Bill)request.getAttribute("bill");
	
	ArrayList<Bill> rBills			= defaultList((ArrayList<Bill>)request.getAttribute("related-bill"));
	ArrayList<Action> rActions	= defaultList((ArrayList<Action>)request.getAttribute("related-action"));
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
	
	String billMemo = bill.getMemo().replace("-\n", "").replace("\n\n", "<br/><br/>").replace("\n", " ");
%>
<h2 class='page-title'>Bill Details for ${bill.senateBillNo}</h2>
<div class="content-bg">
	<div class="title-block">
		<div class='item-actions'>
			<ul>
        		<li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
				<li><a href="${appPath}/api/1.0/lrs-print/bill/${bill.senateBillNo}" class="hidemobile" target="_new">Print Original Bill Format</a></li>
				<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
        		<li><a href="#comments">Read or Leave Comments</a></li>
			</ul>
		</div>
		<h3 class='item-title'>${bill.senateBillNo}: ${bill.title}</h3>
	   <div class="summary"><p>${bill.summary}</p></div>
	</div>
    <c:if test="${active} == false">
        <div class="amended">This bill has been amended.</div>
    </c:if>
    <div class="item-meta">
        <div id="subcontent">
	       <div class="billheader">
                <% if (bill.getSameAs()!=null) { %>
                    <div>
                        <span class="meta">Same as:</span>
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
					%></div><%
			    }

                if (rBills.size() > 0) { %>
                    <div>
                        <span class="meta">Versions:</span> 
                        <% for (Bill rBill:rBills) { %>
				           <a href="/legislation/bill/<%=rBill.getSenateBillNo()%>"><%=rBill.getSenateBillNo()%></a> 
				        <% } %>
 					</div><%
				}
                
                if (bill.getSenateBillNo().equals("J375-2013")) { %>
                    <div>
		                <span class="meta">Sponsors: </span> 
						<%=wrapPerson("STEWART-COUSINS",appPath)%>,
						<%=wrapPerson("SKELOS",appPath)%>,
						<%=wrapPerson("KLEIN",appPath)%>
				    </div>
                <% } else { %>
                    <div>
                        <% if (bill.getOtherSponsors().isEmpty()) { %>
                            <span class="meta">Sponsor:</span><%=JSPHelper.getSponsorLinks(bill, appPath) %>
                        <% } else { %>
                            <span class="meta">Sponsors:</span><%=JSPHelper.getSponsorLinks(bill, appPath) %>
                        <% }
                        if(bill.getMultiSponsors() != null && bill.getMultiSponsors().size() > 0) { %>
                        <div>
                            <span class="meta">Multi-sponsor(s):</span>
                            <%=JSPHelper.getMultiSponsorLinks(bill, appPath)%>
                        </div><%
                    }
			        
                    if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0) { %>
                        <div>
                            <span class="meta">Co-sponsor(s):</span>
                            <%=JSPHelper.getCoSponsorLinks(bill, appPath)%>
                        </div><%
                    }
                }

                if (bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) { %>
                    <div>
                        <span class="meta">Committee:</span>
                        <a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a>
                    </div>
                <% }

                if (bill.getLawSection() != null && !bill.getLawSection().equals("")) { %>
                    <div>
                        <span class="meta">Law Section:</span> <a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("lawsection:\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a>
                    </div>
	 			<% }
	 				 				
		 		if (bill.getLaw() != null && bill.getLaw() != "") { %>
                    <div>
                        <span class="meta">Law:</span> <%=bill.getLaw()%>
                    </div>
				<% } %>
            </div>
            <% if (rActions.size() > 0) { %>
                <h3 class="section"><%=senateBillNo%> Actions</h3>
                <ul>
                <%
                ArrayList<Action> events = sortBillEvents(rActions);
                for (Action be : events) { %>
					<li><%=df.format(be.getDate().getTime())%>: <%=formatBillEvent(bill.getSenateBillNo(), be.getText(), appPath)%></li>
				<% } %>
                </ul>
            <% } %>

	<% if (rMeetings.size() > 0) { %>
		<h3  class="section" ><%=senateBillNo%> Meetings</h3>
		<%
			for (Iterator<Meeting> itMeetings = rMeetings.iterator(); itMeetings.hasNext();){
				Meeting meeting = itMeetings.next();
				Date meetingDate = meeting.getMeetingDateTime();
				%>
				<a href="<%=appPath%>/meeting/<%=meeting.luceneOid()%>" class="sublink"><%=meeting.getCommitteeName() + (meetingDate == null ? "" : ": " + calendarSdf.format(meetingDate))%></a><%if (itMeetings.hasNext()){%>,<%}
				
			}
		}
	%>
	
	<% 
		if (rCals.size() > 0) {
			%>
				<h3  class="section" ><%=senateBillNo%> Calendars</h3>
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
					<a href="<%=appPath%>/calendar/<%=cal.luceneOid()%>" class="sublink"><%=type%><%=calDate == null ? "" : ": " +  calendarSdf.format(calDate)%></a>
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
				<h3 class="section" ><%=senateBillNo%> Votes</h3>
			<%
			
			for (Vote vote:rVotes) {
			   	String voteType = "Floor Vote";
			   	
				if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
					voteType = "Committee Vote";
		 		%>
		 		
				<div>
		  			<b>VOTE: <%=voteType.toUpperCase()%>:
                    <% if(vote.getDescription() != null && !vote.getDescription().isEmpty()){ %>
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
                        <%if (vote.getAbsent()!=null && vote.getAbsent().size() > 0){ %>
                            <br/>
                            <b>Absent (<%=vote.getAbsent().size()%>):</b>
                            <%= getVoterString(vote.getAbsent(), appPath) %>
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
		<div style="page-break-after:always"></div>
		<h3 class="section"><%=senateBillNo%> Memo</h3>
		<pre><%=billMemo%></pre>
	<% } %>
	<div style="page-break-after:always"></div>
	<h3 class="section" ><%=senateBillNo%> Text</h3>
	<%
		if (bill.getFulltext()!=null && !bill.getFulltext().equals("")) {

			String billText = TextFormatter.lrsPrinter(bill.getFulltext());
			System.out.println(bill.getSenateBillNo().substring(0,1));
			if ( bill.getSenateBillNo().startsWith("A") || bill.getSenateBillNo().startsWith("S") ){
				billText = "<div class='billHeader'>" +removeBillLineNumbers(billText);
				billText = billText.replace("S T A T E   O F   N E W   Y O R K ","                                   STATE OF NEW YORK");
				billText = billText.replace("EXPLANATION--Matter","<br/><br/><div class='hidden'>EXPLANATION--Matter").replace(" is old law to be omitted.", " is old law to be omitted.</div>");
				billText = billText.replace("Introduced ","</div>Introduced").replace("IN  SENATE ","</div>IN  SENATE");
				billText = billText.replaceAll(" br/>","<br/>");
			}else{
				billText = removeBillLineNumbers(billText);
			}
 
		 	 
			%>
				<pre><%=billText %></pre>
		<% } else { %>
			Not Available.
	<% } %>
	<br/>
</div>
</div>
</div>