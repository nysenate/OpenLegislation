package gov.nysenate.openleg.client;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenLegClient implements APIClientConstants
{

	public static void main (String[] args)
	{
	
		OpenLegClient olc = new OpenLegClient();
		
		Bill bill = olc.getBill("S1234");
		
		System.out.println("Got bill: " + bill.getSenateBillNo() + ": " + bill.getSummary());
		
		System.out.println("***** get bill by keyword ******");
		ArrayList<Bill> result = olc.getBillsByKeyword("tobacco", 1, 10);
		
		Iterator<Bill> it = result.iterator();
		
		while (it.hasNext())
		{
			bill = it.next();
			System.out.println("Got bill: " + bill.getSenateBillNo() + ": " + bill.getSummary());
		}
		
		System.out.println("***** get bill by sponsor ******");
		result = olc.getBillsBySponsor("krueger", 1, 10);
		
		it = result.iterator();
		
		while (it.hasNext())
		{
			bill = it.next();
			System.out.println("Got bill: " + bill.getSenateBillNo() + ": " + bill.getSummary());
		}
		
		System.out.println("***** get bill by committee ******");
		result = olc.getBillsByCommittee("rules", 1, 10);
		
		it = result.iterator();
		
		while (it.hasNext())
		{
			bill = it.next();
			System.out.println("Got bill: " + bill.getSenateBillNo() + ": " + bill.getSummary());
		}
	}
	
	public Bill getBill (String billId)
	{
		
		try
		{
			
			String url = buildAPIUri (API_VERSION, API_FORMAT_JSON, API_ACTION_BILL, billId, -1, -1);
			
			String result = callAPI (url);
			
			return jsonToBill ((JSONObject)(new JSONArray(result).get(0)));
			
		}
		catch (Exception ioe)
		{

			return null;
		}
	}
	
	public ArrayList<Bill> getBillsByKeyword (String keyword, int start, int end)
	{
		try
		{
		
			String url = buildAPIUri (API_VERSION, API_FORMAT_JSON, API_ACTION_SEARCH, keyword, start, end);

			String result = callAPI (url.toString());
			
			return jsonToBills (result);
			
		}
		catch (IOException ioe)
		{

			return null;
		}
	}
	
	public ArrayList<Bill> getBillsBySponsor(String name, int start, int end)
	{
		
		try
		{
			String url = buildAPIUri (API_VERSION, API_FORMAT_JSON, API_ACTION_SPONSOR, name, start, end);

			String result = callAPI (url);
			
			return jsonToBills (result);
			
		}
		catch (IOException ioe)
		{

			return null;
		}
	}
	
	public ArrayList<Bill> getBillsByCommittee(String comm, int start, int end)
	{
		try
		{
			String url = buildAPIUri (API_VERSION, API_FORMAT_JSON, API_ACTION_COMM, comm, start, end);

			String result = callAPI (url.toString());
			
			return jsonToBills (result);
			
		}
		catch (IOException ioe)
		{

			return null;
		}
	}
	
	public ArrayList<Bill> doBillMultiSearch(String sponsor, String committee, String keyword, int start, int end)
	{
		
		try
		{
			String url = null;//buildAPIUri (API_VERSION, API_FORMAT_JSON, API_ACTION_SPONSOR, sponsor, start, end);

			String result = callAPI (url);
			
			return jsonToBills ((result));
			
		}
		catch (IOException ioe)
		{

			return null;
		}
		
	}
	
	public static String buildAPIUri (String version, String format, String action, String argument, int start, int end) throws IOException
	{
		StringBuffer url = new StringBuffer();
		url.append(API_BASE);
		url.append('/');
		
		url.append(version);
		url.append('/');
		
		url.append(format);
		url.append('/');
		
		url.append(action);
		url.append('/');
		
		try {
			url.append(URLEncoder.encode(argument,TEXT_ENCODING));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (start != -1)
		{
			url.append('/');
			url.append(start);
			url.append('/');
		
			if (end != -1)
				url.append(end);
		}
		
		return url.toString();
	}
	
	private String callAPI (String url) throws IOException
	{
		URLConnection uConn = new URL(url).openConnection();
		InputStream iStream = uConn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
		
		StringBuffer out = new StringBuffer();
		String line = null;
		
		while ((line = br.readLine())!=null)
		{
			out.append(line);
		}
		
		return out.toString();
	}
	
	private ArrayList<Bill> jsonToBills (String json) 
	{
		ArrayList<Bill> result = new ArrayList<Bill>();
		
		try
		{
			Bill bill = null;
			
			JSONArray jArray = new JSONArray(json);
			
			for (int i = 0; i < jArray.length(); i++)
			{
				bill = jsonToBill ((JSONObject)jArray.get(i));
				result.add(bill);
			}
		}
		catch (JSONException jse)
		{
			
		}
		
		return result;
	}
	
	private Bill jsonToBill (JSONObject jObj) throws JSONException
	{
		Bill bill = new Bill();
		
		bill.setSenateBillNo(jObj.getString("senateId"));
		
		//bill.setSponsor(new Person(jObj.getString("sponsor"), null));
		
		bill.setSummary(jObj.getString("summary"));
		
		return bill;
	}
	
}
