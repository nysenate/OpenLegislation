<%@ page language="java" import="java.util.*,java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*"  contentType="text/html" pageEncoding="utf-8" %>
<%
    List<Transcript> transcripts = (List<Transcript>)request.getAttribute("transcripts");
%>
<jsp:include page="/header.jsp">
    <jsp:param name="title" value="Find Transcripts"/>
    <jsp:param name="showTypeFilter" value="true"/>
</jsp:include>
<style>
#transcript_listing td, #transcript_listing th {
    padding:10px;
    border: 1px solid black;"
}
</style>
<script>
$(document).ready(function() {
	var year = "<%=request.getAttribute("year")%>";
	var month = "<%=request.getAttribute("month")%>";
	$("select[name=year]").val(year);
	$("select[name=month]").val(month);
});
</script>
<div id="content">
<div class="content-bg">
<div class="page-title">
    <div><form method="GET" action="" style="float:right; padding:0px; margin:0px;">
	<select name="year">
	<option value="2015">2015</option>
	<option value="2014">2014</option>
	<option value="2013">2013</option>
	<option value="2012">2012</option>
	<option value="2011">2011</option>
	<option value="2010">2010</option>
	<option value="2009">2009</option>
	<option value="2008">2008</option>
	<option value="2007">2007</option>
	<option value="2006">2006</option>
	<option value="2005">2005</option>
	<option value="2004">2004</option>
	<option value="2003">2003</option>
	<option value="2002">2002</option>
	<option value="2001">2001</option>
	<option value="2000">2000</option>
	<option value="1999">1999</option>
	<option value="1998">1998</option>
	<option value="1997">1997</option>
	<option value="1996">1996</option>
	<option value="1995">1995</option>
	<option value="1994">1994</option>
	<option value="1993">1993</option>


	</select>
	<select name="month">
    <option value="">All</option>
    <option value="01">Jan</option>
    <option value="02">Feb</option>
    <option value="03">Mar</option>
    <option value="04">Apr</option>
    <option value="05">May</option>
    <option value="06">Jun</option>
    <option value="07">Jul</option>
    <option value="08">Aug</option>
    <option value="09">Sep</option>
    <option value="10">Oct</option>
    <option value="11">Nov</option>
    <option value="12">Dec</option>
    </select>
    <input name="searchtext" type="text" placeholder="Fulltext search" value="<%=request.getAttribute("searchtext")%>"/>
    <input type="submit" value="Search" />
	</form></div>
    <h2>Transcripts</h2>
</div>
<div class="subcontent">
<% for(Transcript transcript : transcripts) { %>
<div class="row">
    <span><%=new SimpleDateFormat("MMM dd, yyyy").format(transcript.getTimeStamp()) %> <%=transcript.getType() %></span>

    <div style="float:right"><a href="<%=JSPHelper.getLink(request, "/api/2.0/transcript/"+transcript.getOid()+".pdf")%>">Download (PDF)</a></div>
    <div style="float:right; padding-right:10px"><a href="<%=JSPHelper.getLink(request, "/transcript/"+transcript.getOid()) %>">View (HTML)</a></div>&nbsp;&nbsp;
</div>
<% } %>
</div>
</div>
</div>
<jsp:include page="/footer.jsp"/>
