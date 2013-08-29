<%@ page language="java" import="java.util.ArrayList, java.util.Iterator, java.util.List, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.api.*,gov.nysenate.openleg.lucene.*,gov.nysenate.openleg.model.*,gov.nysenate.openleg.util.*"  contentType="text/html" pageEncoding="utf-8" %>
<!DOCTYPE HTML>
<html>
    <head>
    <base target="_blank"/>
    <title>Active List</title>
    <link rel="shortcut icon" href="<%=JSPHelper.getLink(request, "/static/img/nys_favicon_0.ico")%>" type="image/x-icon" /> 
    <link rel="stylesheet" type="text/css" media="screen" href="<%=JSPHelper.getLink(request, "/static/css/style.css")%>"/> 
    <link rel="stylesheet" type="text/css" media="screen" href="<%=JSPHelper.getLink(request, "/static/css/style-mobile.css")%>"/> 
    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;" />
    <meta name="apple-mobile-web-app-capable" content="YES">
    <style>
    body {
       font-size:80%;
    }
    ul {
        margin:0px;
        padding:0px;
    }
    li {
        border-bottom:1px solid #ccc;
        list-style-type:none;
        margin:0px;
        padding-top:3px;
    }
    .widget-narrow {
       width:150px;
    }
    </style>
</head>
<body>  
    <div class="widget-narrow">
        <%
        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);

        Lucene lucene = Application.getLucene();
        SenateResponse senateResponse = lucene.search("ctype:active", 0, 1, "when", true);
        Result result = ApiHelper.buildSearchResultList(senateResponse).get(0);

        Calendar calendar = (Calendar)result.getObject();
        Supplemental supp = calendar.getSupplementals().get(0);
        %>
        <h4>New York Senate<br/>Active List (Cal <%=calendar.getNo()%>)
            for
            <% if (supp.getCalendarDate()!=null) { %>
                <%=df.format(supp.getCalendarDate())%>
            <% } else if (supp.getSequences()!=null && !supp.getSequences().isEmpty()) {%>
                <%=df.format(supp.getSequences().get(0).getActCalDate())%>
            <% } %>
        </h4>
        <div style="background:#00CCFF;font-size:7pt;padding:1px;">
            Updated every 30 minutes or less
        </div>
        <div id="content">
            <% if (supp.getSequences()!=null) {
                for(Sequence seq:supp.getSequences()) {
                    String seqTitle = "Active List "+calendar.getNo();
                    if (!seq.getNo().isEmpty()) {
                        seqTitle += "-"+seq.getNo();
                    }
                    %>
                    <h2><%=seqTitle %></h2>
                    <ul>
                    <%
                    for (CalendarEntry calEnt : seq.getCalendarEntries()) {
                        try { %>
                        <li>
                        Cal No. <%=calEnt.getNo()%>
                        <%
                            if (calEnt.getBill()!=null && calEnt.getBill().getBillId()!=null ) {
                        %>
                            / Bill: <a href="<%=JSPHelper.getLink(request,"/api/1.0/html/bill/"+calEnt.getBill().getBillId())%>" target="_blank"><%=calEnt.getBill().getBillId()%></a>
                            / <a href="<%=JSPHelper.getLink(request,"/search?term=sponsor:"+calEnt.getBill().getSponsor().getFullname())%>" target="_blank"><%=calEnt.getBill().getSponsor().getFullname()%></a>
                            <%
                                if (calEnt.getSubBill()!=null) {
                            %>
                                (Sub-bill Sponsor: <a href="<%=JSPHelper.getLink(request, "/search?term=sponsor:"+calEnt.getSubBill().getSponsor().getFullname())%>"><%=calEnt.getSubBill().getSponsor().getFullname()%></a>)
                            <%
                                }
                            %>
                            <%
                                if (calEnt.getBillHigh()!=null) {
                            %>
                                <b style="color:green">HIGH</b>
                            <%
                                }
                            %>
                            <%
                                if (calEnt.getSubBill()!=null) {
                            %>
                                (Sub-bill: <a href="<%=JSPHelper.getLink(request, "/api/1.0/mobile/bill/"+calEnt.getSubBill().getBillId())%>"><%=calEnt.getSubBill().getBillId()%></a>)
                            <% } %>
                            <div style="font-size:80%">
                                <%if (calEnt.getBill().getTitle()!=null) { %>
                                    <%=calEnt.getBill().getTitle()%>
                                <% } else if (calEnt.getSubBill()!=null && calEnt.getSubBill().getTitle()!=null) { %>
                                    <%=calEnt.getSubBill().getTitle()%>
                                <% } %>
                            </div>
                        <% }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            %></li><%
                        }
                    } %>
                    </ul>
                <% }
            } %>
        </div>
    </div>
</body>
</html>
