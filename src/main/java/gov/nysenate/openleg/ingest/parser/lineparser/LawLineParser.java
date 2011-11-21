package gov.nysenate.openleg.ingest.parser.lineparser;

import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.model.bill.Bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LawLineParser implements LineParser {
	private static final Pattern SECTION = Pattern.compile("(›|\\xF5|•À)");
	private static final Pattern CONTROL = Pattern.compile("\\xBD");
	
	private Matcher SECTION_MATCHER = SECTION.matcher("");
	private Matcher CONTROL_MATCHER = CONTROL.matcher("");
	
	private StringBuffer lawBuffer = new StringBuffer();
	private StringBuffer tempLawBuffer = new StringBuffer();
	public void parseLineData(String line, String lineData, BillParser billParser) {
		if(lineData.contains("DELETE")) return;
		
		lineData = BillParser.replace(SECTION_MATCHER, lineData, "S");
		lineData = BillParser.replace(CONTROL_MATCHER, lineData, "");
		
		if(lawBuffer.toString().trim().contains(lineData)) {				
			tempLawBuffer.append(lineData);
			tempLawBuffer.append(" ");
			
			if(lawBuffer.equals(tempLawBuffer)) {
				tempLawBuffer.setLength(0);
			}
		}
		else {
			if(tempLawBuffer.length() > 0) {
				lawBuffer.append(tempLawBuffer);
				tempLawBuffer.setLength(0);
			}
			
			lawBuffer.append(lineData);
			lawBuffer.append(" ");
		}
	}

	public void saveData(Bill bill) {
		if(lawBuffer.length() > 0) {
			bill.setLaw(lawBuffer.toString().trim());
		}
	}

	public void clear() {
		lawBuffer.setLength(0);
		tempLawBuffer.setLength(0);
	}
}
