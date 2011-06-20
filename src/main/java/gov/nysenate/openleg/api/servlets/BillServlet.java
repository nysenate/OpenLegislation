package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.QueryBuilder;
import gov.nysenate.openleg.api.QueryBuilder.QueryBuilderException;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.search.SearchEngine;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

public class BillServlet extends AbstractSenateObjectServlet<Bill> {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doRelated(String oid, HttpServletRequest request) {
		try {
			String rType = "action";
			String rQuery = QueryBuilder.build().otype(rType).and().relatedBills("billno", oid).query();
			ArrayList<BillEvent> billEvents = SearchEngine.getInstance().getSenateObjects(rQuery, BillEvent.class);
			request.setAttribute("related-" + rType, billEvents);
	
			rType = "bill";
			rQuery = QueryBuilder.build().otype(rType).and().relatedBills("oid", oid).query();
			ArrayList<Bill> bills = SearchEngine.getInstance().getSenateObjects(rQuery, Bill.class);
			request.setAttribute("related-" + rType, bills);
	
			rType = "meeting";
			rQuery = QueryBuilder.build().otype(rType).and().keyValue("bills", oid).query();
			ArrayList<Meeting> meetings = SearchEngine.getInstance().getSenateObjects(rQuery, Meeting.class);
			request.setAttribute("related-" + rType, meetings);
			
			rType = "calendar";
			rQuery = QueryBuilder.build().otype(rType).and().keyValue("bills", oid).query();
			ArrayList<Calendar> calendars = SearchEngine.getInstance().getSenateObjects(rQuery, Calendar.class);
			request.setAttribute("related-" + rType, calendars);
			
			rType = "vote";
			rQuery = QueryBuilder.build().otype(rType).and().keyValue("billno", oid).query();
			ArrayList<Vote> votes = SearchEngine.getInstance().getSenateObjects(rQuery, Vote.class);
			request.setAttribute("related-" + rType, votes);
			
		} catch (QueryBuilderException e) {
			e.printStackTrace();
		}
	}
}
