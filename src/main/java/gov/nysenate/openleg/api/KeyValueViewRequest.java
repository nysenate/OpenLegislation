package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Result;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class KeyValueViewRequest extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(KeyValueViewRequest.class);

    String key;
    String value;

    public KeyValueViewRequest(HttpServletRequest request, HttpServletResponse response,
            String format, String key, String value, String pageNumber, String pageSize) {
        super(request, response, pageNumber, pageSize, format, getApiEnum(KeyValueView.values(),key));
        logger.info("New key value request: format="+format+", key="+key+", value="+value+", page="+pageNumber+", size="+pageSize);
        this.key = key;
        this.value = value;
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        String urlPath = TextFormatter.append("/legislation/", key, "/", value, "/");

        String sortField = "sortindex";
        boolean sortOrder = false;

        QueryBuilder queryBuilder = new QueryBuilder();



        SenateResponse sr = null;

        // now calculate start, end idx based on pageIdx and pageSize
        int start = (pageNumber - 1) * pageSize;

        try {
            queryBuilder.keyValue(key, "\""+value+"\"").and().otype("bill");

            String filter = request.getParameter("filter");
            if(filter != null) {
                queryBuilder.and().insertAfter(filter);
            }
            else {
                queryBuilder.and().current().and().active();
            }
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

        if(format.matches("(?i)(csv|json|mobile|rss|xml)")) {
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
            request.setAttribute("type", key);
            request.setAttribute("term", queryBuilder.query());
            request.setAttribute("format", format);
            request.setAttribute(PAGE_IDX, pageNumber);
            request.setAttribute(PAGE_SIZE, pageSize);
            request.setAttribute("urlPath", urlPath);
            request.setAttribute("results", sr);
        }
    }

    @Override
    public String getView() {
        String vFormat = format.equals("jsonp") ? "json" : format;
        if(vFormat.matches("(?i)(csv|json|mobile|rss|xml)")) {
            return TextFormatter.append("/views/bills-", vFormat, ".jsp");
        }
        else {
            return TextFormatter.append("/views/search-", vFormat, ".jsp");
        }
    }

    @Override
    public boolean hasParameters() {
        return key != null && value != null;
    }

    public enum KeyValueView implements ApiEnum {
        SPONSOR("sponsor", 		Bill.class, 	new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"}),
        COMMITTEE("committee", 	Bill.class, 	new String[] {"html", "json", "jsonp", "xml", "rss", "csv", "html-list"});

        public final String view;
        public final Class<? extends BaseObject> clazz;
        public final String[] formats;

        private KeyValueView(final String view, final Class<? extends BaseObject> clazz, final String[] formats) {
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
