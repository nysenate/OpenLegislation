package gov.nysenate.openleg.api.servlets;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.api.ApiType;
import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * Can be used to easily create servlets for objects implementing ISenateObject
 *
 * @param <T extends ISenateObject>
 */
public abstract class AbstractSenateObjectServlet<T extends ISenateObject>
		extends HttpServlet implements OpenLegConstants {
	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_FORMAT = "html";
	
	// matches /legislation/<type>/<oid>
	private static final Pattern SHORT_PATTERN = Pattern
			.compile("^/legislation/(.*)/((?!/).*)$");
	// matches /legislation/api/(1.0/)?<type>/<format>/<oid>
	private static final Pattern LONG_PATTERN = Pattern
			.compile("^/legislation/api/(?:1\\.0/)?(.*)/(.*)/(.*)$");
	private Matcher matcher;

	private static Logger logger = Logger
			.getLogger(AbstractSenateObjectServlet.class);
	
	public AbstractSenateObjectServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		SenateObjectRequest sor = getSenateObjectRequest(request);

		try {
			//if SenateObjectRequest isn't available we can't complete request
			if(sor == null || !sor.isValid())
				throw new InvalidRequestException();
			
			//check if format for type is valid
			if(!sor.type.isValidFormat(sor.format))
				throw new InvalidRequestException();
			
			//if request was made with long uri but short is abailable redirect
			if(sor.redirectToSimple) {
				response.sendRedirect(TextFormatter.append("/legislation/",sor.type.type(),"/",sor.oid));
				return;
			}

			T senateObject = getSenateObject(sor);
			
			if(senateObject == null) throw new InvalidRequestException();
			
			doRelated(sor, request);
			doView(senateObject, sor, request, response);
		} catch (InvalidRequestException e) {
			sendError(response, HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	private T getSenateObject(SenateObjectRequest sor) {
		return getSenateObject(sor.oid, sor.type);
	}
	
	@SuppressWarnings("unchecked")
	protected T getSenateObject(String oid, ApiType apiType) {
		return (T) SearchEngine.getInstance().getSenateObject(apiType.clazz(),
				apiType.type(), oid);
	}
	
	protected abstract void doRelated(String oid, HttpServletRequest request);
	
	private void doRelated(SenateObjectRequest sor, HttpServletRequest request) {
		doRelated(sor.oid, request);
	}

	private void doView(T senateObject, SenateObjectRequest sor,
			HttpServletRequest request, HttpServletResponse response) {
		doView(senateObject, sor.format, sor.type.type(), request, response);
	}

	/*
	 * load JSP based on format and type
	 */
	protected void doView(T senateObject, String format, String type,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setAttribute(type, senateObject);
			getServletContext().getRequestDispatcher(
					TextFormatter.append("/views/", type, "-", format, ".jsp"))
					.forward(request, response);
			return;
		} catch (ServletException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		sendError(response, HttpServletResponse.SC_NOT_FOUND);
	}

	public SenateObjectRequest getSenateObjectRequest(HttpServletRequest request) {
		return getSenateObjectRequest(request.getRequestURI());
	}

	/*
	 * Determine if the uri is a long or short path, create SenateObjectRequest
	 * object after parsing out type, oid and format
	 */
	public SenateObjectRequest getSenateObjectRequest(String uri) {
		try {
			uri = URLDecoder.decode(uri, OpenLegConstants.ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		
		SenateObjectRequest sor = new SenateObjectRequest();
		sor.format = DEFAULT_FORMAT;

		matcher = LONG_PATTERN.matcher(uri);
		if (matcher.find()) {
			sor.format = matcher.group(1);
			sor.type = ApiHelper.getApiType(matcher.group(2));
			sor.oid = matcher.group(3);
			
			if(sor.format.equals(DEFAULT_FORMAT))
				sor.redirectToSimple = true;
			
		} else {
			matcher = SHORT_PATTERN.matcher(uri);
			if (matcher.find()) {
				sor.type = ApiHelper.getApiType(matcher.group(1));
				sor.oid = matcher.group(2);
			}
		}

		return sor;
	}

	protected void sendError(HttpServletResponse response, int errNo) {
		try {
			response.sendError(errNo);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	@SuppressWarnings("serial")
	static class InvalidRequestException extends Exception {
		public InvalidRequestException() {
			super();
		}
	}

	private static class SenateObjectRequest {
		public String oid;
		public ApiType type;
		public String format;
		public boolean redirectToSimple;

		public boolean isValid() {
			return oid != null && type != null && format != null;
		}
	}
}