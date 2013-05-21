<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, org.apache.commons.lang3.StringUtils, java.util.Iterator,java.util.ArrayList, java.util.Collection,java.util.List,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.xml.bind.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String oid = null;

	String appPath = request.getContextPath();

	Calendar calendar = (Calendar) request.getAttribute("calendar");
	String title = "Calendar " + calendar.getNo() + " "
			+ calendar.getSessionYear();
	SimpleDateFormat sdf = new SimpleDateFormat();
	sdf.applyPattern("EEE, MMM d, yyyy");
%>
<br/>

<h2>Calendar no. <%=calendar.getNo()%> (<%=calendar.getType()%>) / Year: <%=calendar.getYear()%> / Session: <%=calendar.getSessionYear()%> - <%=calendar.getSessionYear() + 1%></h2>

<div style="float:right;">
		<script type="text/javascript"
			src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script>
</div>
<br style="clear: both;" />
<div id="content">
	
	<%
		int count = 0;
		
		Iterator<Supplemental> itSupp = calendar.getSupplementals().iterator();
		Supplemental supp = null;
	
		while (itSupp.hasNext()) {
			try {
				supp = itSupp.next();
				supp.setCalendar(calendar);
				oid = calendar.luceneOid();
	
				if (calendar.getType().equals("active") && supp.getSequences() == null)
					continue; %>
					
				<h3>
					<% if (supp.getSequences() == null || supp.getSequences().size() == 0) {
						if(count != 0) {
							%> Supplemental 
							<% if (supp.getSupplementalId() != null) { %>
								 (<%=supp.getSupplementalId()%>)
							<% } %>
							: 
						<% }
					} else {
						supp.setCalendarDate(supp.getSequences().get(0).getActCalDate());
					}
						
					%> 
					<% if (supp.getCalendarDate() != null) { %>
						 <b>Calendar Date:</b> <%=sdf.format(supp.getCalendarDate())%>
				 	<% } %> 
				 	<% if (supp.getReleaseDateTime() != null) { %>
				 		  / <b>Released:</b> <%=sdf.format(supp.getReleaseDateTime())%>
				 	<% } %>
				</h3>
				
				<%
					List<Sequence> seqs = supp.getSequences();
				
					if(seqs != null) {
						for(Sequence seq:seqs) {
                            String seqTitle = "Active List "+supp.getCalendar().getNo()+(seq.getNo() == "" ? "" : "-"+seq.getNo());
                            %> <h4><%=seqTitle%> published <%=new SimpleDateFormat("MMM d, h:mma").format(seq.getReleaseDateTime())%>:</h4> <%
							
							if (seq.getNotes() != null && seq.getNotes().trim().length() > 0) { %>
							<h4>Notes</h4>
							<%=seq.getNotes()%>
							<hr />
						<% } %>
	
						<div class="billSummary">
							<ul>
							<%
								Iterator<CalendarEntry> itCals = seq.getCalendarEntries().iterator();
								while (itCals.hasNext()) {
									CalendarEntry calEnt = itCals.next();
									%>
									<li>Calendar: <%=calEnt.getNo()%> 
									<%
							 			if (calEnt.getBill() != null) {
							 			    Bill bill = calEnt.getBill();
							 				String senateBillNo = calEnt.getBill().getSenateBillNo();
					                        if (bill.getSponsor()!=null) {
				                                if (bill.getOtherSponsors().isEmpty()) { %>
			                                         Sponsor: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
                                                <% } else { %>
			                                         Sponsors: <%=JSPHelper.getSponsorLinks(bill, appPath) %>
			                                    <% } %>
					                            / 
					                        <% } %>

											<% if (calEnt.getSubBill() != null) {
											    bill = calEnt.getSubBill();
											    if (bill.getOtherSponsors().isEmpty()) { %>
	                                                (Sub-bill Sponsor: <%=JSPHelper.getSponsorLinks(bill, appPath) %>)
	                                           <% } else { %>
	                                                (Sub-bill Sponsors: <%=JSPHelper.getSponsorLinks(bill, appPath) %>)
	                                           <% } %>
											<% } %>
											 / Printed No.: <a href="<%=appPath%>/bill/<%=senateBillNo%>"><%=senateBillNo%></a>
											<% if (calEnt.getBillHigh() != null) { %>
												<b style="color: green">HIGH</b> 
											<% } 
											if (calEnt.getSubBill() != null) {
												String senateSubBillNo = calEnt.getSubBill().getSenateBillNo();
												%>
													(Sub-bill: <a href="<%=appPath%>/bill/<%=senateSubBillNo%>"><%=senateSubBillNo%></a>)
												<%
											}
										}
										if (calEnt.getBill().getTitle() != null) { %>
											<br />
											Title: <%=calEnt.getBill().getTitle()%>
										<% } else if (calEnt.getSubBill() != null && calEnt.getSubBill().getTitle() != null) { %>
											<br/>
											Title: <%=calEnt.getSubBill().getTitle()%> 
										<% } %> 
									</li>
								<% } %> 
							</ul>
						</div> <%
						}
					}
				
					if (supp.getSections() != null && supp.getSections().size() > 0) { %>
	 					<blockquote>
	 					<%
							Iterator<Section> itSection = supp.getSections().iterator();
							while (itSection.hasNext()) {
								Section section = itSection.next();
								%>
									<h4>Section:<%=section.getName()%> (<%=section.getType()%> / <%=section.getCd()%>)</h4>
									<div class="billSummary">
										<ul>
										<%
											Iterator<CalendarEntry> itCals = section.getCalendarEntries().iterator();
											while (itCals.hasNext()) {
												CalendarEntry calEnt = itCals.next();
												%>
													<li>Calendar: <%=calEnt.getNo()%> 
												<%
										 			if (calEnt.getBill() != null) {
										 				String senateBillNo = calEnt.getBill().getSenateBillNo();
										 					
														%> / Sponsor: <a href="<%=appPath%>/sponsor/<%=calEnt.getBill().getSponsor()
																.getFullname()%>"><%=calEnt.getBill().getSponsor()
																.getFullname()%></a> 
														<% if (calEnt.getSubBill() != null) { %>
															(Sub-bill Sponsor: 
																<a href="<%=appPath%>/sponsor/<%=calEnt.getSubBill().getSponsor()
																		.getFullname()%>"><%=calEnt.getSubBill().getSponsor()
																		.getFullname()%></a>)
														<% } %>
														 / Printed No.: <a href="<%=appPath%>/bill/<%=senateBillNo%>"><%=senateBillNo%></a>
														<% if (calEnt.getBillHigh() != null) { %>
															<b style="color: green">HIGH</b> 
														<% } 
														if (calEnt.getSubBill() != null) {
															String senateSubBillNo = calEnt.getSubBill().getSenateBillNo();
															%>
																(Sub-bill: <a href="<%=appPath%>/bill/<%=senateSubBillNo%>"><%=senateSubBillNo%></a>)
															<%
														}
													}
													if (calEnt.getBill().getActClause() != null) { %>
														<br /> <%=calEnt.getBill().getActClause()%>
													<% } else if (calEnt.getSubBill() != null && calEnt.getSubBill().getActClause() != null) { %>
														<br/> <%=calEnt.getSubBill().getActClause()%>
													<% } %> 
												</li>
											<% } %> 
									</ul>
								</div>
							<% } %>
						</blockquote>
					<% }
			}
			catch (Exception e) {
				
			}
	
			count++;
		}
		if (oid != null) { %>
				<div id="formatBox"><b>Formats:</b>
				<a href="<%=appPath%>/api/1.0/json/calendar/<%=oid%>">JSON</a>
				<a href="<%=appPath%>/api/1.0/xml/calendar/<%=oid%>">XML</a></div>
		<% } 
	%>
</div>
