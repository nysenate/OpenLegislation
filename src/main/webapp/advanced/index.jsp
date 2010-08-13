<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Open Legislation - Advanced Search - NY Senate"/>
</jsp:include>

<style>
#advsearchbox
{
width:700px;
margin:24px;
border:1px solid #ccc;
}

#advsearchbox p
{
margin:12px;
text-align:left;
}

#advsearchboxheader
{
background:#eee;
font-size:14pt;
padding:3px;
}

.searchlabel
{
float:left;
margin:3px;
width:200px;
text-align:right;
}

.searchinput
{
float:left;
}

.searchinput input,.searchinput select
{
width:400px;
font-size:12pt;
border:1px solid #ccc;
}

.searchrow
{
clear:left;
}

#content
{
text-align:center;
}
</style>
 <div id="content">
 
		<center>
		<div id="advsearchbox">
		<div id="advsearchboxheader">Open Legislation Advanced Search</div>
		 <form method="get" action="/legislation/search/">
		
		<p>
		<em>In the text boxes below you can use AND or OR to require all or any of theop words you enter to be matched.
		In addition you can use a * character ("S12*") as a wildcard or a ~ character if you are unsure of the spelling ("Schenectedy~").
		</em>
		</p>
		
		<style>
		input[type='checkbox'],input.checkbox
		{
		width:20px;
		}
		</style>
		<div class="searchrow">
		<div class="searchlabel">Legislative Content Type(s):</div><div class="searchinput">
		

		    <select name="type">
                <option value="">All Types</option>
                <option value="bill">Bills, Resolutions, etc. (Senate &amp; Assembly)</option>
                <option value="transcript">Floor Transcripts</option>
                <option value="meeting">Committee Meetings</option>
                <option value="calendar">Floor &amp; Active Calendars</option>
                <option value="action">Bill Actions</option>
                <option value="vote">Floor &amp; Committee Votes</option>
                </select>
		
		
		</div>
		</div>
		
		<div class="searchrow">
		<div class="searchlabel">Full text search:</div><div class="searchinput"><input type="text" name="full" value=""/></div>
		</div>
			<div class="searchrow">
		<div class="searchlabel">Order By:</div><div class="searchinput">
		<select name="sort">
			<option value="">Best Match</option>
			<option value="when">Most Recent</option>
						<option value="title">Title</option>
			
						<option value="sponsor">Sponsor</option>
			<option value="oid">Identifier</option>
		</select>
		</div>
		</div>
		<br style="clear:both;"/><br/>
				<b>Bill Actions, Meetings, Calendars and Transcripts</b>
		<div class="searchrow">
		<div class="searchlabel">Last Updated between:</div><div class="searchinput">
		<input type="text" name="startdate" value="mm/dd/yyyy" style="width:125px"/> and <input type="text" name="enddate" value="mm/dd/yyyy"  style="width:125px"/>
		 (optional)
		</div>
		</div>
		<br style="clear:both;"/><br/>
		<b>Bills Only (Optional)</b>
		
		<div class="searchrow">
		<div class="searchlabel">and Status is:</div><div class="searchinput">
		<select name="status">
		   <option value="" >All Status</option>
                <option value="Passed" >Passed</option>
                
		    <option value="Vetoed" >Vetoed</option>
			    <option value="REFERRED" >Referred</option>
                <option value="Reported" >Reported</option>
                <option value="Delivered" >Delivered</option>
                <option value="Home Rule Request" >Home Rule Request</option>
                <option value="1st Report" >1st Report</option>
                <option value="2nd Report" >2nd Report</option>
                <option value="Third Reading" >Third Reading</option>
                <option value="Delivered to Assembly" >Delivered</option>
                <option value="Amended" >Amended</option>
                <option value="Substituted" >Substituted</option>
                <option value="Vote Reconsidered" >Vote Reconsidered</option>
                <option value="Tabled" >Tabled</option>
               
                <option value="Delivered to Governor" >Delivered to Governor</option>
                <option value="Signed" >Signed</option>
                 <option value="Adopted" >Adopted</option>
		</select>
		</div>
		</div>
		
		<div class="searchrow">
		<div class="searchlabel">and Sponsor Memo matches:</div><div class="searchinput"><input type="text" name="memo" value=""/></div>
		</div>
		
		<div class="searchrow">
		<div class="searchlabel">and "Same As" Bill:</div><div class="searchinput"><input type="text" name="sameas" value=""/></div>
		</div>
		

		<div class="searchrow"><div class="searchlabel">and Sponsor is:</div><div class="searchinput"><input type="text" name="sponsor" value=""/></div></div>
				
		<div class="searchrow"><div class="searchlabel">and Co-Sponsors include:</div><div class="searchinput"><input type="text" name="cosponsors" value=""/></div></div>
				
				<div class="searchrow">
		
		<div class="searchlabel">and current Committee is:</div><div class="searchinput"><input type="text" name="committee" value=""/></div>
			</div>
			
		<br style="clear:both;"/><br/>
		
		<b>Meetings / Session Transcripts</b>
		
		<div class="searchrow">
		<div class="searchlabel">Physical Location was:</div><div class="searchinput"><input type="text" name="location" value=""/></div>
		</div>
		
		
		<br style="clear:both;"/><br/>
		
		<input type="submit" value="Advanced Search"/>
			<br style="clear:both;"/><br/>
			
	</form>
		</div>
		</center>
		


 </div>
   
 <jsp:include page="/footer.jsp"/>