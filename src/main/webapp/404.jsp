<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp"/>
<div id="content">
	<div class="content-bg">
		<h2 class='page-title'>Sorry no results were found for that search.</h2>
	    <h3>It's possible that this information isn't available yet for the current session.</h3>
	    <br/>
	    <br/>
		There are currently five different distinct paths for searching on Open Legislation:
		<br/>
		<br/>
		1) <b>By KEYWORD:</b> type one or more words in the text box at the top of any screen; this performs a keyword search on the Title, Summary, Memo, Bill Number and Sponsor fields of all bills (and resolutions), and any bill containing any of the keywords will be returned.  Highest numbered bills are returned first, at the top of the list.  TIP: you can also use the "AND" operator or "" (quotation marks) to narrow your keyword search to more precise matches.
		<br/>
		<br/>
		2) <b>By RECENT ACTIONS:</b> click the "Recent Actions" hyperlink to return the bills with the most recent activity; you may then further refine this search by using the drop-down menu to filter by various types of recent actions; these filters search for the respective action keywords within the "action" text field.
		<br/>
		<br/>
		3) <b>By RECENT VOTES:</b> click the "Recent Votes" hyperlink to return the bills most recently voted upon in the Senate; note that this search will not return Assembly bills.
		<br/>
		<br/>
		4) <b>By SPONSOR:</b> click on the "By Sponsor" hyperlink to get a listing of all Senators, from which you can click on any Senator to see a full listing of bills they have sponsored.
		<br/>
		<br/>
		5) <b>By COMMITTEE:</b> click on the "By Committee" hyperlink to get a listing of all Senate Standing Committees, from which you can click on any Committee to see a full listing of bills from that Committee; note that this search will not return any Assembly Committees or bills.
		<br/>
		<br/>
		<hr/>
		*Note: Except where otherwise noted, all searches return both Senate and Assembly bills.
		<br/>
		*Note: when entering a Bill number, OMIT any leading zero (0).
	</div>
</div>
 <jsp:include page="footer.jsp"/>
