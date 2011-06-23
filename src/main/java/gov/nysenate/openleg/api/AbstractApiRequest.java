package gov.nysenate.openleg.api;

import java.util.Arrays;
import java.util.HashSet;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.OpenLegConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * abstract wrapper for api request objects
 */
public abstract class AbstractApiRequest implements OpenLegConstants {
	public static final String DEFAULT_FORMAT = "html";
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected int pageNumber;
	protected int pageSize;
	protected String format;
	protected ApiEnum apiEnum;
	
	public AbstractApiRequest(HttpServletRequest request, HttpServletResponse response, 
			String pageNumber, String pageSize, String format, ApiEnum apiEnum) {
		
		this(request, 
				response, 
				getNumber(pageNumber, DEFAULT_PAGE_NUMBER), 
				getNumber(pageSize, DEFAULT_PAGE_SIZE),
				format,
				apiEnum);
	}
	
	public AbstractApiRequest(HttpServletRequest request, HttpServletResponse response, 
			int pageNumber, int pageSize, String format, ApiEnum apiEnum) {
		this.request = request;
		this.response = response;
		
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		
		this.format = thisOrThat(format, DEFAULT_FORMAT);
		
		this.apiEnum = apiEnum;
	}
	
	public boolean isValidFormat() {
		for(String validFormat:apiEnum.formats()) {
			if(format.equalsIgnoreCase(validFormat)) {
				return true;
			}
		}
		return false;
	}
	
	private String thisOrThat(String str1, String str2) {
		if(str1 == null || str1.matches("\\s*"))
			return str2;
		return str1;
	}
	
	public abstract void fillRequest() throws ApiRequestException;
	
	public abstract String getView();
	
	public abstract boolean hasParameters();
	
	/*
	 * helper function used on class instantiation
	 * to convert string numbers to ints, or if that
	 * fails to a default value
	 */
	private static int getNumber(String raw, final int def) {
		int ret;
		try {
			ret = new Integer(raw);
			return ret;
		}
		catch (Exception e) {
			return def;
		}
	}
	
	public static <T extends ApiEnum> T getApiEnum(T[] array, String view) {
		for(T t:array) {
			if(t.view().equals(view)) {
				return t;
			}
		}
		return null;
	}
	
	/*
	 * arr[x, y, z, z] -> set[x, y, z]
	 */
	public static <T extends ApiEnum> HashSet<String> getUniqueFormats(T[] array) {
		HashSet<String> set = new HashSet<String>();
		for(T t:array) {
			set.addAll(Arrays.asList(t.formats()));
		}
		return set;
	}
	
	public interface ApiEnum {
		public String view();
		public String[] formats();
		public Class<? extends SenateObject> clazz();
	}
	
	@SuppressWarnings("serial")
	public static class ApiRequestException extends Exception {
		public ApiRequestException() {
			super();
		}
		public ApiRequestException(String message) {
			super(message);
		}
	}
}