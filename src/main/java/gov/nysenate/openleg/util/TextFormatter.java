package gov.nysenate.openleg.util;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
