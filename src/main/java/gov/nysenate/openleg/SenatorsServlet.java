package gov.nysenate.openleg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * Servlet implementation class SenatorsServlet
 */
public class SenatorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final String DISTRICT_JSON_FOLDER_PATH = "WEB-INF/classes/data/districts/";
   
	private static final String VIEW_PATH = "/senators/index.jsp";
	
	private static Logger logger = Logger.getLogger(SenatorsServlet.class);	

	private static ArrayList<JSONObject> districts = null;
	/**
     * 
     * @see HttpServlet#HttpServlet()
     */
    public SenatorsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setAttribute("districts", districts);
		getServletContext().getRequestDispatcher(VIEW_PATH).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		
			
		try
		{
		
			districts = new ArrayList<JSONObject>();
			
			for (int i = 1; i <= 62; i++)
			{
				String jsonPath = DISTRICT_JSON_FOLDER_PATH + "sd" + i + ".json";
				logger.info("loading: " + jsonPath);
				
				InputStream is = getServletContext().getResourceAsStream(jsonPath);
			//	InputStream is = ClassLoader  
				//.getSystemResourceAsStream(DISTRICT_JSON_FOLDER_PATH + "sd" + i + ".json");  
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				
				String line = null;
				StringBuffer jsonb = new StringBuffer();
				while ((line = reader.readLine())!=null)
				{	
					jsonb.append(line);
					
				}
				
				
				JSONObject jsono = new JSONObject(jsonb.toString());

				JSONObject jSenator = jsono.getJSONObject("senator");
				
				String senatorName = jSenator.getString("name");
				String senatorKey = jSenator.getString("url");
				senatorKey = senatorKey.substring(senatorKey.lastIndexOf('/')+1);
				
				senatorKey = senatorKey.replace("-jr", ""); 
				senatorKey = senatorKey.replace("-sr", ""); 
				String[] senatorKeyParts =senatorKey.split("-");

				if (senatorName.contains("-"))
				{
					
					senatorKey = senatorKeyParts[senatorKeyParts.length-2] + '-' + senatorKeyParts[senatorKeyParts.length-1];
				}
				else
				{
					senatorKey = senatorKeyParts[senatorKeyParts.length-1];
				}
				
				jSenator.put("key", senatorKey);
				
				districts.add(jsono);
				
				
				logger.info(jsono.get("district"));
				logger.info(jsono.getJSONObject("senator").get("name"));
				
			}
			
			 Collections.sort(districts, new byLastName());
		}
		catch (Exception e)
		{
			logger.error("error loading json district files",e);
		}
	}
	
	
	 class byLastName implements java.util.Comparator {
		 public int compare(Object districtA, Object districtB) {
			 int sdif = 0;
			 
			 try
			 {
				 JSONObject senatorA = ((JSONObject)districtA).getJSONObject("senator");
				 JSONObject senatorB = ((JSONObject)districtB).getJSONObject("senator");
				 
				 sdif = senatorA.getString("key").compareTo(senatorB.getString("key"));
				 
			//	 logger.info("sort value: " + senatorA.getString("key") + " vs " +  senatorB.getString("key") + sdif);
			 }
			catch (Exception e)
			{
				logger.error("error sorting districts",e);
			}
			
		  
		  return sdif;
		 }
		} 
}
