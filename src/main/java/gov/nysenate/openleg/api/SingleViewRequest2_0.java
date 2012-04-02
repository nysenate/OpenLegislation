package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.api.SingleViewRequest.SingleView;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class SingleViewRequest2_0 extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SingleViewRequest2_0.class);

    String type;
    String id;

    public SingleViewRequest2_0(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String id) {
        super(request, response, 1, 1, format, getApiEnum(SingleView.values(),type));
        this.type = type;
        this.id = id;
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        String vFormat = format.equals("jsonp") ? "json" : format;

        request.setAttribute("format", vFormat);

        String term = id;
        if(type.equals("bill")) {
            term = Bill.formatBillNo(term);
        }
        try {
            QueryBuilder queryBuilder = QueryBuilder.build().otype(type);

            if(term != null && !term.matches("\\s*")) queryBuilder.and().oid(term);

            term = queryBuilder.query();
        }
        catch(QueryBuilderException e) {
            logger.error(e);
        }

        int start = (pageNumber - 1) * pageSize;

        try {
            SenateResponse sr = SearchEngine.getInstance().search(term, vFormat, start, pageSize, null, false);

            request.setAttribute("results", sr);
        }
        catch (ParseException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public String getView() {
        return "/views2/v2-api.jsp";
    }

    @Override
    public boolean hasParameters() {
        return type != null && id != null;
    }

    public enum SingleView2_0 implements ApiEnum {
        BILL		("bill",		Bill.class, 		new String[] {"json", "jsonp", "xml"}),
        CALENDAR	("calendar",	Calendar.class, 	new String[] {"json", "jsonp", "xml"}),
        MEETING		("meeting", 	Meeting.class, 		new String[] {"json", "jsonp", "xml"}),
        TRANSCRIPT	("transcript", 	Transcript.class, 	new String[] {"json", "jsonp", "xml"});

        public final String view;
        public final Class<? extends SenateObject> clazz;
        public final String[] formats;

        private SingleView2_0(final String view, final Class<? extends SenateObject> clazz, final String[] formats) {
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
        public Class<? extends SenateObject> clazz() {
            return clazz;
        }
    }
}
