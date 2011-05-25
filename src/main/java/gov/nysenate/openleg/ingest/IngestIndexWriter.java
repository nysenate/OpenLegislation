package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;

public class IngestIndexWriter {
	private Logger logger = Logger.getLogger(IngestIndexWriter.class);
	
	JsonDao ingestJson;
	SearchEngine searchEngine;
	
	public IngestIndexWriter(SearchEngine searchEngine, JsonDao ingestJson) {
		this.searchEngine = searchEngine;
		this.ingestJson = ingestJson;
	}
	
	public void indexSenateObject(ISenateObject obj) {
		logger.warn("Indexing object " + obj.luceneOid());
		try {
			searchEngine.indexSenateObjects(
					new ArrayList<ILuceneObject>(
						Arrays.asList(obj)), 
						new LuceneSerializer[]{
							new XmlSerializer(), 
							new JsonSerializer()});
		} catch (IOException e) {
			logger.warn("Exception while indexing object", e);
		}
	}
	
	/**
	 * desirable to hide old versions of an amended bill from the default search
	 * this appends "active:false" as a field to any old versions of bills
	 * 
	 * to avoid constantly rewriting amended versions of bills this does a query
	 * to lucene to check if they've already been hidden, if they haven't then 
	 * they are sent to reindexInactiveBill
	 * 
	 * @param bill
	 * 
	 * returns true if current bill isn't searchable, false otherwise
	 */
	public boolean reindexAmendedVersions(Bill bill) {
		int idx = bill.getSenateBillNo().indexOf("-");
		char c = bill.getSenateBillNo().charAt(idx-1);
		String strings[] = bill.getSenateBillNo().split("-");
		
		String query = null;
		
		if(c >= 65 && c < 90)
			query = strings[0].substring(0, strings[0].length()-1);
		else 
			query = strings[0];
			
		try {
			//oid:(S418-2009 OR [S418A-2009 TO S418Z-2009]) AND year:2009
			query = "otype:bill AND oid:((" 
				+ query + "-" + strings[1] 
                    + " OR [" + query + "A-" + strings[1] 
                       + " TO " + query + "Z-" + strings[1]
                    + "]) AND " + query + "*-" + strings[1] + ")";
			
			SenateResponse sr = searchEngine.search(query,
					"json", 0,100, null, false);
						
			//if there aren't any results this is a new bill
			if(sr.getResults().isEmpty())
				return false;
								
			//create a list and store bill numbers from oldest to newest
			ArrayList<String> billNumbers = new ArrayList<String>();				
			for(Result result:sr.getResults()) {
				billNumbers.add(result.getOid());
			}
			if(!billNumbers.contains(bill))
				billNumbers.add(bill.getSenateBillNo());
			Collections.sort(billNumbers);
			
			String newest = billNumbers.get(billNumbers.size()-1);
			
			//if bill being stored isn't the newest we can assume
			//that the newest bill has already reindexed older bills
			if(!bill.getSenateBillNo().equals(newest))
				return true;
			
			billNumbers.remove(newest);
			billNumbers.remove(bill.getSenateBillNo());				
							
			for(Result result:sr.getResults()) {
				if(billNumbers.contains(result.getOid())) {
					if(result.isActive()) {
						reindexInactiveBill(result.getOid(), bill.getYear()+"");
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void reindexInactiveBill(String senateBillNo, String year) {
		Bill temp = (Bill)ingestJson.loadSenateObject(senateBillNo,
				year,
				"bill",
				Bill.class);
		
		if(temp != null) {
			temp.setLuceneActive(false);
			
			this.indexSenateObject(temp);
		}
	}
	
	public boolean deleteSenateObject(ISenateObject so) {
		try {
			searchEngine.deleteSenateObject(so);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
