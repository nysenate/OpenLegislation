package gov.nysenate.openleg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public interface OpenLegConstants {

	public static final String SENATE_BILL_CODE = "S";
	
	public static final String ASSEMBLY_BILL_CODE = "A";
	
	public static final String FORMAT_XML = "xml";
	
	public static final String PAGE_IDX = "pageIdx";
	public static final String PAGE_SIZE = "pageSize";

	public final static String TYPE_BILL = "bill";
	public final static String TYPE_BILLS = "bills";
	public final static String TYPE_COSPONSOR = "cosponsor";
	public final static String TYPE_SPONSOR = "sponsor";
	public final static String TYPE_SENATOR = "senator";
	public final static String TYPE_ASSEMBLY = "assembly";
	public final static String TYPE_VOTE = "vote";
	public final static String TYPE_TRANSCRIPT = "transcript";
	public final static String TYPE_ACTIONS = "actions";
	public final static String TYPE_COMMITTEE = "committee";
	public final static String TYPE_COMM = "comm";
	
	public final static String TYPE_SEARCH = "search";
	public final static String TYPE_SEARCH_ADVANCED = "search-advanced";
	
	//new types added January 2010
	public final static String TYPE_AGENDA = "agenda";
	public final static String TYPE_MEETINGS = "meetings";
	public final static String TYPE_MEETING = "meeting";
	public final static String TYPE_CALENDAR = "calendar";
	public final static String TYPE_CALENDARS = "calendars";

	public final static String KEY_TERM = "term";
	public final static String ENCODING = "utf-8";
	
	public final static String DOT_JSP = ".jsp";
	
	public static final String SORTINDEX_ASCENDING = "sortIndex ascending";
	public static final String SORTINDEX_DESCENDING = "sortIndex descending";

	public static final String SENATEBILLNO_DESCENDING = "senateBillNo descending";
	public static final String COMMITTEE_ASCENDING = "currentCommittee ascending";
	
	    //SORT_FULLNAME_ASCENDING
	public static final String SORT_FULLNAME_ASCENDING = "fullname ascending";
	public static final String SORT_EVENTDATE_DESCENDING = "eventDate descending";
	    
	
	public final static String SQL_OR = " || ";
	public final static String SQL_CLOSE_PAREN = ")";
	public final static String SQL_TITLE_MATCHES = "this.title.matches(";
	public final static String SQL_SUMMARY_MATCHES = "this.summary.matches(";
	public final static String SQL_MEMO_MATCHES = "this.memo.matches(";
	public final static String SQL_AND = " && ";
	public final static String SQL_BILL_MATCHES = "this.senateBillNo.matches(";
	public final static String SQL_SPONSOR_MATCHES = "this.sponsor.fullname.matches(";
	public final static String SQL_COMM_MATCHES = "this.currentCommittee.matches(";

	public static final java.text.SimpleDateFormat TRANSCRIPT_DATE_PARSER = new java.text.SimpleDateFormat("MMMM dd, yyyy HH:mm aa");
	
	public final static DateFormat OL_SEARCH_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	public final static DateFormat LRS_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat LRS_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss'Z'");
	//'2009-11-03 T09.30.00Z'
	
	public static int DEFAULT_CACHE_TIME = 3600;
	//public static int DEFAULT_CACHE_TIME_ONE_HOUR = 3600;
	//public static int DEFAULT_CACHE_TIME_ONE_DAY = 3600 * 24;
	
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int DEFAULT_API_PAGE_SIZE = 20;
	public static final int DEFAULT_START_PAGE = 1;
	public static final int MAX_PAGE_SIZE = 1000;
	
	public static int MAX_FETCH_DEPTH = -1;


	public final static String KEY_PATH = "path";
	public final static String KEY_TYPE = "type";
	
	
	public final static String REGEX_API_KEY = ":::::|||||";
}
