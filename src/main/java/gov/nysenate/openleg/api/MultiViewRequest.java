package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.servlets.ApiServlet.ApiEnum;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.TextFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultiViewRequest extends AbstractApiRequest {
	String format;
	String type;
	
	public MultiViewRequest(HttpServletRequest request, HttpServletResponse response,
			String format, String type, String pageNumber, String pageSize) {
		super(request, response, pageNumber, pageSize, getApiEnum(MultiView.values(),type));
		this.format = thisOrThat(format, DEFAULT_FORMAT);
		this.type = type;
	}
	
	@Override
	public void fillRequest() {
		String urlPath = TextFormatter.append("/legislation/", type, "/");

		String sFormat = "json";
		String sortField = "when";
		boolean sortOrder = true;
		
		QueryBuilder queryBuilder = new QueryBuilder();

		if (type.contains("bill")) {
			sortField = "sortindex";
			sortOrder = false;
		} else if (type.contains("meeting")) {
			sortField = "sortindex";
		}

		SenateResponse sr = null;

		// now calculate start, end idx based on pageIdx and pageSize
		int start = (pageNumber - 1) * pageSize;

		try {
			type = type.substring(0, type.length() - 1);

			queryBuilder.otype(type).and().current().and().active();
			
			sr = SearchEngine.getInstance().search(queryBuilder.query(), sFormat,
					start, pageSize, sortField, sortOrder);

			sr.setResults(ApiHelper.buildSearchResultList(sr));
			
			request.setAttribute("sortField", sortField);
			request.setAttribute("sortOrder", Boolean.toString(sortOrder));
			request.setAttribute("type", type);
			request.setAttribute("term", queryBuilder.query());
			request.setAttribute("format", format);
			request.setAttribute(PAGE_IDX, pageNumber + "");
			request.setAttribute(PAGE_SIZE, pageSize + "");
			request.setAttribute("urlPath", urlPath);
			request.setAttribute("results", sr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getView() {
		return TextFormatter.append("/views/search-", format, ".jsp");
	}
	
	@Override
	public boolean isValid() {
		return type!= null;
	}
	
	public enum MultiView implements ApiEnum {
		BILLS		("bills", 		Bill.class, 		new String[] {"html", "json", "xml", "rss"}),
		CALENDARS	("calendars", 	Calendar.class, 	new String[] {"html", "json", "xml", "rss"}),
		MEETINGS	("meetings", 	Meeting.class, 		new String[] {"html", "json", "xml", "rss"}),
		TRANSCRIPTS	("transcripts", Transcript.class, 	new String[] {"html", "json", "xml", "rss"}),
		VOTES		("votes", 		Vote.class, 		new String[] {"html", "json", "xml", "rss"}),
		ACTIONS		("actions", 	BillEvent.class, 	new String[] {"html", "json", "xml", "rss"});
		
		public final String view;
		public final Class<? extends SenateObject> clazz;
		public final String[] formats;
		
		private MultiView(final String view, final Class<? extends SenateObject> clazz, final String[] formats) {
			this.view = view;
			this.clazz = clazz;
			this.formats = formats;
		}
		
		public String view() {
			return view;
		}
		public String[] formats() {
			return formats;
		}
		public Class<? extends SenateObject> clazz() {
			return clazz;
		}
	}
}