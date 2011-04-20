package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.api.ApiHelper;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;

public class MemoTester {
	public static void main(String[] args) throws IOException, ParseException {
		
		if(args.length == 0) {
			System.out.println("please provide memo file");
			System.exit(0);
		}
		
		String filePath = args[0];
	
		
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		
		String in = null;
		while((in = br.readLine()) != null) {
			String billNo = "S" + in + "-2011";
			
			SenateResponse srs = SearchEngine.getInstance().search("otype:bill AND oid:" + billNo, "json", 0, 1, null, true);
			
			if(srs.getResults().isEmpty())
				continue;
			
			Result result = srs.getResults().iterator().next();
			String jsonData = result.data;
			
			jsonData = jsonData.substring(jsonData.indexOf(":")+1);
			jsonData = jsonData.substring(0,jsonData.lastIndexOf("}"));
			
			Bill bill = (Bill) ApiHelper.getMapper().readValue(jsonData,  Bill.class);
			
			if(!bill.getSenateBillNo().equals(billNo))
				continue;
			
			if(bill.getMemo() == null || bill.getMemo().matches("\\s*")) {
				System.out.println(pad(in, "0"));
			}
		}
	}
	
	public static String pad(String string, String c) {
		while(!string.matches("\\d{5}.*")) {
			string = c + string;
		}
		return string;
	}
}
