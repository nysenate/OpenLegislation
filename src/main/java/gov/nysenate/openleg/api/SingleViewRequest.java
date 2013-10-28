package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.BaseObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.Application;
import gov.nysenate.openleg.util.TextFormatter;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SingleViewRequest extends AbstractApiRequest {
    private final Logger logger = Logger.getLogger(SingleViewRequest.class);

    String type;
    String id;

    public SingleViewRequest(HttpServletRequest request, HttpServletResponse response,
            String format, String type, String id) {
        super(request, response, 1, 1, format, getApiEnum(SingleView.values(),type));
        logger.info("New single view request: format="+format+", type="+type+", id="+id);
        this.type = type;
        this.id = id;
    }

    @Override
    public void fillRequest() throws ApiRequestException {
        IBaseObject so = Application.getLucene().getSenateObject(id, type);

        if(so == null) {
            throw new ApiRequestException(TextFormatter.append("couldn't find id: ", id, " of type: ", type));
        }

        request.setAttribute(type , so);

        try {
            if(type.equals("bill") && !format.matches("(csv|json|xml)")) {
                Bill bill = (Bill) so;
                request.setAttribute("related-action", bill.getActions());
                request.setAttribute("related-vote", bill.getVotes());

                String rType = "bill";
                String rQuery = QueryBuilder.build().otype(rType).and().relatedBills("oid", id).query();
                ArrayList<Bill> bills = Application.getLucene().getSenateObjects(rQuery);
                request.setAttribute("related-" + rType, bills);

                rType = "meeting";
                rQuery = QueryBuilder.build().otype(rType).and().keyValue("bills", id).query();
                ArrayList<Meeting> meetings = Application.getLucene().getSenateObjects(rQuery);
                request.setAttribute("related-" + rType, meetings);

                rType = "calendar";
                rQuery = QueryBuilder.build().otype(rType).and().keyValue("bills", id).query();
                ArrayList<Calendar> calendars = Application.getLucene().getSenateObjects(rQuery);
                request.setAttribute("related-" + rType, calendars);
            }
        } catch (QueryBuilderException e) {
            logger.error(e);
        }
    }

    @Override
    public String getView() {
        String vFormat = format.equals("jsonp") ? "json" : format;
        return TextFormatter.append("/views/", type, "-", vFormat, ".jsp");
    }

    @Override
    public boolean hasParameters() {
        return type != null && id != null;
    }

    public enum SingleView implements ApiEnum {
        BILL		("bill",		Bill.class, 		new String[] {"html", "json", "jsonp", "mobile", "xml",
            "csv", "html-print", "lrs-print"}),
            CALENDAR	("calendar",	Calendar.class, 	new String[] {"html", "json", "jsonp", "mobile", "xml"}),
            MEETING		("meeting", 	Meeting.class, 		new String[] {"html", "json", "jsonp", "mobile", "xml"}),
            TRANSCRIPT	("transcript", 	Transcript.class, 	new String[] {"html", "json", "jsonp", "mobile", "xml"});

        public final String view;
        public final Class<? extends BaseObject> clazz;
        public final String[] formats;

        private SingleView(final String view, final Class<? extends BaseObject> clazz, final String[] formats) {
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
