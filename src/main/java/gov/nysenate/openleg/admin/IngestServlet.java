package gov.nysenate.openleg.admin;


import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


@SuppressWarnings("all")
public class IngestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2542698022262519069L;
	private static final Logger log = Logger.getLogger(IngestServlet.class.getName());

	/**
	 * Constructor of the object.
	 */
	public IngestServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String filename = request.getParameter("filename");
		String dataurl = request.getParameter("dataurl");
		
		InputStream is = new URL(dataurl).openConnection().getInputStream();
		
		if (dataurl.endsWith("gz"))
		{
			is = new GZIPInputStream(is);
		}
		
		BasicParser bp = new BasicParser();
		//bp.parseFile (new BufferedReader (new InputStreamReader(is)));
		
	    //Collection<Bill> bills = PMF.getBills();
	    
	    out.println("code=0");
	    
		out.flush();
		out.close();
		*/
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String filename;

		try
		{
			// Check that we have a file upload request
			//boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(99999999);
			
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			// Parse the request
			List<?> items = upload.parseRequest(request);
			
			// Process the uploaded items
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();

			    if (item.isFormField()) {
			        
			    	if (item.getFieldName().equals("filename"))
			    	{
			    		filename = item.getString();
			    	}
			    	
			    	
			    } else {
			    	
			    	if (item.getFieldName().equals("data"))
			    	{
			    		/*
			    		if (filename.equals("status"))
						{
							BasicParser.parseStatusData(new BufferedReader(new InputStreamReader(item.getInputStream())));
						}
			    		else if (filename.equals("memo"))
						{
							BasicParser.parseMemoData(new BufferedReader(new InputStreamReader(item.getInputStream())));
						}
			    		else if (filename.equals("sameas"))
						{
							BasicParser.parseSameAsData(new BufferedReader(new InputStreamReader(item.getInputStream())));
						}
			    		else if (filename.equals("sponsor"))
						{
							BasicParser.parseSponsorData(new BufferedReader(new InputStreamReader(item.getInputStream())));
						}
			    		else if (filename.equals("text"))
						{
							BasicParser.parseTextData(new BufferedReader(new InputStreamReader(item.getInputStream())));
						}*/
			    	}
			    	
			    }
			}
			
			
			response.sendRedirect("/ingest.jsp");
		}
		catch (Exception e)
		{
			log.warning(e.toString());
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
