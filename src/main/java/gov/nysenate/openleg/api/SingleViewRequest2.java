package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.api.SingleViewRequest.SingleView;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.SenateResponse;
import gov.nysenate.openleg.util.Application;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;

public class SingleViewRequest2 extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SingleViewRequest2.class);

    String type;
    String id;

    public SingleViewRequest2(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String id, int pageIdx, int pageSize, String sort, boolean sortOrder) {
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
            SenateResponse sr = Application.getLucene().search(term, vFormat, start, pageSize, null, false);

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
}
