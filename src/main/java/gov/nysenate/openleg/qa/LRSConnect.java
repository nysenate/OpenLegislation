package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Person;
import gov.nysenate.openleg.util.SessionYear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

public class LRSConnect {
	private static final int PORT = 80;
	
	private static final String TEMP_FILE_NAME = "lbdc.bill.temp";
	
	public static void main(String[] args) throws IOException, org.apache.lucene.queryParser.ParseException, InterruptedException {
		
		TreeSet<String> set = new TreeSet<String>();
		
		set.addAll(Arrays.asList("summary","billno","year","sponsor","cosponsors","when","sameas","committee","status","location","session-type","chair"));
		
		
		for(String s:set) {
			System.out.print("\"" + s + "\", ");
		}
//		
//		ReportBuilder reportBuilder = new ReportBuilder();
//		TreeSet<ReportBill> bills = reportBuilder.getBillReportSet("2011");
//		
//		LRSConnect l = LRSConnect.getInstance();
//		
//		for(ReportBill bill:bills) {
//			if(bill.getHeat() < 7)
//				break;
//			
//			System.out.print("testing bill " + bill.getBill());
//			
//			if(l.getBillFromLbdc(bill.getBill()) == null)
//				System.out.println(" -- delete " + bill.getMissingFields());
//			else
//				System.out.println(" -- keep " + bill.getMissingFields());
//			
//			
//			Thread.currentThread().sleep(3600);
//		}
	}
	
	private static final String KEY_URL = "http://public.leginfo.state.ny.us/menugetf.cgi";
	private static final String BASE_URL = "http://public.leginfo.state.ny.us";
	private static final String APPLICATION = "/bstfrmef.cgi?";
	private static final String QUERY_TYPE = "QUERYTYPE=BILLNO";
	private static final String SESSION_YEAR = "&SESSYR=";
	private static final String QUERY_DATA = "&QUERYDATA=";
	private static final String QQ_DATA = "&QQDATA=";
	private static final String GET_SEL = "&GETSEL=";
	private static final String LST = "&LST=";
	private static final String BROWSER = "&BROWSER=Netscape";
	private static final String TOKEN = "&TOKEN=";
	private static final String SELECT = "&SELECT=TEXT++&SELECT=STATUS++&SELECT=SPMEMO++&SELECT=SUMMARY++&SELECT=HISTORY";
	
	private static Logger logger = Logger.getLogger(LRSConnect.class);
	
	private final String key;
	
	public static LRSConnect getInstance() {
		String key = getInstanceKey();
		
		if(key == null)
			return null;
		else
			return new LRSConnect(key);
	}
	
	private LRSConnect(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	private String constructUrlFile(String billNumber, String year) {
		return APPLICATION + QUERY_TYPE + SESSION_YEAR + year 
						+ QUERY_DATA + billNumber + QQ_DATA 
						+ billNumber + GET_SEL + LST + BROWSER 
						+ TOKEN + this.key + SELECT;
	}
	
	public Bill getBillFromLbdc(String bill) {
		if(bill.indexOf("0") == -1)
			return getBillFromLbdc(bill, SessionYear.getSessionYear()+"");
		else {
			String[] strings = bill.split("-");
			return getBillFromLbdc(strings[0], strings[1]);
		}
	}
	
	public Bill getBillFromLbdc(String billNumber, String year) {
		Bill bill = new Bill();
		File file = new File(TEMP_FILE_NAME);
		try {
			writeDataFromLbdc(billNumber, year);
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			//skip past http header
			while(!br.readLine().equals("Content-type: text/html"));
			br.readLine();
			
			String in = null;
			
			String status = null;
			String summary = null;
			String text = null;
			String memo = null;
					
			while((in = br.readLine()) != null) {
				if(in.contains("<B>STATUS:</B>")) {					
					status = "";
				}
				else if(in.contains("<B>SUMMARY:</B>")) {					
					summary = "";
				}
				else if(in.contains("<B>BILL TEXT:</B>")) {					
					text = "";
				}
				else if(in.contains("<B>SPONSORS MEMO:</B>")) {					
					memo = "";
				}
				
				if(memo != null) {
					memo += in;
				}
				else if(text != null) {
					text += in;
				}
				else if(summary != null) {
					summary += in;
				}
				else if(status != null) {
					status += in;
				}
			}
			br.close();
					
			bill.setSenateBillNo(bill + "-" + year);
			
			if(parseStatus(status, bill)
					&& parseSummary(summary, bill)
					&& parseText(text, bill)
					&& parseMemo(memo, bill)) {
				//success
				
			}
			else {
				bill = null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		finally {
			if(file.exists())
				file.delete();
		}
		
		return bill;
	}
	
	public boolean parseStatus(String status, Bill bill) {
		if(status == null)
			return false;
		
		status = status.replaceAll("(?i)<br>","\n")
			.replaceAll("&nbsp;"," ")
			.replaceAll("(?i)(</?tr.*?>)", "\n\t$1")
			.replaceAll("(?i)<td>(.*?)</td>","\n\t\t$1")
			.replaceAll("(?i)</?(meta|html|table|a|th|body|hr|tr|td|font|b|strong).*?>","")
			.replaceAll("(\t|\n|\\s){2,}","\n")
			.replaceAll("No Same as","No Same as\nNo Same as Sponsor");
	
		if(status.contains("Bill Status Information Not Found"))
			return false;
		
		String strings[] = status.split("\n");
		
		bill.setSponsor(new Person(strings[2]));		
		
		String sameAsBillNo = strings[3].replaceAll("(Same as| |\\-)","");
		bill.setSameAs(sameAsBillNo);
		
		bill.setLawSection(strings[5]);
		
		String title = strings[6].replace("TITLE....","");
		bill.setTitle(title);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		for(int i = 7; i < strings.length;) {
			if(strings[i].matches("\\d{2}/\\d{2}/\\d{2}")) {
				BillEvent be = new BillEvent();
				try {
					
					be.setEventDate(sdf.parse(strings[i]));
					i++;
					be.setEventText(strings[i]);
					
					bill.addBillEvent(be);
				} catch (ParseException e) {
					logger.error(e);
				}
				i++;
			}
			else {
				if(strings[i].charAt(0) == sameAsBillNo.charAt(0)) {
					i += 2;
					while(strings[i].matches("\\d{2}/\\d{2}/\\d{2}")) {
						i += 2;
					}
					i += 2;
				}
				else {
					break;
				}
			}
		}		
		return true;
	}
	
	public boolean parseSummary(String summary, Bill bill) {
		if(summary == null)
			return false;
		
		summary = summary.replaceAll("(?i)<br>","\n")
			.replaceAll("(?i)</?(hr|b).*?>","")
			.replaceAll("(\t|\n|\\s){2,}","\n");
		
		if(summary.contains("Bill Summary Information Not Available"))
			return false;
				
		String[] strings = summary.split("\n");
		
		if(strings.length == 3) {
			bill.setSummary(strings[2]);
		}
		else {
			bill.setSummary(strings[3]);
		}
		
		
		
		return true;
	}
	
	public boolean parseText(String text, Bill bill) {
		if(text == null)
			return false;
			
		text = text.replaceAll("(?i)<br>","\n")
			.replaceAll("(?i)</?(hr|b|html|head|style|title|basefont|font|pre|u|!\\-\\-).*?>","");

		String[] strings = text.split("\n");
		
		bill.setFulltext(strings[1]);
		
		return true;
	}
	
	public boolean parseMemo(String memo, Bill bill) {
		if(memo == null)
			return false;
		
		/*memo = memo.replaceAll("&nbsp;", "").replaceAll("(?i)<br>","\n")
			.replaceAll("&nbsp","")
			.replaceAll("(?i)</?(hr|b|html|head|style|title|basefont|font|pre|u|center|!\\-\\-).*?>","");
	
		if(memo.contains("Memo Text Not Found"))
			return false;
		
		String[] strings = memo.split("\n");
		
		bill.setMemo(strings[5]);*/
		
		return true;
	}
	
	private static String getInstanceKey() {
		String key = null;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL(KEY_URL).openStream()));
			
			Pattern tokenPattern = Pattern.compile("<FRAME NAME=\"TOP\" src=\"frmload.cgi\\?TOP-(\\d+)\">");
			Matcher tokenMatcher = null;
			
			String in = null;
			
			while((in = br.readLine()) != null) {
				
				tokenMatcher = tokenPattern.matcher(in);
				
				if(tokenMatcher.find()) {
					key = tokenMatcher.group(1);
				}
			}
			br.close();
		}
		catch (IOException ioe) {
			logger.error(ioe);
		}
		
		return key;
	}
	
	private SocketChannel getSocketChannel(String url, String file) throws IOException {
		URL connectUrl = new URL(url);
		String host = connectUrl.getHost();
		
		SocketAddress remote = new InetSocketAddress(host, PORT);
		SocketChannel channel = SocketChannel.open(remote);
				
		String request = "GET " + file + " HTTP/1.1\r\n" + "User-Agent: HTTPGrab\r\n" +
			"Accept: text/*\r\nConnection: close\r\nHost: " + host + "\r\n\r\n";
		
		ByteBuffer header = ByteBuffer.wrap(request.getBytes("US-ASCII"));
		channel.write(header);
		
		return channel;
	}
	
	private void writeDataFromLbdc(String billNumber, String year) throws IOException {
		SocketChannel channel = getSocketChannel(BASE_URL, this.constructUrlFile(billNumber, year));
		
		FileOutputStream out = new FileOutputStream(TEMP_FILE_NAME);
		FileChannel local = out.getChannel();
		
		ByteBuffer buffer = ByteBuffer.allocate(131072);
		while(channel.read(buffer) != -1) {
			buffer.flip();
			local.write(ByteBuffer.wrap(Charset.forName("ISO-8859-1").decode(buffer).toString().getBytes()));
			buffer.clear();
		}
		
		local.close();
		channel.close();
	}
}
