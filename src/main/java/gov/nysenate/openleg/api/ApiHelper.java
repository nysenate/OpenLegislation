package gov.nysenate.openleg.api;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.codehaus.jackson.map.ObjectMapper;

public class ApiHelper implements OpenLegConstants {
	private final static DateFormat DATE_FORMAT_MED = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	private final static DateFormat DATE_FORMAT_CUSTOM = new SimpleDateFormat("MMM d, yyyy");
	
	private static Logger logger = Logger.getLogger(ApiHelper.class);
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static ObjectMapper getMapper() {
		if(mapper == null)
			mapper = new ObjectMapper();
		
		return mapper;
	}

	public static ArrayList<Result> getRelatedSenateObjects(String type,
			String query) throws ParseException, IOException,
			ClassNotFoundException {

		String searchString =TextFormatter.append("otype:", type, " AND ", "(" + query + ")");

		int start = 0;
		int pageSize = 100;

		SenateResponse sr = SearchEngine.getInstance().search(dateReplace(searchString), 
				DEFAULT_SEARCH_FORMAT, start, pageSize, DEFAULT_SORT_FIELD, true);

		return buildSearchResultList(sr);
	}

	public static ArrayList<Result> buildSearchResultList(
			SenateResponse sr) throws ClassNotFoundException {
		
		ArrayList<Result> resultList = new ArrayList<Result>();
		
		if (sr.getResults() == null || sr.getResults().isEmpty())
			return resultList;

		for (Result result : sr.getResults()) {
			try {
				String type = result.getOtype();
				String jsonData = result.getData();

				if (jsonData == null)
					continue;

				jsonData = unwrapJson(jsonData);
				
				ApiType apiType = getApiType(type);
				Class<? extends ILuceneObject> clazz = apiType.clazz();

				Object resultObj = null;
				try {
					resultObj = mapper.readValue(jsonData, clazz);
					result.setObject(resultObj);
				} catch (Exception e) {
					logger.warn("error binding:" + clazz.getName(), e);
				}

				if (resultObj == null)
					continue;

				String title = "";
				String summary = "";

				HashMap<String, String> fields = new HashMap<String, String>();
				fields.put("type", type);
				
				/*
				 * populate result objects with any relevant fields, this
				 * provides our more generic, non type-specific search
				 */
				if (type.equals("bill")) {
					Bill bill = (Bill) resultObj;

					if (bill.getTitle() != null)
						title += bill.getTitle();
					else
						title += "(no title)";

					if (bill.getSponsor() != null)
						fields.put("sponsor", bill.getSponsor().getFullname());

					summary = bill.getSummary();

					fields.put("committee", bill.getCurrentCommittee());
					fields.put("billno", bill.getSenateBillNo());
					fields.put("summary", bill.getSummary());
					fields.put("year", bill.getYear() + "");
				} else if (type.equals("calendar")) {
					Calendar calendar = (Calendar) resultObj;

					title = calendar.getNo() + "-" + calendar.getYear();

					if (calendar.getType() == null)
						fields.put("type", "");
					else if (calendar.getType().equals("active"))
						fields.put("type", "Active List");
					else if (calendar.getType().equals("floor"))
						fields.put("type", "Floor Calendar");
					else
						fields.put("type", calendar.getType());

					Supplemental supp = calendar.getSupplementals().get(0);

					if (supp.getCalendarDate() != null) {
						fields.put("date", DATE_FORMAT_CUSTOM.format(supp.getCalendarDate()));

						summary = "";

						if (supp.getSections() != null) {
							Iterator<Section> itSections = supp.getSections()
									.iterator();
							while (itSections.hasNext()) {
								Section section = itSections.next();

								summary += section.getName() + ": ";
								summary += section.getCalendarEntries().size() + " items;";
							}
						}
					} else if (supp.getSequence() != null) {

						fields.put("date", DATE_FORMAT_CUSTOM.format(supp.getSequence().getActCalDate()));

						summary = supp.getSequence().getCalendarEntries().size() + " item(s)";
					}
				} else if (type.equals("transcript")) {
					Transcript transcript = (Transcript) resultObj;

					if (transcript.getTimeStamp() != null)
						title = DATE_FORMAT_CUSTOM.format(transcript.getTimeStamp());
					else
						title = "Transcript - " + transcript.getLocation();

					summary = TextFormatter.append(transcript.getType(), ": ", transcript.getLocation());

					fields.put("location", transcript.getLocation());

				} else if (type.equals("meeting")) {
					Meeting meeting = (Meeting) resultObj;
					title = TextFormatter.append(meeting.getCommitteeName(), " (", 
							meeting.getMeetingDateTime().toLocaleString(), ")");

					fields.put("location", meeting.getLocation());
					fields.put("chair", meeting.getCommitteeChair());
					fields.put("committee", meeting.getCommitteeName());

					summary = meeting.getNotes();
				} else if (type.equals("action")) {
					BillEvent billEvent = (BillEvent) resultObj;
					String billId = billEvent.getBillId();

					title = billEvent.getEventText();

					fields.put("date", DATE_FORMAT_MED.format(billEvent
							.getEventDate()));
					fields.put("billno", billId);
				} else if (type.equals("vote")) {
					Vote vote = (Vote) resultObj;

					if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
						fields.put("type", "Committee Vote");
					else if (vote.getVoteType() == Vote.VOTE_TYPE_FLOOR)
						fields.put("type", "Floor Vote");

					if (vote.getBill() != null) {
						Bill bill = vote.getBill();

						if (bill.getSponsor() != null)
							fields.put("sponsor", bill.getSponsor()
									.getFullname());

						if (vote.getVoteType() == Vote.VOTE_TYPE_COMMITTEE)
							fields.put("committee", bill.getCurrentCommittee());

						fields.put("billno", bill.getSenateBillNo());
						fields.put("year", bill.getYear() + "");
					}

					title += vote.getVoteDate().toLocaleString();

					summary = vote.getDescription();
				}
				
				result.setTitle(title);
				result.setSummary(summary);
				result.setFields(fields);
			} catch (Exception e) {
				logger.warn(TextFormatter.append(
						"problem parsing result: ", result.getOtype(), "-", result.getOid()),
					e);
			}
		}

		return sr.getResults();
	}
	
	public static String dateReplace(String term) throws ParseException {
		Pattern  p = Pattern.compile("(\\d{1,2}[-]?){2}(\\d{2,4})T\\d{2}-\\d{2}");
		Matcher m = p.matcher(term);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy'T'KK-mm");
		
		while(m.find()) {
			String d = term.substring(m.start(),m.end());
			
			Date date = null;
			try {
				date = sdf.parse(d);
				term = term.substring(0, m.start()) + date.getTime() + term.substring(m.end());
			} catch (java.text.ParseException e) {
				logger.warn(e);
			}
			
			m.reset(term);
			
		}
		
		return term;
	}
	
	public static String unwrapJson(String jsonData) {
		jsonData = jsonData.substring(jsonData.indexOf(":")+1);
		jsonData = jsonData.substring(0,jsonData.lastIndexOf("}"));
		return jsonData;
	}
	
	public static ApiType getApiType(String type) {
		for(ApiType apiType:ApiType.values()) {
			if(apiType.type().equalsIgnoreCase(type)) {
				return apiType;
			}
		}
		return null;
	}
	
	public static String buildBillWildCardQuery(String billType, String billWildcard, String sessionYear) {
		return TextFormatter.append(billType,":((",
			billWildcard, "-", sessionYear, 
            " OR [", billWildcard, "A-", sessionYear, 
               " TO ", billWildcard, "Z-", sessionYear, 
            "]) AND ", billWildcard, "*-", sessionYear, ")");
	}
}
