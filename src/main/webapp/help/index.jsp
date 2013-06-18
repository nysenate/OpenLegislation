<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%
String term = (String)session.getAttribute("term");
if (term == null)
        term = "";
        
        String appPath = request.getContextPath();
        
%>
<jsp:include page="/header.jsp"/>
<style>
li 
{
margin-bottom:9px;
}
</style>
<div id="content">
	<div class="content-bg">
	<h2 class='page-title'>Open Legislation Help.</h2>
		<p>Open Legislation offers a variety of ways to search and browse legislative information.</p>
		<h2>Available Content</h2>
		<ul>
			<li>Bills: browse and search Senate and Assembly bills by number, keyword, sponsor and more</li>
			<li>Calendars: View recent and search floor calendars and active lists by number or date (i.e. 11/07/2009)</li>
			<li>Meetings: View upcoming and recent committee meetings, and search by committee, chairperson, location, date (i.e. 11/07/2009) and more.</li>
			<li>Transcripts: View and search Senate floor full text transcripts</li>
			<li>Actions: View and filter Floor Actions on Bills from the Floor of the Senate</li>
			<li>Votes: Recent committee and floor votes on Senate bills. You can also find all vote data within each related bill page.</li>
		</ul>
		<h2>Searching Bills</h2>
		<ul>
			<li>
			 <b>By KEYWORD:</b> type one or more words in the text box at the top of any screen; this performs a keyword search on the Title, Summary, Memo, Bill Number and Sponsor fields of
			all bills (and resolutions), and any bill containing any of the keywords will be returned.  Highest numbered bills are returned first, at the top of the list.  TIP: you can also use the "AND" operator or "" (quotation marks) to narrow your keyword search to more precise matches.
			</li><li>
			<b>By RECENT ACTIONS:</b> click the "Recent Actions" hyperlink to return the bills with the most recent activity; you may then further refine this search by using the drop-down menu to filter by various types of recent actions; these filters search for the respective action keywords within the "action" text field.

			</li><li>
			<b>By RECENT VOTES:</b> click the "Recent Votes" hyperlink to return the bills most recently voted upon in the Senate; note that this search will not return Assembly bills.

			</li><li>
			 <b>By SPONSOR:</b> click on the "By Sponsor" hyperlink to get a listing of all Senators, from which you can click on any Senator to see a full listing of bills they have sponsored.

			</li><li>
			<b>By COMMITTEE:</b> click on the "By Committee" hyperlink to get a listing of all Senate Standing Committees, from which you can click on any Committee to see a full listing of bills from that Committee; note that this search will not return any Assembly Committees or bills.
			</li>
		</ul>
		<br/><br/>
		<hr/>
		*Note: Except where otherwise noted, all searches return both Senate and Assembly bills.<br/>
		*Note: when entering a Bill number, OMIT any leading zero (0).
	</div>
</div>
<jsp:include page="/footer.jsp"/>
