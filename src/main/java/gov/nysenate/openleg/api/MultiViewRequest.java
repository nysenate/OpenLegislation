package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.Action;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.JSPHelper;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MultiViewRequest extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(MultiViewRequest.class);

    String type;

    public MultiViewRequest(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String pageNumber, String pageSize) {
        super(request, response, pageNumber, pageSize, format, getApiEnum(MultiView.values(),type));
        logger.info("New multi view request: format="+format+", type="+type+", page="+pageNumber+", size="+pageSize);
        this.type = type;
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        String urlPath = TextFormatter.append("/legislation/", type, "/");

        String sortField = "when";
        boolean sortOrder = true;

        QueryBuilder queryBuilder = new QueryBuilder();

        if (type.contains("bill") || type.contains("resolution")) {
            sortField = "sortindex";
            sortOrder = false;
        }

        SenateResponse sr = null;

        // now calculate start, end idx based on pageIdx and pageSize
        int start = (pageNumber - 1) * pageSize;

        type = type.substring(0, type.length() - 1);

        try {
            queryBuilder.otype(type).and().current().and().active();
        } catch (QueryBuilderException e) {
            logger.error("Invalid query construction", e);
            throw new ApiRequestException("Invalid query construction", e);
        }

        try {
            sr = Application.getLucene().search(queryBuilder.query(), start, pageSize, sortField, sortOrder);
        }
        catch (IOException e) {
            logger.error(e);
        }

        if(sr == null || sr.getResults() == null || sr.getResults().isEmpty())
            throw new ApiRequestException(TextFormatter.append("no results for query"));

        sr.setResults(ApiHelper.buildSearchResultList(sr));

        if(type.equalsIgnoreCase("bill") && format.matches("(?i)(csv|json|mobile|rss|xml)")) {
            ArrayList<Result> searchResults = ApiHelper.buildSearchResultList(sr);
            ArrayList<Bill> bills = new ArrayList<Bill>();
            for(Result result: searchResults) {
                bills.add((Bill)result.getObject());
            }
            request.setAttribute("bills", bills);
        }
        else {
            request.setAttribute("sortField", sortField);
            request.setAttribute("sortOrder", Boolean.toString(sortOrder));
            request.setAttribute("type", type);
            request.setAttribute("term", queryBuilder.query());
            request.setAttribute("format", format);
            request.setAttribute(PAGE_IDX, pageNumber);
            request.setAttribute(PAGE_SIZE, pageSize);
            request.setAttribute("urlPath", urlPath);
            request.setAttribute("results", sr);
        }

        HashMap<String, String> feeds = new HashMap<String, String>();
        feeds.put(type+" Feed", JSPHelper.getFullLink(request, "/search/?format=atom&amp;term="+queryBuilder.query()+"&amp;title="+StringUtils.capitalize(type+" Feed")));
        request.setAttribute("feeds", feeds);
    }

    @Override
    public String getView() {
        String vFormat = format.equals("jsonp") ? "json" : format;
        if(type.equalsIgnoreCase("bill")
                && vFormat.matches("(?i)(csv|json|mobile|rss|xml)")) {
            return TextFormatter.append("/views/bills-", vFormat, ".jsp");
        }
        else {
            return TextFormatter.append("/views/search-", vFormat, ".jsp");
        }
    }

    @Override
    public boolean hasParameters() {
        return type!= null;
    }

    public enum MultiView implements ApiEnum {
        BILLS		("bills", 		Bill.class, 		new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        RESOLUTIONS ("resolutions", Bill.class,   new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        CALENDARS	("calendars", 	Calendar.class, 	new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        MEETINGS	("meetings", 	Meeting.class, 		new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        TRANSCRIPTS	("transcripts", Transcript.class, 	new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        VOTES		("votes", 		Vote.class, 		new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        ACTIONS		("actions", 	Action.class, 	new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"});

        public final String view;
        public final Class<? extends BaseObject> clazz;
        public final String[] formats;

        private MultiView(final String view, final Class<? extends BaseObject> clazz, final String[] formats) {
            this.view = view;
            this.clazz = clazz;
            this.formats = formats;
        }

        @Override
        public String view() {
            return view;
        }
        @Override
        public String[] formats() {
            return formats;
        }
        @Override
        public Class<? extends BaseObject> clazz() {
            return clazz;
        }
    }
}