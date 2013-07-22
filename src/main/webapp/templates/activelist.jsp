<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, org.apache.commons.lang3.StringUtils, java.util.Iterator,java.util.ArrayList, java.util.Collection,java.util.List,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>
<%
SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/d/yyyy hh:mm:ss aa");

Calendar activeList = (Calendar) request.getAttribute("calendar");
String activeListTitle = "Active List #"+activeList.getNo();

String activeListDate = "";
if (activeList.getDate() != null) {
    activeListDate = dateFormat.format(activeList.getDate());
}
List<Supplemental> supplementals = activeList.getSupplementals();
List<String> sectionLinks = new ArrayList<String>();
if (supplementals != null && supplementals.size() != 0) {
    Supplemental supplemental = activeList.getSupplementals().get(0);
    List<Sequence> sequences = supplemental.getSequences();
    if (sequences != null) {
        for (Sequence sequence : sequences) {
            String sequenceTitle = ""+activeList.getNo();
            if (sequence.getNo() != null && !sequence.getNo().isEmpty()) {
                sequenceTitle += "-"+sequence.getNo();
            }
            sectionLinks.add("<a href=\"#active-list-"+sequenceTitle+"\">"+sequenceTitle+"</a>");
        }
    }
}
String pageTitle = (sectionLinks.size() > 1) ? "Active Lists" : "Active List";
pageTitle += ": "+StringUtils.join(sectionLinks, ", ");

%>

<div id="content">
    <div class="content-bg">
        <h2 class="page-title"><%=pageTitle%></h2>
        <div class="item-meta">
	        <div id="subcontent" class="emptytitle">
	       		<div class="billmeta">
				     <ul>
				         <li>
                             <span class="meta">Calendar Date: </span>
                             <span class="metadata"><%=activeListDate%></span>
				     	 </li>
			        </ul>
                </div>
                <div class='item-actions'>
					<ul>
                        <li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
						<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
					</ul>
				</div>
	 		</div>
 		</div>
        <%
        if (supplementals != null && supplementals.size() != 0) {
            Supplemental supplemental = activeList.getSupplementals().get(0);
            List<Sequence> sequences = supplemental.getSequences();
            if (sequences != null) {
                for (Sequence sequence : sequences) {
                    String sequenceTitle = "Active List "+activeList.getNo();
                    if (sequence.getNo() != null && !sequence.getNo().isEmpty()) {
                        sequenceTitle += "-"+sequence.getNo();
                    }
                    String sequenceLink = sequenceTitle.toLowerCase().replace(" ", "-");
                    String sequenceReleaseDate="";
                    if (sequence.getReleaseDateTime() != null) {
                        sequenceReleaseDate = datetimeFormat.format(sequence.getReleaseDateTime());
                    }

                    %>
                    <h3 class="section" ><a id="<%=sequenceLink%>" href="#<%=sequenceLink%>" class="anchor ui-icon ui-icon-link"></a><%=sequenceTitle%></h3>
                    <div class="item-meta">
				        <div id="subcontent" class="emptytitle">
				       		<div class="billmeta">
							     <ul>
							         <li>
							     	     <span class="meta">Released: </span>
							     	     <span class="metadata"><%=sequenceReleaseDate%></span>
							     	 </li>
						        </ul>  
				 			</div> 
				        </div>
			        </div>
			        
                    <div class="sequence">
	                    <% if (sequence.getNotes() != null && !sequence.getNotes().equals("null")) { %>
	                        <%=sequence.getNotes()%>
	                    <% } %>
                    </div>
                    <div id="sequence-<%=sequence.getNo()%>-bills" class="billSummary">
                    <% for (CalendarEntry entry : sequence.getCalendarEntries()) {
                        Bill bill = entry.getBill();
                        Bill subBill = entry.getSubBill();

                        // Just to be safe, this shouldn't ever happen
                        if (bill == null) continue;
                        %>
                        <div class="row">
                            <a id="cal<%=entry.getNo()%>" href="#cal<%=entry.getNo()%>" class="anchor ui-icon ui-icon-link"></a>
                            <span style="color:#777777; font-size:0.85em;">#<%=entry.getNo()%>: </span>
	                        <%
                            if (bill.isResolution()) {
                                %> Resolution <a href="<%=JSPHelper.getLink(request, bill)%>"><%=bill.getBillId()%></a><%
	                            } else {
	                        %> Bill <a href="<%=JSPHelper.getLink(request, bill)%>"><%=bill.getBillId()%></a><%
                            }

	                        if (entry.getBillHigh() != null && entry.getBillHigh().equals("true")) {
	                            %><span class="calendar-high" title="HIGH bills have not yet aged the normal 3 days."> HIGH </span><%
	                        }
							%>
							<br/>
							<span class="subrow indent">
				               	<a href="<%=JSPHelper.getLink(request, bill)%>">
									<%=bill.getActClause()%>
				 				</a>
    							<%
                                if (bill.getSponsor() != null) {
                                    if (bill.getOtherSponsors().isEmpty()) {
                                        %> <br/>Sponsor: <%=JSPHelper.getSponsorLinks(bill, request)%> <%
                                    } else {
                                        %> <br/>Sponsors: <%=JSPHelper.getSponsorLinks(bill, request)%> <%
                                    }
                                }

                                if (subBill != null) {
                                    if (subBill.getOtherSponsors().isEmpty()) {
                                        %> (Substituted-bill Sponsor: <%=JSPHelper.getSponsorLinks(subBill, request)%>) <%
                                    } else {
                                        %> (Substituted-bill Sponsors: <%=JSPHelper.getSponsorLinks(subBill, request)%>) <%
                                    }
	                            } %>
	                        </span>
                        </div>
                    <% } %>
                    </div>
                <% }
            }
        } %>
    </div>
</div>
