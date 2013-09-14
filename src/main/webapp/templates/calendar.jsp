<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.Date,  org.apache.commons.lang3.StringUtils, java.util.Iterator,java.util.ArrayList, java.util.Collection,java.util.List,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
$(document).ready(function(){
    $(".calendar-section").click(function() {
        $("#"+$(this).attr('id')+"-bills").toggle();
    }).click();
});
</script>
<%
SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/d/yyyy hh:mm:ss aa");

Calendar calendar = (Calendar) request.getAttribute("calendar");
String calendarTitle = "Floor Calendar #"+calendar.getNo();

String calendarDate = "";
if (calendar.getDate() != null) {
    calendarDate = dateFormat.format(calendar.getDate());
}
%>
<div id="content">
	<div class="content-bg">
		<h2 class='page-title'><%=calendarTitle%></h2>
        <div class="item-meta">
            <div id="subcontent" class="emptytitle">
                <div class="billmeta">
	                 <ul>
	                     <li>
	                         <span class="meta">Calendar Date: </span>
	                         <span class="metadata"><%=calendarDate%></span>
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
		for (Supplemental supplemental : calendar.getSupplementals()) {
		    // Just to be safe, shouldn't ever happen.
		    if (supplemental == null || supplemental.getSections() == null) continue;

		    String supplementalId = supplemental.getSupplementalId();
		    if (supplementalId == null || supplementalId.equals("null")) {
		        supplementalId = "";
		    }

            String itemTitle = "Calendar "+calendar.getNo();
            if (!supplementalId.isEmpty()) {
                itemTitle += "-"+supplementalId;
            }
            
            String supplementalReleaseDate = "";
            if (supplemental.getReleaseDateTime() != null) {
                supplementalReleaseDate = datetimeFormat.format(supplemental.getReleaseDateTime());
            }

            %>
            <div>
                <h3 class="section">
                    <a href="#calendar-<%=calendar.getNo()+"-"+supplementalId%>" class="anchor ui-icon ui-icon-link"></a>
                    <%=itemTitle%>
                </h3>
            </div>
            <div class="item-meta">
                <div id="subcontent" class="emptytitle">
                    <div class="billmeta">
                         <ul>
                            <li>
                                <span class="meta">Released: </span>
                                <span class="metadata"><%=supplementalReleaseDate%></span>
                            </li>
                        </ul>  
                    </div> 
                </div>
            </div>
            <%
            for (Section section : supplemental.getSections()) {
                // Just to be safe, shouldn't ever happen.
                if (section.getCalendarEntries() == null) continue;

                String sectionId = calendar.getNo()+supplementalId+"-"+section.getName().toLowerCase().replace(" ","-");
                %>
                <div id="<%=sectionId%>" class="calendar-section">
	                <a href="#<%=sectionId%>" class="anchor ui-icon ui-icon-link"></a> 
	                <%=section.getName()%> (<%=section.getCalendarEntries().size()%> items)
                </div>
                
                <div id="<%=sectionId%>-bills" class="billSummary calendar-bills">
                <%
                for (CalendarEntry entry : section.getCalendarEntries()) {
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
                    <%
                }
                %>
                </div><%
            }
		} %>
	</div>
</div>