package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.search.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

public class BillServlet extends AbstractSenateObjectServlet<Bill> {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doRelated(String oid, HttpServletRequest request) {
		String billQueryId = oid;
		String sessionYear = DEFAULT_SESSION_YEAR;
		
		/* default behavior to maintain previous permalinks
		 * is if key=S1234 to transform to S1234-2009
		 * in line with our new bill oid format <billno>-<sessYear> */
		String[] billParts = billQueryId.split("-");
		billQueryId = billParts[0];
		if (billParts.length > 1)
			sessionYear = billParts[1];
	
		/* turns S1234A in to S1234 */
		String billWildcard = billQueryId.replaceAll("[a-zA-Z]$", "");
			
		//get BillEvents for this 
		//otype:action AND billno:((S1234-2011 OR [S1234A-2011 TO S1234Z-2011) AND S1234*-2011)
		String rType = "action";
		String rQuery = null;
		rQuery = ApiHelper.buildBillWildCardQuery("billno", billWildcard, sessionYear);
		
		ArrayList<Result> relatedActions = ApiHelper.getRelatedSenateObjects(rType,rQuery);
		Hashtable<String,Result> uniqResults = new Hashtable<String,Result>();
		for (Result rResult: relatedActions) {
			BillEvent rAction = (BillEvent)rResult.getObject();
			uniqResults.put(rAction.getEventDate().getTime()+'-'+rResult.getTitle().toUpperCase(), rResult);
		}
		ArrayList<Result> list = Collections.list(uniqResults.elements());
		request.setAttribute("related-" + rType, list);

		//get sameas bills (e.g. for S1234A get S1234)
		//otype:bill AND oid:((S1234-2011 OR [S1234A-2011 TO S1234Z-2011) AND S1234*-2011)
		rType = "bill";
		rQuery = ApiHelper.buildBillWildCardQuery("oid", billWildcard, sessionYear);
		request.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));

		//get Meetings
		rType = "meeting";
		rQuery = "bills:\"" + oid + "\"";					
		request.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
		
		//get calendars
		rType = "calendar";
		rQuery = "bills:\"" + oid + "\"";
		request.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
		
		//get votes
		rType = "vote";
		rQuery = "billno:\"" + oid + "\"";
		request.setAttribute("related-" + rType, ApiHelper.getRelatedSenateObjects(rType,rQuery));
	}
}
