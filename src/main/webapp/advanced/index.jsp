<%@ page language="java" import="java.util.*, java.io.*, gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<jsp:include page="/header.jsp">
    <jsp:param name="title" value="Open Legislation - Advanced Search - NY Senate"/>
</jsp:include>

<style>
#advsearchbox {
    width:700px;
    margin:24px auto;
    border:1px solid #ccc;
}

#advsearchbox p {
    margin:12px;
    text-align:left;
}

#advsearchboxheader {
    background:#eee;
    font-size:14pt;
    padding:3px;
}

.searchlabel {
    float:left;
    margin:3px;
    width:200px;
    text-align:right;
}

.searchinput {
    float:left;
}

.searchinput input,.searchinput select {
    width:400px;
    font-size:12pt;
    border:1px solid #ccc;
}

.searchrow {
    clear:left;
}

#search-help {
    padding:0px 10px 15px 10px;
}

#search-help ul {
    list-style:none;
    padding:5px 0px 5px 10px;
}

#advanced-search-help {
    font-style:italic;
}

.term {
    background-color:#EEEEEE;
    padding:1px;
    margin:1px;
}

</style>

<script>
$(document).ready(function() {

    var clearOnFocus = function(element, text) {
        element.focus(function() {
            var self = $(this);
            if (self.val() == text) {
                self.val("");
            }
        }).blur(function() {
            var self = $(this);
            if (self.val() == "") {
                self.val(text);
            }
        }).blur();
    };

    clearOnFocus($("input[name=startdate]"), "mm/dd/yyyy");
    clearOnFocus($("input[name=enddate]"), "mm/dd/yyyy");
});
</script>
 <div id="content">
        <div id="advsearchbox">
        <div id="advsearchboxheader">Open Legislation Advanced Search</div>
         <form method="get" action="/legislation/search/">

        <div id="search-help">
            Use the options below to create your search.
            <br/><br/>
            <div id="advanced-search-help">
                The following several special characters and words (case sensitive) are available for all text fields:
                <ul>
                    <li><b>AND</b>: Require both words. <span class="term">property AND tax</span> matches documents mentioning both property and tax.
                    <li><b>OR</b>: Require at least one of the words. <span class="term">debt OR loan</span> matches documents with the words debt or loan.
                    <li><b>*</b>: Represents any number of additional characters. <span class="term">health*</span> matches health, healthcare, healthy, etc.
                    <li><b>~</b>: Fuzzy search for the preceding word. Can be used if you are unsure of spelling; e.g. <span class="term">Schenectedy~</span>.
                    <li><b>""</b>: Require an exact match. <span class="term">"student loans"</span> finds documents with the exact phrase "student loans".
                </ul>
                If no special words or characters are used OpenLegislation will look for an exact match of the whole value. 
            </div>
        </div>

        <div class="searchrow">
        <div class="searchlabel">Legislative Content Type(s):</div><div class="searchinput">
            <select name="type">
                <option value="">All Types</option>
                <option value="bill">Bills (Senate &amp; Assembly)</option>
                <option value="resolution">Resolutions</option>
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
                <center><b>Bill Actions, Meetings, Calendars and Transcripts (Optional)</b></center>
        <div class="searchrow">
        <div class="searchlabel">Last Updated between:</div><div class="searchinput">
        <input type="text" name="startdate" style="width:125px"/> and <input type="text" name="enddate" style="width:125px"/>
        </div>
        </div>
        <br style="clear:both;"/><br/>
        <center><b>Bills Only (Optional)</b></center>

        <div class="searchrow">
            <div class="searchlabel">Legislative Session Period:</div>
            <div class="searchinput">
                <select name="session">
                    <option value=""></option>
                    <option value="2009">2009-2010</option>
                    <option value="2011">2011-2012</option>
                    <option value="2013">2013-2014</option>
                </select>
            </div>
        </div>
        <div class="searchrow">
            <div class="searchlabel">and Status is:</div>
            <div class="searchinput">
                <select name="status">
                   <option value="" >Any</option>
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

        <center><b>Meetings / Session Transcripts</b></center>

        <div class="searchrow">
        <div class="searchlabel">Physical Location was:</div><div class="searchinput"><input type="text" name="location" value=""/></div>
        </div>


        <br style="clear:both;"/><br/>

        <center><input type="submit" value="Advanced Search"/></center>
            <br style="clear:both;"/><br/>

        </form>
    </div>
 </div>

 <jsp:include page="/footer.jsp"/>