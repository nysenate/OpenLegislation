package gov.nysenate.openleg;

import gov.nysenate.openleg.util.BillCleaner;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class APIServlet extends HttpServlet implements OpenLegConstants {

	private static Logger logger = Logger.getLogger(APIServlet.class);	

	private static final String SRV_DELIM = "/";
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
	//	CachedContentManager.initCache(getServletContext().getResourceAsStream("/WEB-INF/classes/oscache.properties"));
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		
	//	CachedContentManager.initCache(getServletContext().getResourceAsStream("/WEB-INF/classes/oscache.properties"));
		
		
	}
	

	
	
	
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException
	{
		
		String encodedUri = req.getRequestURI();
				
		String uri = java.net.URLDecoder.decode(encodedUri,OpenLegConstants.ENCODING);
		
		
		if(uri.contains("/bill/")) {
			
			String nuri = BillCleaner.validBill(uri);	
			uri = nuri;
				
		}
			
		logger.info("request: " + uri + " (" + encodedUri + ")");
		
		req.setAttribute(KEY_PATH,uri);
	
		StringTokenizer st = new StringTokenizer (uri,SRV_DELIM);
		
		String app = st.nextToken(); //legislation
		String service = st.nextToken(); //api
		String version = "";
		
		if (st.hasMoreTokens())
			version = st.nextToken(); //1.0

		int pageIdx = DEFAULT_START_PAGE;
		int pageSize = DEFAULT_PAGE_SIZE;
		
		try
		{
			
		
			if (version.equals("1.0"))
			{
				String format = st.nextToken().toLowerCase();	
				String type = st.nextToken().toLowerCase();
				
				req.setAttribute(KEY_TYPE,type);
				
				String key = "";
				
				if (st.hasMoreTokens())
					key = URLDecoder.decode(st.nextToken(),OpenLegConstants.ENCODING);
			
				if (st.hasMoreTokens())
				{
					pageIdx = Integer.parseInt(st.nextToken());
						
						if (st.hasMoreTokens())
						{
							pageSize = Integer.parseInt(st.nextToken());
							
						}
						else
						{
							pageSize = pageIdx;
							pageIdx = Integer.parseInt(key);
							key = "";
						}
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
				
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
			else if (version.equals("2.0"))
			{
				String format = st.nextToken().toLowerCase();	
				String type = st.nextToken().toLowerCase();
				
				req.setAttribute(KEY_TYPE,type);
				
				String key = "";
				
				if (st.hasMoreTokens())
					key = URLDecoder.decode(st.nextToken(),OpenLegConstants.ENCODING);
			
				if (st.hasMoreTokens())
				{
					pageIdx = Integer.parseInt(st.nextToken());
						
						if (st.hasMoreTokens())
						{
							pageSize = Integer.parseInt(st.nextToken());
							
						}
						else
						{
							pageSize = pageIdx;
							pageIdx = Integer.parseInt(key);
							key = "";
						}
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
				
				handleAPIv2(format, type, key, pageIdx, pageSize, req, resp);
			}
			else if (version.equals("html"))
			{
	
				String format = version;
				String type = st.nextToken().toLowerCase();
				String key = "%20";
				
				if (st.hasMoreTokens())
					key = URLDecoder.decode(st.nextToken(),OpenLegConstants.ENCODING);
				
				req.setAttribute(KEY_TYPE,type);
	
				
				if (st.hasMoreTokens())
				{
					pageIdx = Integer.parseInt(st.nextToken());
						
						if (st.hasMoreTokens())
						{
							pageSize = Integer.parseInt(st.nextToken());
							
						}
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
				
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
			else if (service.equals("api")) // /legislation/api/csv/bill/S1399
			{
				
				String format = version;
				String type = URLDecoder.decode(st.nextToken(),OpenLegConstants.ENCODING);
				String key = URLDecoder.decode(st.nextToken(),OpenLegConstants.ENCODING);
				
				req.setAttribute(KEY_TYPE,type);
	
				
				if (st.hasMoreTokens())
				{
					pageIdx = Integer.parseInt(st.nextToken());
						
						if (st.hasMoreTokens())
						{
							pageSize = Integer.parseInt(st.nextToken());
							
						}
				}
				else if (format.equals(FORMAT_XML)) //for now with XML
					pageSize = DEFAULT_API_PAGE_SIZE;
					
				handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
			}
			else 
			{
			
				String format = "html";
				
				if (req.getSession().getAttribute("mobile")!=null)
					format = "mobile";
				
				String type = service;
				String key = URLDecoder.decode(version,OpenLegConstants.ENCODING);
				
				if (key.length()==0 && req.getParameterNames().hasMoreElements())
				{
					key = req.getParameterNames().nextElement().toString();
				}
				
				req.setAttribute(KEY_TYPE,type);
	
				
					if (st.hasMoreTokens())
					{
						pageIdx = Integer.parseInt(st.nextToken());
							
							if (st.hasMoreTokens())
							{
								pageSize = Integer.parseInt(st.nextToken());
								
							}
					}
					else if (format.equals(FORMAT_XML)) //for now with XML
						pageSize = DEFAULT_API_PAGE_SIZE;
					
					handleAPIv1(format, type, key, pageIdx, pageSize, req, resp);
					
				
			}
		
		}
		catch (NumberFormatException nfe)
		{
			logger.warn ("Invalid API call", nfe);
			
			
		}
		catch (NoSuchElementException nse)
		{
			logger.warn ("Invalid API call", nse);
			
			
		}
		catch (NullPointerException npe)
		{
			logger.warn ("Invalid API call", npe);
			
			
		}
		catch (Exception e)
		{
			logger.warn ("Invalid API call", e);
			
			
		}
		
	}
	
	
	

	// /openleg/api/1.0/html/bill/A9067/1/5
	public void handleAPIv1 (String format, String type, String key, int pageIdx, int pageSize, HttpServletRequest req, HttpServletResponse resp) 
		throws IOException, ServletException 
	{
			
		key = key.trim();
		
		if (pageSize > MAX_PAGE_SIZE)
			throw new ServletException ("The maximum page size is " + MAX_PAGE_SIZE);
		
		req.setAttribute(PAGE_IDX,pageIdx+"");
		req.setAttribute(PAGE_SIZE,pageSize+"");
		req.setAttribute("type",type);
		req.setAttribute("term", key);
		
		//now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;
		
		logger.info("request: key=" + key + ";type=" + type + ";format=" + format + ";paging=" + start + "/" + end);
		
		String viewPath = "";
		
		
		if (!format.equals("html") && !format.equals("mobile"))
		{
			String viewType = type;
			
			if (type.equals(TYPE_SENATOR) || type.equals(TYPE_SPONSOR) || type.equals(TYPE_COMMITTEE) || type.equals(TYPE_COMM) || type.equals(TYPE_SEARCH))
				viewType = "bills";
			
			viewPath = "/views/" + viewType + "-" + format + ".jsp";
		}
		else
		{
			if (type.equals(TYPE_SEARCH))
			{	
				
				viewPath = "/search/?term=" + key + "&type=bill&pageIdx=" + pageIdx + "&pageSize=" + pageSize + "&format=" + format;
	
			}
			else if (type.equals(TYPE_SENATOR) || type.equals(TYPE_SPONSOR))
			{	
				key = java.net.URLEncoder.encode(key,ENCODING);

				viewPath = "/search/?term=sponsor:" + key + "&type=bill&pageIdx=" + pageIdx + "&pageSize=" + pageSize + "&format=" + format;
	
			}
			else if (type.equals(TYPE_COMMITTEE) || type.equals(TYPE_COMM))
			{	
				
				key = java.net.URLEncoder.encode("\"" + key + "\"",ENCODING);
				
				viewPath = "/search/?term=committee:" + key + "&type=bill&pageIdx=" + pageIdx + "&pageSize=" + pageSize + "&format=" + format;
	
			}
			else
			{
	
				if (type.endsWith("s") && format.equals("html"))
				{
					type = type.substring(0,type.length()-1);
				
					viewPath = "/search/?sort=when&sortOrder=true&term=" + key + "&type=" + type + "&format=" + format + "&pageIdx=" + pageIdx + "&pageSize=" + pageSize;
				}
				else if (type.endsWith("s") && format.equals("mobile"))
				{
					type = type.substring(0,type.length()-1);
				
					viewPath = "/search/?sort=when&sortOrder=true&term=" + key + "&type=" + type + "&format=" + format + "&pageIdx=" + pageIdx + "&pageSize=" + pageSize;
				}
				else
				{
					viewPath = "/views/" + type + "-" + format + ".jsp";
				}
			}
		}
		
		try
		{
			logger.info("routing to search controller:" + viewPath);
			
			getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
		}
		catch (Exception e)
		{
			logger.warn("search controller didn't work for: " + req.getRequestURI(),e);
		}
	}
	
	
	
	public void handleAPIv2 (String format, String type, String key, int pageIdx, int pageSize, HttpServletRequest req, HttpServletResponse resp) 
		throws IOException, ServletException 
	{
		
		key = key.trim();
		
		if (pageSize > MAX_PAGE_SIZE)
			throw new ServletException ("The maximum page size is " + MAX_PAGE_SIZE);
		
		req.setAttribute(PAGE_IDX,pageIdx+"");
		req.setAttribute(PAGE_SIZE,pageSize+"");
		req.setAttribute("type",type);
		req.setAttribute("term", key);
		
		//now calculate start, end idx based on pageIdx and pageSize
		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;
		
		logger.info("request: key=" + key + ";type=" + type + ";format=" + format + ";paging=" + start + "/" + end);
		
		String viewPath = "";
		
		viewPath = "/views/2p0" + type + "-" + format + ".jsp";
		
		try
		{
			logger.info("routing to search controller:" + viewPath);
			
			getServletContext().getRequestDispatcher(viewPath).forward(req, resp);
		}
		catch (Exception e)
		{
			logger.warn("search controller didn't work for: " + req.getRequestURI(),e);
		}
	}
	
	
	
	/*
	public static boolean routeSearchRequest (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{

		String uri = request.getRequestURI();
		
		logger.info("request: " + uri);
		
		String type = request.getParameter("type");

		String appPath = request.getContextPath();
		String term = request.getParameter("term");
		
		request.setAttribute("term", term);
		request.setAttribute("type",type);
		
		if (type != null)
		{
			if (type.equals("transcripts"))
			{
				response.sendRedirect(appPath + "/transcripts/index.jsp?type=transcripts&term=" + java.net.URLEncoder.encode(term,"UTF-8"));
				return true;
			}
			else if (type.equals("meetings"))
			{
				response.sendRedirect(appPath + "/meetings/?" + java.net.URLEncoder.encode(term,"UTF-8"));
				return true;
			}
			else if (type.equals("calendars"))
			{
				response.sendRedirect(appPath + "/calendars/?" + java.net.URLEncoder.encode(term,"UTF-8"));
				return true;
			}
			else if (type.equals("actions"))
			{
				response.sendRedirect(appPath + "/actions/?action=" + java.net.URLEncoder.encode(term,"UTF-8"));
				return true;
			}
		}
		
		if (term != null && term.toLowerCase().indexOf("transcript")!=-1)
		{
			term = term.replace("transcript", "").trim();
			request.getSession().setAttribute("type","transcript");

			response.sendRedirect(appPath + "/transcripts/index.jsp?type=transcripts&term=" + java.net.URLEncoder.encode(term,"UTF-8"));
			return true;
		}
			
		String format = "html";

		if (request.getParameter("format")!=null)
			format = request.getParameter("format");
			

		long start = 0;
		long end = 1;

		String redirUrl = null;
		
		if (request.getParameter("start")!=null)
		{
			start = Long.parseLong(request.getParameter("start"));
		}

		if (request.getParameter("end")!=null)
		{
			end = Long.parseLong(request.getParameter("end"));
		}

		if (term == null)
		{
			term = request.getRequestURI();
			term = term.substring(term.lastIndexOf('/')+1);
			
			term =URLDecoder.decode(term,ENCODING).trim();
		}

		
		 String billTerm = term;
		   billTerm = billTerm.replace("-","");
		   billTerm = billTerm.replace(".","");
		   billTerm = billTerm.replace("'"," ");

		Bill bill = PMF.getDetachedBill(billTerm);

		if (bill != null)
		{
		   
			Bill latestBill = bill;
			
			if (bill.getAmendments() != null && bill.getAmendments().size()>0)
			{
				latestBill = bill.getAmendments().get(0);
			}
			
			response.sendRedirect(appPath + "/api/1.0/" + format + "/bill/"+latestBill.getSenateBillNo());
			return true;
			
		}
		
		redirUrl = appPath + "/api/1.0/" + format + "/search/"+java.net.URLEncoder.encode(term,"utf-8");
		response.sendRedirect(redirUrl);
		
		return true;
		
	}
	*/
	
	
	
	
}
