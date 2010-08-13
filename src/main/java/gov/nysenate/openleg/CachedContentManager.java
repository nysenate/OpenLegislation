package gov.nysenate.openleg;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Committee;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class CachedContentManager implements OpenLegConstants  {

	//private static GeneralCacheAdministrator cache = null;
	//private static int cacheRefreshPeriod = OpenLegConstants.DEFAULT_CACHE_TIME;
	
	private static Logger logger = Logger.getLogger(CachedContentManager.class);	


	/*
	public static void initCache (InputStream inputStream)
	{
		
		 Properties props = new Properties();
		 try {
			props.load(inputStream);
			cache = new GeneralCacheAdministrator (props);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	
	public static void fillCache (HttpServletRequest request) throws Exception
	{
		String type = (String)request.getAttribute("type");
		String key = (String)request.getAttribute("term");
		int pageIdx = Integer.parseInt((String)request.getAttribute(PAGE_IDX));
		int pageSize = Integer.parseInt((String)request.getAttribute(PAGE_SIZE));

		int start = (pageIdx - 1) * pageSize;
		int end = start + pageSize;
		
		logger.info("attempting to retrieve cached object: " + type + "=" + key + ";" + start + "/" + end);
		
		Object response = null;
		
		if (type.equals(TYPE_BILL))
			response = getBill (key, request);
		
		else if (type.equals(TYPE_TRANSCRIPT))
			response = getTranscript(key, request);
		
		
		else if (type.equals(TYPE_SPONSOR))
		{
			response = getBillsBySponsor (key, start, end, request);
			type = "bills";
			
		}
		else if (type.equals(TYPE_SENATOR))
		{	
			
			response = getBillsBySponsor (key, start, end, request);
			type = "bills";
			
		}
		else if (type.equals(TYPE_COMMITTEE) || type.equals(TYPE_COMM))
		{			
			response = getBillsByCommittee (key, start, end, request);
			type = "bills";
			

			
		}
		else if (type.equals(TYPE_SEARCH) || type.equals("bills"))
		{
			response = getBillsBySearch (key, start, end, request);
			type = "bills";
			
			
		}
		else if (type.equals(TYPE_AGENDA))
		{
			response = getAgenda (key, request);
		}
		else if (type.equals(TYPE_MEETING))
		{
			response = getMeeting (key, request);
		}
		else if (type.equals(TYPE_MEETINGS))
		{
			Committee comm = new Committee();
			comm.setName(key);
		
			response = getMeetings (key, start, end, request);
			
			comm.setMeetings((ArrayList<Meeting>)response);
			
			response = comm;
		}
		else if (type.equals(TYPE_CALENDAR))
		{
			response = getCalendar (key, request);
		}
		else if (type.equals(TYPE_CALENDARS))
		{
			response = getCalendars (key, start, end, request);
		}
		
		request.setAttribute(type, response);
		
	}
	
	public static Bill getBill (String key, HttpServletRequest req)
	{
		Bill bill = null;		
		
		bill = PMF.getDetachedBill(key);
			
		
		return bill;
	}
	
	public static Calendar getCalendar (String key, HttpServletRequest req)
	{

		Calendar calendar = null;
		
		calendar = (Calendar)PMF.getDetachedObject(Calendar.class, "id", key, "no descending");
			
		
		return calendar;
	}
	
	public static Collection<Bill> getBillsBySearch (String key, int start, int end, HttpServletRequest req) throws Exception
	{
		Collection<Bill> bills = null;
		int total = -1;
		
		key = URLDecoder.decode(key,ENCODING).trim();
		req.getSession().setAttribute(KEY_TERM,key.replaceAll("\"", "&quot;"));
		
						
				total = 0;
				QueryResult qr;
				
				key = key.trim();
				
				if (key.length() > 0 && key.toLowerCase().indexOf("and")==-1
						&& key.toLowerCase().indexOf("or")==-1)
				{
					String quoteKey = '"' + key + '"'; //first search for exact match
					
					qr = PMF.getBillByKeywords(quoteKey, start, end);
					
					bills = qr.getResult();
					total = qr.getTotal();
				}
				
				if (total == 0) //if no exact match, then search more fuzzy
				{
					qr = PMF.getBillByKeywords(key, start, end);
				
					bills = qr.getResult();
					total = qr.getTotal();
				}
				
				
			
		req.setAttribute("searchTotal", total+"");
		
		return bills;
	}
	
	public static Collection<Bill> getBillsByCommittee (String key, int start, int end, HttpServletRequest req) throws Exception
	{
		String comm = URLDecoder.decode(key,"utf-8");
		
		Collection<Bill> bills = null;
		
		req.getSession().setAttribute(KEY_TERM,comm);
		
		String queryString = "currentCommittee.matches('(?i)" + comm + "')";
				
		int total = 0;
		
			QueryResult qr = PMF.queryBills(queryString,start,end);
			bills = qr.getResult();
			total = qr.getTotal();
			
			
		
		req.setAttribute("searchTotal", total+"");
		
		return bills;
	}
	
	public static Collection<Bill> getBillsBySponsor (String key, int start, int end, HttpServletRequest req) throws Exception
	{
		String sponsor = URLDecoder.decode(key,ENCODING);
		
		req.getSession().setAttribute(KEY_TERM,sponsor);
		boolean fuzzy = false;
		Collection<Bill> bills = null;
		int total = -1;
	
			
			QueryResult qr = PMF.getBillFromSponsor(sponsor, start, end, fuzzy);
			
			bills = qr.getResult();
			total = qr.getTotal();
			
			if (bills.size() == 0)
			{
				fuzzy = true;
				qr = PMF.getBillFromSponsor(sponsor, start, end, fuzzy);
				bills = qr.getResult();
				total = qr.getTotal();
				
			}
		
		req.setAttribute("searchTotal", total+"");
		
		return bills;
	}
	
	public static Transcript getTranscript (String key, HttpServletRequest req)
	{
		Transcript transcript = null;
			
		System.out.println(key);
		
			transcript = (Transcript)PMF.getTranscript(PMF.getPersistenceManager(),key);
			
		return transcript;
	}
	
	public static ArrayList<Calendar> getCalendars (String key, int start, int end, HttpServletRequest req)
	{
		
		String cacheKey = "comm-calendars-" + key + "-" + start + "-" + end;
		
		ArrayList<Calendar> listCalendar = null;
		
		Collection<Object> calendars = null;

		calendars = (Collection<Object>)PMF.getDetachedObjects(Calendar.class, "type", ".*" + key, "year descending,no descending", start, end);

		if (calendars.size() == 0)
		{
			try
			{
				calendars = (Collection<Object>)PMF.getDetachedObjects(Calendar.class, "no", Integer.parseInt(key), "year descending,no descending", start, end);
			}
			catch (Exception e3)
			{
				//not an int
				logger.warn("error getting calendar",e3);
				
			}
		}
		
		if (calendars.size() == 0)
		{
			try
			{
				java.util.Date matchDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT).parse(key);
				Collection<Object> supps = (Collection<Object>)PMF.getDetachedObjects(Supplemental.class, "calendarDate", matchDate, "year descending,no descending", start, end);
				
				calendars = new ArrayList <Object>();
				
				Iterator it = supps.iterator();
				Supplemental supp = null;
				while (it.hasNext())
				{
					supp = (Supplemental)it.next();
					if (!calendars.contains(supp.getCalendar()))
						calendars.add(supp.getCalendar());
				}
			}
			catch (Exception pe)
			{
				try
				{
					java.util.Date matchDate = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).parse(key);
					Collection<Object> supps = (Collection<Object>)PMF.getDetachedObjects(Supplemental.class, "calendarDate", matchDate, "year descending,no descending", start, end);
				
				
					calendars = new ArrayList <Object>();
					
					Iterator it = supps.iterator();
					Supplemental supp = null;
					while (it.hasNext())
					{
						supp = (Supplemental)it.next();
						if (!calendars.contains(supp.getCalendar()))
							calendars.add(supp.getCalendar());
					}
				}
				catch (Exception pe2)
				{
					//not a date!
					logger.warn("error getting calendar",pe2);
					
				}
			}
		}
		
		listCalendar = new ArrayList<Calendar>();
	

		if (calendars != null)
		{
			Iterator<Object> it = calendars.iterator();
			Calendar calendar = null;
			
			while (it.hasNext())
			{
				calendar =(Calendar)it.next();
				listCalendar.add(calendar);
			}
			

		}
			
		
		return listCalendar;
	}
	
	public static Collection<Meeting> getMeetings (String key, int start, int end, HttpServletRequest req)
	{
		Collection<Meeting> meetings = null;
		
					
		meetings = (Collection<Meeting>)PMF.getDetachedObjects(Meeting.class, "committeeName", ".*" + key + ".*", "meetingDateTime descending", start, end);
		
		if (meetings.size() == 0)
		{
			meetings = (Collection<Meeting>)PMF.getDetachedObjects(Meeting.class, "committeeChair", ".*" + key + ".*", "meetingDateTime descending", start, end);
		}
		
		if (meetings.size() == 0)
		{
			meetings = (Collection<Meeting>)PMF.getDetachedObjects(Meeting.class, "location", ".*" + key + ".*", "meetingDateTime descending", start, end);
		}
		
		if (meetings.size() == 0)
		{
			String dateKey = key.replace('-', '/');
					
			try
			{
				java.util.Date matchDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT).parse(dateKey);
				meetings = (Collection<Meeting>)PMF.getDetachedObjects(Meeting.class, "meetingDateTime", matchDate, "meetingDateTime descending", start, end);
			}
			catch (Exception pe)
			{
				try
				{
					java.util.Date matchDate = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).parse(dateKey);
					meetings = (Collection<Meeting>)PMF.getDetachedObjects(Meeting.class, "meetingDateTime", matchDate, "meetingDateTime descending", start, end);
				}
				catch (Exception pe2)
				{
					//not a date!
					
				}
			}
		}
		
		
		if (meetings.size() == 0)
		{
		//	Query query = pm.newQuery (Magazine.class, "articles.contains (art) " 
			//	    + "&& art.title == 'Fourier Transforms'";
				//query.declareVariables ("Article art");
				
		//	meetings = (Collection<Object>)PMF.getObjects(Meeting.class, "addendums.agenda.number", Integer.parseInt(key), "meetingDateTime descending");
		
			PersistenceManager pm = PMF.getPersistenceManager();
			  
			  Transaction tx = pm.currentTransaction();
			  
			  try
		        {
				  
				  tx.begin();
				  
				  int agendaNum = Integer.parseInt(key);
				  
			        // 4. mark object as persistent
			        //pm.makePersistent(billMap);
			        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
			        pm.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_OPTIMAL);
			        
			        Query query = pm.newQuery(Meeting.class,"addendums.contains(addendum) && addendum.agenda.number==" + agendaNum);
			        query.declareVariables ("Addendum addendum");
			        query.setOrdering("meetingDateTime descending");
			        query.setRange(start, end);
			        
			        // 3. perform query
			        meetings = (Collection<Meeting>)query.execute();
			 
			        meetings = pm.detachCopyAll(meetings);
			        
			        query.closeAll();
			        
			        tx.commit();
			        
		        }
		        catch (Exception e2)
		        {
		        	logger.info("error occured: " + e2);
		        	
		        	if (tx.isActive())
		        		tx.rollback();
		        	
		        	
		        	 try
				        {
						  
						  tx.begin();
						  
						
						  
					        // 4. mark object as persistent
					        //pm.makePersistent(billMap);
					        pm.getFetchPlan().setMaxFetchDepth(MAX_FETCH_DEPTH);
					        
					        Query query = pm.newQuery(Meeting.class,"this.bills.contains(bill) && bill.senateBillNo.matches(key0)");
					        query.declareImports("import gov.nysenate.openleg.model.Bill");
					        
					        query.declareVariables ("Bill bill");
					        
					        query.declareParameters("String key0");
					        query.setOrdering("meetingDateTime descending");
					        query.setRange(start, end);
					        
					        // 3. perform query
					        meetings = (Collection<Meeting>)query.execute(key);
					 
					        meetings = pm.detachCopyAll(meetings);
					        
					        query.closeAll();
					        
					        tx.commit();
					        
				        }
				        catch (Exception e3)
				        {
				        	logger.info("error occured: " + e3);
				        	
				        	if (tx.isActive())
				        		tx.rollback();
				        }
		        }
			
			}
			
		
		return meetings;
	}
	
	public static Meeting getMeeting (String key, HttpServletRequest req)
	{
		Meeting meeting = null;
			
		meeting = (Meeting)PMF.getDetachedObject(Meeting.class, "id", key, "meetingDateTime descending");
		
		return meeting;
	}
	
	public static Agenda getAgenda (String key, HttpServletRequest req)
	{

		
		Agenda agenda = null;
		
		agenda = (Agenda)PMF.getDetachedObject(Agenda.class, "id", key, "number descending");
	
		return agenda;
	}
}
