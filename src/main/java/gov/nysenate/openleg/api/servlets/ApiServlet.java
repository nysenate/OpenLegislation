package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.api.AbstractApiRequest;
import gov.nysenate.openleg.api.KeyValueViewRequest;
import gov.nysenate.openleg.api.KeyValueViewRequest.KeyValueView;
import gov.nysenate.openleg.api.MultiViewRequest;
import gov.nysenate.openleg.api.MultiViewRequest.MultiView;
import gov.nysenate.openleg.api.SingleViewRequest;
import gov.nysenate.openleg.api.SingleViewRequest.SingleView;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.OpenLegConstants;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * a cleaner and [hopefully] more intelligent catch all for generated views
 */
@SuppressWarnings("serial")
public class ApiServlet extends HttpServlet implements OpenLegConstants {
	
	public static final int SINGLE_FORMAT = 1;
	public static final int SINGLE_TYPE = 2;
	public static final int SINGLE_ID = 3;
	
	public static final int MULTI_FORMAT = 1;
	public static final int MULTI_TYPE = 2;
	public static final int MULTI_PAGE_NUMBER = 3;
	public static final int MULTI_PAGE_SIZE = 4;
	
	public static final int KEY_VALUE_FORMAT = 1;
	public static final int KEY_VALUE_KEY = 2;
	public static final int KEY_VALUE_VALUE = 3;
	public static final int KEY_VALUE_PAGE_NUMBER = 4;
	public static final int KEY_VALUE_PAGE_SIZE = 5;
	
	/*
	 * Used to match the start of a single, multi or key value view..
	 * 		/legislation/[view type]
	 * 		/legislation/api/[view type]
	 * 		/legislation/api/1.0/[view type]
	 */
	public static final String BASE_START = "^(?i)/legislation/(?:(?:api/)(?:(?<=api/)1\\.0/)?(?:(";
	
	/*
	 * Ends base start, surrounds possible formats associated with a view
	 */
	public static final String BASE_MIDDLE = ")/))?(";
	
	public static final String BASE_END = "$";
	
	/*
	 * multi views and key value views have an optional
	 * paging mechanism.. can end with
	 * 		../[page]
	 * 		../[page]/[page size]
	 */
	public static final String PAGING = "(?:(\\d+)/?+)?(?:(\\d+)/?)?";
	
	/*
	 * Captures ID from single view
	 */
	public static final String SINGLE_END = ")/(.+)";
	
	public static final String MULTI_END = ")/?";
	
	/*
	 * Captures value for Key Value view
	 */
	public static final String KEY_VALUE_END = ")/(.*?)/?";
	
	public final Pattern SINGLE_PATTERN;
	public final Pattern MULTI_PATTERN;
	public final Pattern KEY_VALUE_PATTERN;
	
	public ApiServlet() throws ServletException {
		super();
		
		String singleViews = new Join<SingleView>() {
			public String value(SingleView t) {
				return t.view;
			}
		}.join(SingleView.values(), "|");
		
		String singleFormats = new Join<String>() {
			public String value(String t) {
				return t;
			}
		}.join(getUniqueFormats(SingleView.values()), "|");
				
		String multiViews = new Join<MultiView>() {
			public String value(MultiView t) {
				return t.view;
			}
		}.join(MultiView.values(), "|");
		
		String multiFormats = new Join<String>() {
			public String value(String t) {
				return t;
			}
		}.join(getUniqueFormats(MultiView.values()), "|");
		
		String keyValueViews = new Join<KeyValueView>() {
			public String value(KeyValueView t) {
				return t.view;
			}
		}.join(KeyValueView.values(), "|");
		
		String keyValueFormats = new Join<String>() {
			public String value(String t) {
				return t;
			}
		}.join(getUniqueFormats(KeyValueView.values()), "|");
		
		SINGLE_PATTERN = Pattern.compile(
				BASE_START + singleFormats + BASE_MIDDLE + singleViews + SINGLE_END + BASE_END 
			);
		
		MULTI_PATTERN = Pattern.compile(
				BASE_START + multiFormats + BASE_MIDDLE + multiViews + MULTI_END + PAGING + BASE_END 
			);
				
		KEY_VALUE_PATTERN = Pattern.compile(
				BASE_START + keyValueFormats + BASE_MIDDLE + keyValueViews + KEY_VALUE_END + PAGING + BASE_END 
			);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		Matcher m = null;
		
		String uri = URLDecoder.decode(request.getRequestURI(), ENCODING);
		
		AbstractApiRequest apiRequest = null;
		
		if(apiRequest == null && (m = SINGLE_PATTERN.matcher(uri)) != null && m.find()) {
			apiRequest = new SingleViewRequest(	request, 
												response, 
												m.group(SINGLE_FORMAT),
												m.group(SINGLE_TYPE),
												m.group(SINGLE_ID));
		}
		
		if(apiRequest == null && (m = MULTI_PATTERN.matcher(uri)) != null && m.find()) {
			apiRequest = new MultiViewRequest(	request,
												response,
												m.group(MULTI_FORMAT),
												m.group(MULTI_TYPE),
												m.group(MULTI_PAGE_NUMBER),
												m.group(MULTI_PAGE_SIZE));
		}
		
		if(apiRequest == null && (m = KEY_VALUE_PATTERN.matcher(uri)) != null && m.find()) {
			apiRequest = new KeyValueViewRequest(	request,
													response,
													m.group(KEY_VALUE_FORMAT),
													m.group(KEY_VALUE_KEY),
													m.group(KEY_VALUE_VALUE),
													m.group(KEY_VALUE_PAGE_NUMBER),
													m.group(KEY_VALUE_PAGE_SIZE));
		}
		
		if(apiRequest == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		else {
			doReq(apiRequest, request, response);
		}
    }
	
	public void doReq(AbstractApiRequest apiRequest, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		
		apiRequest.fillRequest();
		getServletContext().getRequestDispatcher(apiRequest.getView())
				.forward(request, response);
	}
	
	public interface ApiEnum {
		public String view();
		public String[] formats();
		public Class<? extends SenateObject> clazz();
	}
	
	public static <T extends ApiEnum> HashSet<String> getUniqueFormats(T[] array) {
		HashSet<String> set = new HashSet<String>();
		for(T t:array) {
			set.addAll(Arrays.asList(t.formats()));
		}
		return set;
	}
	
	static abstract class Join<T> {
		public abstract String value(T t);
		
		public String join(Iterable<T> iterable, String on) {
			StringBuffer buf = new StringBuffer();
			
			Iterator<T> iter = iterable.iterator();
			
			if(iter.hasNext())
				buf.append(value(iter.next()));
			
			while(iter.hasNext()) {
				buf.append(on);
				buf.append(value(iter.next()));
			}
			
			return buf.toString();
		}
		
		public String join(T[] array, String on) {
			StringBuffer buf = new StringBuffer();
			int length = array.length;
			
			if(length == 0) return buf.toString();
			
			buf.append(value(array[0]));
			
			for(int i = 1; i < array.length; i++) {
				buf.append(on);
				buf.append(value(array[i]));
			}
			return buf.toString();
		}
	}
}
