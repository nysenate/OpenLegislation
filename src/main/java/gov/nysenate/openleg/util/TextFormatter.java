package gov.nysenate.openleg.util;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.model.Bill;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspWriter;

public class TextFormatter {

	public static String addHyperlinks (String input)
	{
		 Pattern pattern = Pattern.compile("(Senate Bill Number)\\s(\\w*)");
		 Matcher matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/bill/S$2\">Senate Bill Number $2</a></b>");
		 
		 pattern = Pattern.compile("(Senate Print Number)\\s(\\w*)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/bill/S$2\">Senate Print Number $2</a></b>");
		 
		 pattern = Pattern.compile("(Assembly Bill Number)\\s(\\w*)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/bill/A$2\">Assembly Bill Number $2</a></b>");
		 
		 pattern = Pattern.compile("(Assembly Print Number)\\s(\\w*)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/bill/A$2\">Assembly Print Number $2</a></b>");

		 pattern = Pattern.compile("(SENATOR\\s)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b>$1</b>");
		 
		 pattern = Pattern.compile("(ACTING PRESIDENT\\s)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b>$1</b>");
		 
		 
		 pattern = Pattern.compile("(THE SECRETARY)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b>$1</b>");
		 
		
		 /*
		// pattern = Pattern.compile("(Senator)\\s(^[A-Z]'?[- a-zA-Z]( [a-zA-Z])*)");
		 pattern = Pattern.compile("(Senator)\\s(\\w*)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/sponsor/$2\">$1 $2</a></b>");
		 
		 pattern = Pattern.compile("(ACTING PRESIDENT)\\s(\\w*)");
		 matcher = pattern.matcher(input);
		 input = matcher.replaceAll("<b><a href=\"/legislation/api/html/sponsor/$2\">$1 $2</a></b>");*/
 
		 
		 return input;
	}
	
	public final static String TRANSCRIPT_INDENT = "             ";
	public final static String TRANSCRIPT_INDENT_REPLACE = "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	
	public static String removeLineNumbers (String input)
	{
		StringBuffer resp = new StringBuffer();
		
		StringTokenizer st = new StringTokenizer (input,"\n");
		String line = null;
		int breakIdx = -1;
		
		while (st.hasMoreTokens())
		{
			line = st.nextToken().trim();
			
			breakIdx = line.indexOf(' ');
		
			if (breakIdx != -1)
			{
				
				line = line.substring(breakIdx+1);
				
				if (line.startsWith("Transcription Service, Inc."))
					continue;
				if (line.startsWith("371-8910"))
					continue;
				
				if (line.startsWith(TRANSCRIPT_INDENT))
					resp.append(TRANSCRIPT_INDENT_REPLACE);
				
				line = line.trim();
				
				resp.append(' ');
				resp.append(line);
			}
			
			
			
		}
		
		
		String output =  resp.toString();
		

		output = output.replace("SENATOR", "<br/>SENATOR");
		
		output = output.replace("REVEREND", "<br/>REVEREND");

		output = output.replace("ACTING", "<br/>ACTING");
		
		output = output.replace("REGULAR SESSION", "REGULAR SESSION<br/><br/>");
		
		return output;
		
	}
	
	public static String removeBillLineNumbers (String input)
	{
		StringBuffer resp = new StringBuffer();
		
		input = input.replace("S E N A T E","SENATE");
		input = input.replace("A S S E M B L Y","ASSEMBLY");
		
		StringTokenizer st = new StringTokenizer (input,"\n");
		String line = null;
		int breakIdx = -1;
		
		String startChar = null;
		boolean isLineNum = false;
		
		while (st.hasMoreTokens())
		{
			line = st.nextToken().trim();

			line = line.replace(" S ","<br/><br/>S ");
			line = line.replace(" ¤ ","<br/><br/>¤ ");
			line = line.replace(" Section ","<br/><br/>Section ");
			line = line.replace("AN ACT ","<br/><br/>AN ACT ");
			line = line.replace("THE  PEOPLE ","<br/><br/>THE PEOPLE ");
			line = line.replace("_","");
			
			
			breakIdx = line.indexOf(' ');
		
			if (breakIdx != -1)
			{
				
				startChar = line.substring(0,breakIdx);
			
				try 
				{	
					Integer.parseInt(startChar);
					isLineNum = true;
				}
				catch (NumberFormatException nfe)
				{
					isLineNum = false;
				}
				
				if (isLineNum)
				{
					line = line.substring(breakIdx+1).trim();
				}
				
				int sepIdx = -1;
				
				if (line.endsWith(":"))
				{
					line = line + "<br/>";
					
					
				}
				
			
				resp.append(' ');
				
			
				if (line.endsWith("-"))
					line = line.substring(0,line.length()-1).trim();
				

				resp.append(line);
				
				
			}
			else
			{
				resp.append(' ');
				
				resp.append(line);
				
				resp.append("<br/>");
			}
			
		
			
			
		
			
			
		}
		
		
		String output =  resp.toString();
		

		return output;
		
	}
	
	public static String formatMemo (String input)
	{
		StringBuffer resp = new StringBuffer();
		
		StringTokenizer st = new StringTokenizer (input,"\n");
		String line = null;
		int breakIdx = -1;
		
		String startChar = null;
		boolean isLineNum = false;
		
		while (st.hasMoreTokens())
		{
			line = st.nextToken();
			
			line = line.replace("Section", "<br/><br/>Section");
			line = line.replace("- ", "<li/>");
			breakIdx = line.indexOf(' ');
		
			if (breakIdx != -1)
			{
				
				startChar = line.substring(0,breakIdx);
			
				try 
				{	
					Integer.parseInt(startChar);
					isLineNum = true;
				}
				catch (NumberFormatException nfe)
				{
					isLineNum = false;
				}
				
				if (isLineNum)
				{
					line = line.substring(breakIdx+1).trim();
				}
				
				int sepIdx = -1;
				
				if (line.endsWith(" "))
					line = line.substring(0,line.length()-1).trim();
				
				if (line.trim().endsWith("-"))
					line = line.substring(0,line.length()-1).trim();
			
				if (line.indexOf(":")>-1 && line.indexOf(":")<50)
					resp.append("<br/><br/>");
				
				resp.append(line.trim());
				resp.append(' ');
				
			}
			else
			{
				resp.append(line);
				resp.append("<br/><br/>");
			}
			
			
			
		
			
			
		}
		
		
		String output =  resp.toString();
		

		return output;
		
	}
	
	public static String clean(String s) {
    	s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("'", "&apos;");
		s = s.replaceAll("<","&lt;");
		s = s.replaceAll(">","&gt;");
		s = s.replaceAll("\"","&quot;");
		
		return s;
    }
		
	public void lrsPrinter(Bill bill, JspWriter out) {
		bill = PMF.getDetachedBill("S66026");
		
		StringTokenizer st = new StringTokenizer(bill.getFulltext(), "\n");


		boolean redact = false;
		int r_start = -1;
		int r_end = -1;
		boolean cap = false;
		int capCount = 0;
		int start = -1;
		int end = -1;
		
		List<TextPoint> points;

		while(st.hasMoreTokens()) {
			String line = st.nextToken();
			
			Pattern p = Pattern.compile("^\\s{3,4}\\d{1,2}\\s*");
			Matcher m = p.matcher(line);
			
			points = new ArrayList<TextPoint>();

			if(m.find()) {
				String text = line.substring(m.end());
				String lineNo = line.substring(m.start(), m.end());
				
				char[] textChar = text.toCharArray();
				
				for(int i = 0; i < textChar.length; i++) {
					if(textChar[i] == '[') {
						redact = true;
						r_start = i+1;
					}
					else if(textChar[i] == ']') {
						r_end = i;						
						points.add(new TextPoint(r_start,r_end,false));
						
						r_start = -1;
						r_end = -1;
						redact = false;
					}
					
					if(Character.isUpperCase(textChar[i])) {
						if(!cap) {
							cap = true;
							if(i < 6) {
								start = 0;
							}
							else {
								start = i;
							}
						}
						capCount++;
					}
					else if(Character.isLowerCase(textChar[i])) {
						if(cap) {
							if(capCount > 2) {
								end = i - 1;
								points.add(new TextPoint(start,end,true));
							}
							start = -1;
							end = -1;
							capCount = 0;
							cap = false;
						}
					}
				}
				
				if(cap) {
					text += "</u>";

					if(start != -1) {
						text = text.substring(0,start) + "<u>" + text.substring(start);
					}
					else {
						text = "<u>" + text;
					}
					start = -1;
					end = -1;
					capCount = 0;
				}
				if(redact) {
					text += "</del>";
					
					if(r_start != -1) {
						text = text.substring(0,r_start) + "<del>" + text.substring(r_start);
					}
					else {
						text = "<del>" + text;
					}
					r_start = -1;
					r_end = -1;
				}
				
				Collections.reverse(points);
				for(TextPoint tp:points) {
					if(tp.s == -1) {
						tp.s = 0;
					}
					
					text = text.substring(0, tp.e) + (tp.uOrDel ? "</u>" : "</del>") + text.substring(tp.e);
					text = text.substring(0,tp.s) + (tp.uOrDel ? "<u>" : "<del>") + text.substring(tp.s);
					
					
				}

				try {
					out.write(lineNo + text + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				start = -1;
				end = -1;
				cap = false;
				capCount = 0;
			}
			else {
				try {
					out.write(line + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class TextPoint {
		public int s;
		public int e;
		public boolean uOrDel;
		
		public TextPoint(int s, int e, boolean uOrDel) {
			this.s = s;
			this.e = e;
			this.uOrDel = uOrDel;
		}
	}
}
