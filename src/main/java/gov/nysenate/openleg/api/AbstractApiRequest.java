package gov.nysenate.openleg.api;

import gov.nysenate.openleg.api.servlets.ApiServlet.ApiEnum;
import gov.nysenate.openleg.util.OpenLegConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractApiRequest implements OpenLegConstants {
	public static final String DEFAULT_FORMAT = "html";
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	protected HttpServletRequest request;
	HttpServletResponse response;
	protected int pageNumber;
	protected int pageSize;
	protected ApiEnum apiEnum;
	
	String view;
	
	public AbstractApiRequest(HttpServletRequest request, HttpServletResponse response, 
			String pageNumber, String pageSize, ApiEnum apiEnum) {
		
		this(request, 
				response, 
				getNumber(pageNumber, DEFAULT_PAGE_NUMBER), 
				getNumber(pageSize, DEFAULT_PAGE_SIZE),
				apiEnum);
	}
	
	public AbstractApiRequest(HttpServletRequest request, HttpServletResponse response, 
			int pageNumber, int pageSize, ApiEnum apiEnum) {
		this.request = request;
		this.response = response;
		
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		
		this.apiEnum = apiEnum;
	}
	
	public abstract void fillRequest();
	
	public abstract String getView();
	
	public abstract boolean isValid();
	
	public String thisOrThat(String str1, String str2) {
		if(str1 == null || str1.matches("\\s*"))
			return str2;
		return str1;
	}
	
	public static int getNumber(String raw, final int def) {
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
}