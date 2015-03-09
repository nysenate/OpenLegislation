<%@ page language="java" import="gov.nysenate.openleg.util.*, java.util.regex.*, java.text.* ,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<%@ page import="gov.nysenate.openleg.model.Calendar" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!
    public <T> ArrayList<T> defaultList(ArrayList<T> list) {
        if(list == null)
            return new ArrayList<T>();
        return list;
    }

    public boolean eventsContainRestoredAmendment(ArrayList<Action> events) {
        for (Action event : events) {
            if (event.getText().matches("^AMEND(ED)? BY RESTORING.*") || event.getText().matches("^amend(ed)? by restoring.*")) {
                return true;
            }
        }
        return false;
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
%>

<%
	/** This is related to http://dev.nysenate.gov/issues/8477. */
	HashMap<String, String> billsWithExternalPdfLinks = new HashMap<String, String>();
	billsWithExternalPdfLinks.put("S2000-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/StateOpsBudget.pdf");
	billsWithExternalPdfLinks.put("A3000-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/StateOpsBudget.pdf");
	billsWithExternalPdfLinks.put("S2001-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/Leg-Judi.pdf");
	billsWithExternalPdfLinks.put("A3001-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/Leg-Judi.pdf");
	billsWithExternalPdfLinks.put("S2002-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/DEBT.pdf");
	billsWithExternalPdfLinks.put("A3002-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/DEBT.pdf");
	billsWithExternalPdfLinks.put("S2003-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/Local.pdf");
	billsWithExternalPdfLinks.put("A3003-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/Local.pdf");
	billsWithExternalPdfLinks.put("S2004-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/CapitalProjectsBudget.pdf");
	billsWithExternalPdfLinks.put("A3004-2015", "https://www.budget.ny.gov/pubs/executive/eBudget1516/fy1516appropbills/CapitalProjectsBudget.pdf");
%>

<%
    Bill bill = (Bill)request.getAttribute("bill");
	
	String titleText = "(no title)";
	if (bill.getTitle()!=null) {
		titleText = bill.getTitle();
	}
	
	String title = bill.getBillId() + " - NY Senate Open Legislation - " + titleText;
	
	request.setAttribute("useTwitterCard", true);
	
    String appPath = request.getContextPath();

    @SuppressWarnings("unchecked")
    ArrayList<Bill> rBills = defaultList((ArrayList<Bill>)request.getAttribute("related-bill"));
    @SuppressWarnings("unchecked")
    ArrayList<Action> rActions = defaultList((ArrayList<Action>)request.getAttribute("related-action"));
    @SuppressWarnings("unchecked")
    ArrayList<Meeting> rMeetings = defaultList((ArrayList<Meeting>)request.getAttribute("related-meeting"));
    @SuppressWarnings("unchecked")
    ArrayList<Calendar> rCals = defaultList((ArrayList<Calendar>)request.getAttribute("related-calendar"));
    @SuppressWarnings("unchecked")
    ArrayList<Vote> rVotes = defaultList((ArrayList<Vote>)request.getAttribute("related-vote"));

    String senateBillNo = bill.getBillId();
    String year = null;
    
    SimpleDateFormat calendarSdf = new SimpleDateFormat("MMM d, yyyy");
    
    DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
    
    String billMemo = bill.getMemo().replace("-\n", "").replace("\n\n", "<br/><br/>").replace("\n", " ");

    String pageTitle = "";
    if (bill.isResolution()) {
        pageTitle = "Resolution "+ bill.getBillId();
    } else {
        pageTitle = "Bill "+bill.getBillId();
    }
    String url = "http://open.nysenate.gov/legislation/bill/"+bill.getBillId();
    String Summary = "";
    if (bill.getSummary().length() > 200 ){
        Summary = bill.getSummary().substring(0,196)+"...";
    }else{
        Summary = bill.getSummary();
    }
    HashMap<String, String> twitterMetaTags = new HashMap<String, String>();
    twitterMetaTags.put("twitter:card", "summary");
    twitterMetaTags.put("twitter:title", pageTitle);
    twitterMetaTags.put("twitter:description",Summary);
    twitterMetaTags.put("twitter:site", "@nysenate");
    twitterMetaTags.put("twitter:url", url);
    request.setAttribute("twitterMetaTags", twitterMetaTags);
%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="<%=title%>"/>
</jsp:include>
<div id="content">
    <div class="content-bg">
        <div class="page-title">
            <%
                if (bill.isActive() == false) {
            %>
               <span class="amended">This bill has been amended</span>
            <%
                }
            %>
            <h2><%=pageTitle%></h2>
        </div>

        <div class="title-block">
            <div class='item-actions'>
                <ul>
                    <li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
					<% if (billsWithExternalPdfLinks.containsKey(bill.getOid()) && bill.getFulltext().isEmpty()) { %>
					<%= "<li><a href=" + billsWithExternalPdfLinks.get(bill.getOid()) + " >Original Bill Format (PDF)</a></li>" %>
					<% } else { %>
						<li><a href="<%=appPath%>/api/1.0/pdf/bill/<%=bill.getBillId()%>">Original Bill Format (PDF)</a></li>
					<%	} %>
					<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
                    <li><a href="#Comments">Read or Leave Comments</a></li>
                </ul>
            </div>
        
            <h3 class='item-title'>${bill.title}</h3>
	        <%
	            if (bill.getTitle()+"." != bill.getSummary()) {
	        %>
	           <div class="summary"><p>${bill.summary}</p></div>
	        <%
	            }
	        %>
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
	                            %><a href="<%=sameAsLink%>"><%=sameAs%></a>
	                        <% } %>
			            </span></li>
			        <% }
			            	
			        if (rBills.size() > 0) { %>
	                        <li><span class="meta">Versions</span><span class="metadata">
		                        <%
		                            for (Bill rBill:rBills) {
		                        %>
		                           <a href="/legislation/bill/<%=rBill.getBillId()%>"><%=rBill.getBillId()%></a> 
		                        <%
 		                            }
 		                        %>
		                    </span></li><%
		                        }
		                    %>
                        <li>
		                    <%
		                        if (bill.getOtherSponsors().isEmpty()) {
		                    %>
		                        <span class="meta">Sponsor:</span><span class="metadata"><%=JSPHelper.getSponsorLinks(bill, request)%></span>
		                    <%
		                        } else {
		                    %>
		                        <span class="meta">Sponsors:</span><span class="metadata"><%=JSPHelper.getSponsorLinks(bill, request)%></span>
		                    <%
		                        }
		                    %>
                        </li>
                        <%
                            if(bill.getMultiSponsors() != null && bill.getMultiSponsors().size() > 0) {
                        %>
		                    <li>
		                        <span class="meta">Multi-sponsor(s):</span>
		                        <span class="metadata"><%=JSPHelper.getMultiSponsorLinks(bill, request)%></span>
		                    </li>
		                <%
		                    }
		                                
		                                if (bill.getCoSponsors()!=null && bill.getCoSponsors().size()>0) {
		                %>
                            <li>
		                        <span class="meta">Co-sponsor(s):</span>
		                        <span class="metadata"><%=JSPHelper.getCoSponsorLinks(bill, request)%></span>
		                    </li>
		                <%
		                    }

		                		                if (bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) {
		                %>
		                    <li>
		                        <span class="meta">Committee:</span>
		                        <span class="metadata"><a href="<%=appPath%>/committee/<%=java.net.URLEncoder.encode(bill.getCurrentCommittee(),"utf-8")%>" class="sublink"><%=bill.getCurrentCommittee()%></a></span>
		                    </li>
		                <%
		                    }

		                		                if (bill.getLawSection() != null && !bill.getLawSection().equals("")) {
		                %>
		                    <li>
		                        <span class="meta">Law Section:</span> <span class="metadata"><a href="<%=appPath%>/search/?term=<%=java.net.URLEncoder.encode("lawsection:\"" + bill.getLawSection()+"\"","utf-8")%>" class="sublink"><%=bill.getLawSection()%></a>
		                    </span></li>
		                <%
		                    }
		                                            
		                		                if (bill.getLaw() != null && bill.getLaw() != "") {
		                %>
		                    <li>
		                        <span class="meta">Law:</span> <span class="metadata"><%=bill.getLaw()%></span>
		                    </li>
		                <%
		                    }
		                %>
                    </ul>
                </div>
	            <%
	                if (rActions.size() > 0) {
	            %>
	                <h3 class="section"> <a id="Actions" href="#Actions" class="anchor ui-icon ui-icon-link"></a> Actions</h3>
	                <div class="section-list"><ul>
		                <%
		                    ArrayList<Action> events = sortBillEvents(rActions);
                            if (eventsContainRestoredAmendment(events)) {
                                for (Action be : events) {
                        %>
                        <li><%=df.format(be.getDate().getTime())%>: <%=be.getText()%></li>
                        <% }
                            }
                            else {
		                        for (Action be : events) {
		                %>
		                    <li><%=df.format(be.getDate().getTime())%>: <%=formatBillEvent(bill.getBillId(), be.getText(), appPath)%></li>
		                <% }
                        }%>
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
			                <li><a href="<%=appPath%>/meeting/<%=meeting.getOid()%>" class="sublink"><%=meeting.getCommitteeName() + (meetingDate == null ? "" : ": " + calendarSdf.format(meetingDate))%></a></li>
			            <% } %>
			       </ul></div>
                <% } %>
    
                <% if (rCals.size() > 0) { %>
                    <h3  class="section" ><a id="Calendars" href="#Calendars" class="anchor ui-icon ui-icon-link"></a> Calendars</h3>
		            <div class="section-list"><ul>
			            <% for (Iterator<Calendar> itCals = rCals.iterator(); itCals.hasNext();) {
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
			                <li><a href="<%=appPath%>/calendar/<%=cal.getOid()%>" class="sublink"><%=type%><%=calDate == null ? "" : ": " +  calendarSdf.format(calDate)%></a></li>
		                <% } %>
		            </ul></div>
		        <% } %>

                <% if(rVotes.size() > 0) { %>
                    <h3 class="section" ><a id="Votes" href="#Votes" class="anchor ui-icon ui-icon-link"></a> Votes</h3>
                    <%
		            for (Vote vote:rVotes) {
		                String voteType = "Floor Vote";
		                
		                if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE) {
		                    voteType = "Committee Vote";
		                }
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
		                            <%=JSPHelper.getPersonLinks(vote.getAyes(), request) %>
		                            <br/>
		                        <% } %>
		                        <%if (vote.getAyeswr() != null && vote.getAyeswr().size() > 0) { %>
		                            <b>Ayes W/R (<%=vote.getAyeswr().size()%>):</b>
		                            <%=JSPHelper.getPersonLinks(vote.getAyeswr(), request) %>
		                            <br/>
		                        <% } %>
		                        <%if (vote.getNays() != null && vote.getNays().size() > 0) { %>
		                            <b>Nays (<%=vote.getNays().size()%>):</b>
		                            <%=JSPHelper.getPersonLinks(vote.getNays(), request) %>
		                            <br/>
		                        <% } %>
		                        <%if (vote.getAbstains()!=null && vote.getAbstains().size() > 0){ %>
		                            <br/>
		                            <b>Abstains (<%=vote.getAbstains().size()%>):</b>
		                            <%=JSPHelper.getPersonLinks(vote.getAbstains(), request) %>
		                        <% } %>
		                        <%if (vote.getAbsent()!=null && vote.getAbsent().size() > 0){ %>
		                            <b>Absent (<%=vote.getAbsent().size()%>):</b>
		                            <%=JSPHelper.getPersonLinks(vote.getAbsent(), request) %>
		                            <br/>
		                        <% } %>
		                        <%if (vote.getExcused()!=null && vote.getExcused().size() > 0){ %>
		                            <b>Excused (<%=vote.getExcused().size()%>):</b>
		                            <%=JSPHelper.getPersonLinks(vote.getExcused(), request) %>
		                            <br/>
		                        <% } %>
		                    </blockquote>
		                </div>
		            <% }
		        }
                
                if(billMemo!=null && !billMemo.matches("\\s*")) { %>
			        <div class="pagebreak"></div>
			        <h3 class="section"><a id="Memo" href="#Memo" class="anchor ui-icon ui-icon-link"></a> Memo</h3>
			        <pre class='memo'><%=billMemo%></pre>
			    <% } %>
			    <br/>
			    <div class="pagebreak"></div>
			    <h3 class="section" ><a id="Text" href="#Text" class="anchor ui-icon ui-icon-link"></a> Text</h3>
				<% if (billsWithExternalPdfLinks.containsKey(bill.getOid()) && bill.getFulltext().isEmpty()) { %>
				<%= "<p style='text-align:center'>Note: The full text of this budget bill is currently available <a href=" + billsWithExternalPdfLinks.get(bill.getOid()) + " >here.</a></p>" %>
				<% } %>
				<pre><%=TextFormatter.htmlTextPrintable(bill)%></pre>


			    <br/>
            </div>
        </div>
        <jsp:include page="/templates/disqus.jsp">
	        <jsp:param name="disqusUrl" value="<%=bill.getDisqusUrl()%>"/>
	        <jsp:param name="title" value="<%=title%>"/>
	    </jsp:include>
    </div>
</div>
<jsp:include page="/footer.jsp"/>
