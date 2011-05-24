package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.qa.model.CouchInstance;
import gov.nysenate.openleg.qa.model.CouchSupport;
import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.LbdcFile;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.qa.model.LbdcFile.AssociatedFields;
import gov.nysenate.openleg.util.SessionYear;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.ektorp.http.StdHttpClient;

public class ReportReader extends CouchSupport {
	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption("ft", "file-type", true, "(bill_html|memo|paging)");
		options.addOption("f", "path-to-file", true, "path to file being parsed");
		options.addOption("m", "report-missing-data", false, "refresh report on missing data");
		options.addOption("r", "reset-couch", false, "reset couchdb");
		options.addOption("h", "help", false, "print this message");
		
		try {
		    CommandLine line = parser.parse(options, args);
		    
		    if(line.hasOption("-h")) {
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp("posix", options );
		    }
		    else {
		    	ReportReader reader = new ReportReader();
		    	
		    	if(line.hasOption("reset-couch")) {
		    		CouchInstance instance = CouchInstance.getInstance("test", true, new StdHttpClient.Builder().build());
		    		instance.getDbInstance().deleteDatabase(CouchSupport.DATABASE_NAME);
		    	}
		    	else if(line.hasOption("file-type") && line.hasOption("path-to-file")) {
		    		ReportType reportType = null;
		    		String fileType = line.getOptionValue("file-type");
		    		
		    		if(fileType.equalsIgnoreCase("bill_html"))
		    			reportType = ReportType.BILL_HTML;
		    		else if(fileType.equalsIgnoreCase("memo"))
		    			reportType = ReportType.MEMO;
		    		else if(fileType.equalsIgnoreCase("paging"))
		    			reportType = ReportType.PAGING;
		    		
		    		if(reportType==null)
		    			throw new org.apache.commons.cli.ParseException("invalid file type: " + fileType);
			       
			        reader.processFile(line.getOptionValue("path-to-file"), reportType);
			    }
		    	else if(line.hasOption("report-missing-data")) {
		    		reader.reportMissingData();
		    	}
		    	else {
		    		throw new org.apache.commons.cli.ParseException("use with -h for options");
		    	}
		    }
		}
		catch( org.apache.commons.cli.ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
	}
	
	/*
	 * report files come in three flavors:
	 * 		1) an html dump of actions, summary, sponsors, law section
	 * 		2) file of bills that have memos
	 * 		3) csv containing length of bill texts
	 */
	public enum ReportType { 
		BILL_HTML, MEMO, PAGING
	}
	
	private Logger logger = Logger.getLogger(ReportReader.class);
	
	public void processFile(String fileName, ReportType reportType) {
		processFile(new File(fileName), reportType);
	}
	
	public void processFile(File file, ReportType reportType) {
		logger.info("Processing file: " + file.getAbsolutePath() + " of type " + reportType);
		
		LbdcFile lbdcFile = null;
		switch(reportType) {
			case BILL_HTML:
				lbdcFile = new LbdcFileHtml(file);
				break;
			case MEMO:
				lbdcFile = new LbdcFileMemo(file);
				break;
			case PAGING:
				lbdcFile = new LbdcFilePaging(file);
		}
		
		FieldName[] fieldNames = new FieldName[0];
		
		AssociatedFields associatedFields = lbdcFile.getClass().getAnnotation(AssociatedFields.class);
		if(associatedFields != null) {
			fieldNames = associatedFields.value();
		}
		
		ArrayList<ProblemBill> problemBills = lbdcFile.getProblemBills(fieldNames);
		
		logger.info("Found " + problemBills.size() + " problematic bills");
		
		pbr.createOrUpdateProblemBills(problemBills, true);
		pbr.deleteNonProblemBills();
		pbr.rankProblemBills();
	}
	
	public void reportMissingData() {
		try {
			refreshMissingData();
			pbr.deleteNonProblemBills();
			pbr.rankProblemBills();
		} catch (ParseException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	private void refreshMissingData() throws ParseException, IOException {
		logger.info("Refreshing missing data");
		
		List<ProblemBill> problemBillList = pbr.findByMissingFields();
		logger.info("Found " + problemBillList.size() + "bills already missing fields");
		
		ReportBuilder reportBuilder = new ReportBuilder();
		HashMap<String, ProblemBill> reportedBillMap = 
				reportBuilder.getBillReportSet(SessionYear.getSessionYear() + "");
		logger.info("Found " + reportedBillMap.size() + " bills missing fields in the index");
				
		for(ProblemBill problemBill:problemBillList)
		{
			//if bill was in missing report but no longer clear missingFields
			if(reportedBillMap.get(problemBill.getOid()) == null)
			{
				problemBill.setMissingFields(null);
				instance.getConnector().addToBulkBuffer(problemBill);
			}
		}
		
		pbr.createOrUpdateProblemBills(reportedBillMap.values(), true);
		
		instance.getConnector().flushBulkBuffer();
		instance.getConnector().clearBulkBuffer();		
	}
	
	public List<ProblemBill> getProblemBills() {
		return pbr.findProblemBillsByRank();
	}
}
