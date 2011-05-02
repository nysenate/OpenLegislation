package gov.nysenate.openleg.search;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.lucene.Lucene;
import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneResult;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;

public class SearchEngine extends Lucene implements OpenLegConstants {
	
	public static void main(String[] args) throws Exception {
		SearchEngine engine = new SearchEngine();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String line = null;
		System.out.print("openlegLuceneConsole> ");
		while (!(line = reader.readLine()).equals("quit"))
		{
			if (line.startsWith("optimize"))
				engine.optimize();
			else if (line.startsWith("delete"))
			{
				StringTokenizer cmd = new StringTokenizer(line.substring(line.indexOf(" ")+1)," ");
				String type = cmd.nextToken();
				String id = (cmd.hasMoreTokens() ? cmd.nextToken() : null);
				engine.deleteSenateObjectById(type, id);
			}
			else if (line.startsWith("create"))
				engine.createIndex();
			else {
				SenateResponse sr = engine.search(line, "xml", 1, 10, null, false);
				if(sr != null && !sr.getResults().isEmpty()) {
					for(Result r:sr.getResults()) {
						System.out.println(r.getOid());
					}
				}
			}
			
			System.out.print("openleg search > ");
		}
		System.out.println("Exiting Search Engine");
	}
	
	private static SearchEngine searchEngine = null;
	
	public static synchronized SearchEngine getInstance() {
		if(searchEngine == null) {
			searchEngine = new SearchEngine();
		}
		return searchEngine;
	}
	
	private SearchEngine() {
		super("/usr/local/openleg/lucene/2");
		logger = Logger.getLogger(SearchEngine.class);
	}

	protected DateFormat DATE_FORMAT_MEDIUM = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
    
    public void deleteSenateObject (ILuceneObject obj) throws Exception
    {
    	if (obj instanceof Agenda) {
    		Agenda agenda = (Agenda)obj;
    		if (agenda.getAddendums() != null)
	    		for( Addendum addendum : agenda.getAddendums() ) 
	    			for( Meeting meeting : addendum.getMeetings() ) {
	    				deleteSenateObject( meeting );
	    			}
    	}
    	else if(obj instanceof Calendar) {
    		Calendar calendar = (Calendar)obj;
    		if(calendar.getSupplementals() != null) {
    			for(Supplemental supplemental:calendar.getSupplementals()) {
    				deleteSenateObject(supplemental);
    			}
    		}
    	}
    	else {
    		if(obj instanceof Bill) {
    			Bill bill = (Bill)obj;
        		if(bill.getBillEvents() != null) {
        			for(BillEvent be:bill.getBillEvents()) {
        				deleteSenateObject(be);
        			}
        		}
        		if(bill.getVotes() != null) {
        			for(Vote vote:bill.getVotes()) {
        				deleteSenateObject(vote);
        			}
        		}
    		}
    		
    		deleteSenateObjectById(obj.luceneOtype(), obj.luceneOid());
    	}
    }
    
    public void deleteSenateObjectById (String type, String id) throws Exception {
    	closeSearcher();
    	deleteDocuments(type, id);
    	openSearcher();
    }
	
    public  boolean indexSenateObjects (Collection<ILuceneObject> objects, LuceneSerializer[] ls) throws IOException
    {
    	createIndex ();
        Analyzer  analyzer    = getAnalyzer();
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, false, MaxFieldLength.UNLIMITED);
       
    	Iterator<ILuceneObject> it = objects.iterator();
    	while (it.hasNext()) {
    		ILuceneObject obj = it.next();
    		
    		if (obj instanceof Calendar) {
    			Calendar cal = (Calendar)obj;
    			
    			Iterator<Supplemental> itSupps = cal.getSupplementals().iterator();
    			while (itSupps.hasNext()) {
    				Supplemental supp = (Supplemental)itSupps.next();
        			supp.setCalendar(cal);
        			supp.setLuceneModified(cal.getLuceneModified());

    				try {
    	    			addDocument(supp, ls, indexWriter);
    	    		}
    	    		catch (Exception e) {
    	    			logger.warn("unable to index senate supp",e);
    	    		}
    			}
    		}
    		else if(obj instanceof Agenda) {
    			Agenda agenda = (Agenda)obj;
    			
    			if (agenda.getAddendums() != null) {
    	    		for( Addendum addendum : agenda.getAddendums()) {
    	    			addendum.setAgenda(agenda);
    	    			for( Meeting meeting : addendum.getMeetings() ) {
    	    				meeting.setAddendums(new ArrayList<Addendum>(Arrays.asList(addendum)));
    	    				meeting.setLuceneModified(agenda.getLuceneModified());
    	    				try {
    	    					addDocument(meeting, ls, indexWriter);
    	    				}
    	    				catch (Exception e) {
    	    					logger.warn("unable to index senate meeting",e);
    	    				}
    	    			}
    	    		}
    			}
    		}
    		else if(obj instanceof Bill) {
    			Bill bill = (Bill)obj;
    			
    			this.deleteDocumentsByQuery("otype:action AND billno:" + bill.getSenateBillNo(), indexWriter);
    			
    			if(bill.getBillEvents() != null) {
    				for(BillEvent be:bill.getBillEvents()) {
    					be.setLuceneModified(bill.getLuceneModified());
    					try {
							addDocument(be, ls, indexWriter);
						} catch (InstantiationException e) {
							logger.warn(e);
						} catch (IllegalAccessException e) {
							logger.warn(e);
						}
    				}
    			}
    			
    			this.deleteDocumentsByQuery("otype:vote AND billno:" + bill.getSenateBillNo(), indexWriter);
    			
    			if(bill.getVotes() != null) {
    				for(Vote vote: bill.getVotes()) {
    					vote.setLuceneModified(bill.getLuceneModified());
    					try {
    						vote.setBill(bill);
							addDocument(vote, ls, indexWriter);
						} catch (InstantiationException e) {
							logger.warn(e);
						} catch (IllegalAccessException e) {
							logger.warn(e);
						}
    				}
    			}
    			
    			try {
					addDocument(bill, ls, indexWriter);
				} catch (InstantiationException e) {
					logger.warn(e);
				} catch (IllegalAccessException e) {
					logger.warn(e);
				}
    		}
    		else {
	    		try {
	    			addDocument(obj, ls, indexWriter);
	    		}
	    		catch (Exception e) {
	    			logger.warn("unable to index senate object: " + obj.getClass().getName(),e);
	    		}
	    	}
    	}
    	
    	indexWriter.commit();
    	
    	logger.info("done indexing objects(" + objects.size() + "). Closing index.");
    	indexWriter.close();
    	return true;
    }
    
	public SenateResponse search(String searchText, String format, int start, int max, String sortField, boolean reverseSort) throws ParseException, IOException {
    	String data = "o"+format.toLowerCase()+"";
    	
    	LuceneResult result = search(searchText,start,max,sortField,reverseSort);
    	
    	SenateResponse response = new SenateResponse();
    	
    	if (result == null)
    	{
    		response.addMetadataByKey("totalresults", 0 );
    	}
    	else
    	{
	    	response.addMetadataByKey("totalresults", result.total );
	    	
	    	for (Document doc : result.results) {
	    		String lastModified = doc.get("modified");
	    		if (lastModified == null || lastModified.length() == 0)
	    			lastModified = new Date().getTime()+"";
	    		
	    		response.addResult(new Result(
	    				doc.get("otype"),
	    				doc.get(data),
	    				doc.get("oid"),
	    				Long.parseLong(lastModified),
	    				Boolean.parseBoolean(doc.get("active"))));
	    	}
	    	
    	}
	    	
    	return response;
	}
	
}