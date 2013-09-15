<%@ page language="java" import="java.net.URLEncoder, gov.nysenate.openleg.util.JSPHelper, gov.nysenate.openleg.util.OpenLegConstants, org.apache.abdera.model.Entry, org.apache.abdera.Abdera, org.apache.abdera.model.Feed, java.util.*, gov.nysenate.openleg.model.*"  contentType="application/atom+xml" pageEncoding="utf-8" %><%
    String[] attribs = {"billno","sponsor","cosponsors","notes","when","sameas","committee","status","location","session-type","chair","summary"};

    String term = (String)request.getAttribute("term");
    SenateResponse sr = (SenateResponse)request.getAttribute("results");

    int total = (Integer)sr.getMetadataByKey("totalresults");
    int pageIdx = (Integer)request.getAttribute(OpenLegConstants.PAGE_IDX);
    int pageSize = (Integer)request.getAttribute(OpenLegConstants.PAGE_SIZE);

    String baseUrl = JSPHelper.getFullLink(request, "/search/"+URLEncoder.encode(term, "utf8"));
    String currentUrl =  baseUrl + "/" + pageIdx + "/" + pageSize;
    String feedUrl = currentUrl+"?format=atom";

    String title = request.getParameter("title");
    if (title == null) {
        title = "Feed: "+term;
    }

    // RFC4287
    Abdera abdera = new Abdera();
    Feed feed = abdera.newFeed();
    feed.setId(currentUrl);
    feed.setTitle(title+" - NY Senate Open Legislation");
    feed.addLink(feedUrl, "self");
    feed.addLink(currentUrl, "alternate");

    // RFC5005
    int pages = (int)Math.ceil(total/(double)pageSize);
    feed.addLink(baseUrl + "/" + 0 + "/" + pageSize + "?format=atom", "first");
    feed.addLink(baseUrl + "/" + pages + "/" + pageSize + "?format=atom", "last");
    if(pageIdx != 0) {
        feed.addLink(baseUrl + "/" + (pageIdx-1) + "/" + pageSize + "?format=atom", "previous");
    }
    if(pageIdx != pages) {
        feed.addLink(baseUrl + "/" + (pageIdx+1) + "/" + pageSize + "?format=atom", "next");
    }

    // Use the most recently modified result data if available
    if (sr.getResults().size() > 0) {
        feed.setUpdated(new Date(sr.getResults().get(0).getLastModified()));
    }
    else {
        feed.setUpdated(new Date());
    }
    System.out.println(sr.getResults().size());
    for(Result r:sr.getResults()) {
        Entry entry = feed.addEntry();

        String contentType = r.getOtype();
        String contentId = r.getOid().toUpperCase();
        String otype = contentType;
        String oid = contentId;

        if (contentType.equals("vote")) {
            otype = "bill";
            oid = (String)r.getFields().get("billno");
        }
        else if (contentType.equals("action")) {
            otype = "bill";
            oid = (String)r.getFields().get("billno");
        }

        String resultTitle = r.getTitle();
        if (contentType.equals("bill")) {
            resultTitle = contentType.toUpperCase() + " - " + contentId + " - " + resultTitle;
        }
        else {
            resultTitle = contentType.toUpperCase() + " - " + resultTitle;
        }

        StringBuilder resultSummary = new StringBuilder();
        for (int i = 0; i < attribs.length; i++){
            if (r.getFields().get(attribs[i])!=null){
                resultSummary.append(attribs[i].trim());
                resultSummary.append(":");
                resultSummary.append(r.getFields().get(attribs[i]).trim());
                resultSummary.append("; ");
            }
        }

        if (r.getSummary() != null && r.getSummary().trim().length()>0) {
            resultSummary.append("Summary:");
            resultSummary.append(r.getSummary().trim());
            resultSummary.append(";");
        }

        oid = oid.replace(" ", "+");
        String entryUrl = JSPHelper.getFullLink(request, "/" + otype + "/" + oid);
        entry.setId(entryUrl+"#"+oid);
        entry.setTitle(resultTitle);
        entry.addAuthor("NYSenate");
        entry.setContent(resultSummary.toString().replaceAll(";$",""));
        entry.setUpdated(new Date(r.getLastModified()));
        entry.setEdited(new Date(r.getLastModified()));
        entry.addLink(entryUrl, "self");
        entry.complete();
    }
    out.print(abdera.getWriter().write(feed));
%>