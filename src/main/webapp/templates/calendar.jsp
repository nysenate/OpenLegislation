<%@ page language="java" import="gov.nysenate.openleg.util.JSPHelper, java.util.Date,  org.apache.commons.lang3.StringUtils, java.util.Iterator,java.util.ArrayList, java.util.Collection,java.util.List,java.text.DateFormat,java.text.SimpleDateFormat,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
$(document).ready(function(){
    $(".section").click(function() {
        $("#"+$(this).attr('id')+"-bills").toggle();
    }).click();
});
</script>
<%
String appPath = request.getContextPath();
SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM/d/yyyy hh:mm:ss aa");
Calendar calendar = (Calendar) request.getAttribute("calendar");

String calendarTitle = "Floor Calendar #"+calendar.getNo();
if (calendar.getDate() != null) {
    calendarTitle += " - "+dateFormat.format(calendar.getDate());
}
%>
<div id="content">
	<h2 class='page-title'><%=calendarTitle%></h2>
	<div class="content-bg">
		<div class='item-actions'>
			<ul>
				<li><a href="#" onclick="window.print(); return false;">Print Page</a></li>
				<li><script type="text/javascript" src="http://w.sharethis.com/button/sharethis.js#publisher=51a57fb0-3a12-4a9e-8dd0-2caebc74d677&amp;type=website"></script></li>
			</ul>
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
            if (supplemental.getReleaseDateTime() != null) {
                itemTitle += " - Released "+datetimeFormat.format(supplemental.getReleaseDateTime());
            }

            %>
            <div class="title-block">
                <h3 class='item-title'><%=itemTitle%></h3>
            </div>
            <%
            for (Section section : supplemental.getSections()) {
                // Just to be safe, shouldn't ever happen.
                if (section.getCalendarEntries() == null) continue;

                String sectionId = supplementalId+"-"+section.getName().toLowerCase().replace(" ","-");
                %>
               <%--  <h3 class="section" ><a id="<%=sectionId%>" href="#<%=sectionId%>" class="anchor ui-icon ui-icon-bookmark"></a> <%=section.getName()%></h3> --%>
                
                <div id="<%=sectionId%>" class="section">
                <a href="#<%=sectionId%>" class="anchor-link"><%=section.getType()+section.getCd()%></a> - 
                 (<%=section.getCalendarEntries().size()%> items)
                </div>
                
                <div id="<%=sectionId%>-bills" class="billSummary">
                <%
                for (CalendarEntry entry : section.getCalendarEntries()) {
                    Bill bill = entry.getBill();
                    Bill subBill = entry.getSubBill();

                    // Just to be safe, this shouldn't ever happen
                    if (bill == null) continue;
                    %>
                    <div class="row">
                        <div style="margin-bottom:3px">
                        <a id="cal<%=entry.getNo()%>" href="#cal<%=entry.getNo()%>" style="color:#777777">#<%=entry.getNo()%></a>
                        <%
                        if (bill.isResolution()) {
                            %> - Resolution <a href="<%=JSPHelper.getLink(request, bill)%>"><%=bill.getSenateBillNo()%></a><%
                        } else {
                            %> - Bill <a href="<%=JSPHelper.getLink(request, bill)%>"><%=bill.getSenateBillNo()%></a><%
                        }

                        if (bill.getSponsor() != null) {
                            if (bill.getOtherSponsors().isEmpty()) {
                                %> - Sponsor: <%=JSPHelper.getSponsorLinks(bill, appPath)%> <%
                            } else {
                                %> - Sponsors: <%=JSPHelper.getSponsorLinks(bill, appPath)%> <%
                            }
                        }

                        if (subBill != null) {
                            if (subBill.getOtherSponsors().isEmpty()) {
                                %> (Substituted-bill Sponsor: <%=JSPHelper.getSponsorLinks(subBill, appPath)%>) <%
                            } else {
                                %> (Substituted-bill Sponsors: <%=JSPHelper.getSponsorLinks(subBill, appPath)%>) <%
                            }
                        } %>
                        </div>
                        <%=bill.getActClause()%>
                    </div>
                    <%
                }
                %>
                </div><%
            }
		} %>
	</div>
</div>