<%@ page language="java"
import="gov.nysenate.openleg.util.JSPHelper, org.apache.commons.lang3.StringUtils, java.util.Iterator,java.util.ArrayList, java.util.Collection,java.util.List,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,javax.xml.bind.*"
	contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String oid = null;

	String appPath = request.getContextPath();

	Calendar calendar = (Calendar) request.getAttribute("calendar");
	String title = "Calendar " + calendar.getNo() + " "
			+ calendar.getSessionYear();
	SimpleDateFormat sdf = new SimpleDateFormat();
	sdf.applyPattern("EEE, MMM d, yyyy");
%>
<div id="content">
<h2 class='page-title'>
	Calendar no.
	<%=calendar.getNo()%>
	(<%=calendar.getType()%>) 
</h2>
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
		<%
			int count = 0;

			Iterator<Supplemental> itSupp = calendar.getSupplementals()
					.iterator();
			Supplemental supp = null;

			while (itSupp.hasNext()) {
				try {
					supp = itSupp.next();
					supp.setCalendar(calendar);
					oid = calendar.luceneOid();

					if (calendar.getType().equals("active")
							&& supp.getSequences() == null)
						continue;
		%>

		<h3 class='item-title'>
			<%
				if (supp.getSequences() == null
								|| supp.getSequences().size() == 0) {
							if (count != 0) {
			%>
			Supplemental
			<%
				if (supp.getSupplementalId() != null) {
			%>
			(<%=supp.getSupplementalId()%>)
			<%
				}
			%>
			:
			<%
				}
						} else {
							supp.setCalendarDate(supp.getSequences().get(0)
									.getActCalDate());
						}
			%>
			<%
				if (supp.getCalendarDate() != null) {
			%>
			Calendar from
			<%=sdf.format(supp.getCalendarDate())%>
			<%
				}
			%>
		</h3>
	</div>
	<div class="item-meta">
		<div id="subcontent">
			<div class="billheader">
				<div>
					<span class="meta">Released:</span>
					<%
						if (supp.getReleaseDateTime() != null) {
					%>
					<%=sdf.format(supp.getReleaseDateTime())%>
					<%
						}
					%>
				</div>
				<div>
					<span class="meta">Year:</span>
					<%=calendar.getYear()%>
				</div>
				<div>
					<span class="meta">Session: </span>
 					<%=calendar.getSessionYear()%>- <%=calendar.getSessionYear() + 1%>
				</div>
				

				<%
					List<Sequence> seqs = supp.getSequences();

							if (seqs != null) {
								for (Sequence seq : seqs) {
									String seqTitle = "Active List "
											+ supp.getCalendar().getNo()
											+ (seq.getNo() == "" ? "" : "-"
													+ seq.getNo());
				%>
				<div>
					<span class="meta">Notes: </span>
					<%=seqTitle%>
					published
					<%=new SimpleDateFormat("MMM d, h:mma")
									.format(seq.getReleaseDateTime())%>
				</div>
				<div>
					<span class="meta">Notes: </span>
					<%
						if (seq.getNotes() != null
												&& seq.getNotes().trim().length() > 0) {
					%>
					<%=seq.getNotes()%>
					<%
						}
					%>
				</div>

		</div>
		<div class="billSummary">

			<div>
				<%
					Iterator<CalendarEntry> itCals = seq
											.getCalendarEntries().iterator();
									while (itCals.hasNext()) {
										CalendarEntry calEnt = itCals.next();
				%>
				<div class="row">
					<a id="<%=calEnt.getNo()%>" href="#<%=calEnt.getNo()%>">Calendar: <%=calEnt.getNo()%>
					</a>
					<span class="subrow">
					
					<%
						if (calEnt.getBill() != null) {
												Bill bill = calEnt.getBill();
												String senateBillNo = calEnt.getBill()
														.getSenateBillNo();
												if (bill.getSponsor() != null) {
													if (bill.getOtherSponsors().isEmpty()) {
					%>
					<br />Sponsor:
					<%=JSPHelper
													.getSponsorLinks(bill,
															appPath)%>
					<%
						} else {
					%>
					<br />Sponsors:
					<%=JSPHelper
													.getSponsorLinks(bill,
															appPath)%>
					<%
						}
						}
					%>

					<%
						if (calEnt.getSubBill() != null) {
													bill = calEnt.getSubBill();
													if (bill.getOtherSponsors().isEmpty()) {
					%>
					(Sub-bill Sponsor:
					<%=JSPHelper
													.getSponsorLinks(bill,
															appPath)%>)
					<%
						} else {
					%>
					(Sub-bill Sponsors:
					<%=JSPHelper
													.getSponsorLinks(bill,
															appPath)%>)
					<%
						}
					%>
					<%
						}
					%>
					<br /> Printed No.: <a href="<%=appPath%>/bill/<%=senateBillNo%>"><%=senateBillNo%></a>
					<%
						if (calEnt.getBillHigh() != null) {
					%>
					<b style="color: green">HIGH</b>
					<%
						}
												if (calEnt.getSubBill() != null) {
													String senateSubBillNo = calEnt
															.getSubBill().getSenateBillNo();
					%>
					(Sub-bill: <a href="<%=appPath%>/bill/<%=senateSubBillNo%>"><%=senateSubBillNo%></a>)
					<%
						}
											}
											if (calEnt.getBill().getTitle() != null) {
					%>
					<br /> Title:
					<%=calEnt.getBill().getTitle()%>
					<%
						} else if (calEnt.getSubBill() != null
													&& calEnt.getSubBill().getTitle() != null) {
					%>
					<br /> Title:
					<%=calEnt.getSubBill().getTitle()%>
					<%
						}
					%>
					</span>
				</div>
				<%
					}
				%>
			</div>
		</div>
		<%
			}
					}

					if (supp.getSections() != null
							&& supp.getSections().size() > 0) {
		%>
		<%
			Iterator<Section> itSection = supp.getSections()
								.iterator();
						while (itSection.hasNext()) {
							Section section = itSection.next();
		%>
		<h3 class='section'>
			Section:
			<%=section.getName()%>
			(<%=section.getType()%>
			/
			<%=section.getCd()%>)
		</h3>
		<div class="calendar-summary">
			<%
				Iterator<CalendarEntry> itCals = section
										.getCalendarEntries().iterator();
								while (itCals.hasNext()) {
									CalendarEntry calEnt = itCals.next();
			%>
			<div class="row">
				<a id="<%=calEnt.getNo()%>" href="#<%=calEnt.getNo()%>">Calendar: <%=calEnt.getNo()%></a><br/>
				<span class="subrow">

					<%
						if (calEnt.getBill() != null) {
												String senateBillNo = calEnt.getBill()
														.getSenateBillNo();
					%> Sponsor: <a
					href="<%=appPath%>/sponsor/<%=calEnt.getBill().getSponsor()
											.getFullname()%>"><%=calEnt.getBill().getSponsor()
											.getFullname()%></a> <%
 	if (calEnt.getSubBill() != null) {
 %>
					(Sub-bill Sponsor: <a
					href="<%=appPath%>/sponsor/<%=calEnt.getSubBill()
												.getSponsor().getFullname()%>"><%=calEnt.getSubBill()
												.getSponsor().getFullname()%></a>) <%
 	}
 %> <br /> Printed No.: <a
					href="<%=appPath%>/bill/<%=senateBillNo%>"><%=senateBillNo%></a> <%
 	if (calEnt.getBillHigh() != null) {
 %>
					<b style="color: green">HIGH</b> <%
 	}
 							if (calEnt.getSubBill() != null) {
 								String senateSubBillNo = calEnt
 										.getSubBill().getSenateBillNo();
 %> (Sub-bill: <a
					href="<%=appPath%>/bill/<%=senateSubBillNo%>"><%=senateSubBillNo%></a>)
					<%
 	}
 						}
 						if (calEnt.getBill().getActClause() != null) {
 %> <br /> <%=calEnt.getBill().getActClause()%>
					<%
						} else if (calEnt.getSubBill() != null
													&& calEnt.getSubBill().getActClause() != null) {
					%>
					<br /> <%=calEnt.getSubBill()
											.getActClause()%> <%
 	}
 %>
				</span>
			</div>
			<%
				}
			%>
		</div>
	</div>
	<%
		}
	%>
	<%
		}
			} catch (Exception e) {

			}

			count++;
		}
		
	%>
</div>
</div>
</div>