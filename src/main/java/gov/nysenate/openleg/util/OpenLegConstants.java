package gov.nysenate.openleg.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;


public interface OpenLegConstants {
    public static final String FORMAT_XML = "xml";

    public static final String PAGE_IDX = "pageIdx";
    public static final String PAGE_SIZE = "pageSize";

    public final static String KEY_TERM = "term";
    public final static String ENCODING = "utf-8";

    public final static String DOT_JSP = ".jsp";

    public static final java.text.SimpleDateFormat TRANSCRIPT_DATE_PARSER = new java.text.SimpleDateFormat("MMMM dd, yyyy hh:mm aa");

    public final static DateFormat OL_SEARCH_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public final static DateFormat LRS_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat LRS_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss'Z'");

    public static int DEFAULT_CACHE_TIME = 3600;

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_API_PAGE_SIZE = 20;
    public static final int DEFAULT_START_PAGE = 1;
    public static final int MAX_PAGE_SIZE = 1000;

    public static int MAX_FETCH_DEPTH = -1;


    public final static String KEY_PATH = "path";
    public final static String KEY_TYPE = "type";

    public final static String REGEX_API_KEY = ":::::|||||";

    final static String DEFAULT_SORT_FIELD = "when";
    final static String DEFAULT_SEARCH_FORMAT = "json";
    final static String DEFAULT_SESSION_YEAR = SessionYear.getSessionYear() + "";

    final static String LUCENE_ACTIVE = "true";
    final static String LUCENE_INACTIVE = "false";
}
