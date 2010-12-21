package gov.nysenate.openleg.search;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.util.JsonConverter;
import gov.nysenate.openleg.util.OriginalApiConverter;
import gov.nysenate.openleg.xstream.XStreamBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchEngine1Depr implements OpenLegConstants{

	private static Logger logger = Logger.getLogger(SearchEngine.class);

	private static IndexSearcher indexSearcher = null;
	
	private final  static String indexDir = "/usr/local/openleg/lucene";
//	private final  static String indexDir = "C:\\o-lucene\\";

	private static DateFormat DATE_FORMAT_MEDIUM = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
	
    /**
     * create index
     */
    public static boolean createIndex() throws IOException{
        if(true == ifIndexExist())
            return true;	
        
        Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, true, MaxFieldLength.LIMITED);
        
        indexWriter.optimize();
        indexWriter.close();
        return true;
    }
    
    /**
     * create index
     */
    public static boolean optimizeIndex() throws IOException{
        
        Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, false, MaxFieldLength.LIMITED);
        
        indexWriter.optimize();
        indexWriter.close();
        return true;

    }
    
    public static synchronized IndexSearcher openIndex() throws IOException {

    	if (indexSearcher == null)
    	{
    		boolean readOnly = true;
    		
    		Directory fsDirectory = FSDirectory.open(new File(indexDir));
    		indexSearcher = new IndexSearcher(fsDirectory, readOnly);
    	}
    	return indexSearcher;
    } 
    
    public static synchronized void closeIndex() throws IOException {
    	if (indexSearcher != null) {
			indexSearcher.close();
			indexSearcher = null;
    	}
    } 
    
    public  static  boolean indexSenateObjects (Collection<?> objects, PersistenceManager pm) throws IOException
    {
    	createIndex ();
    	
    	boolean overwrite = false;
    	
    	Directory fsDirectory = FSDirectory.open(new File(indexDir));
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, overwrite, MaxFieldLength.UNLIMITED);
       
    	Iterator<?> it = objects.iterator();
    	
    	while (it.hasNext()) {
    		Object obj = it.next();
    		
    		if (obj instanceof Calendar) {
    			Calendar cal = (Calendar)obj;
    			
    			Iterator<Supplemental> itSupps = cal.getSupplementals().iterator();
    			while (itSupps.hasNext()) {
    				Supplemental supp = (Supplemental)itSupps.next();
    				supp.setCalendar(cal);
    				
    				try {
    	    			indexSenateObject(supp, indexWriter, false, pm);
    	    		}
    	    		catch (Exception e) {
    	    			logger.warn("unable to index senate supp",e);
    	    		}
    			}
    		}
    		else {
	    		try {
	    			indexSenateObject(obj, indexWriter, false, pm);
	    		}
	    		catch (Exception e) {
	    			logger.warn("unable to index senate object: " + obj.getClass().getName(),e);
	    		}
	    	}
    	}
    	
    	logger.info("done indexing objects(" + objects.size() + ". Closing index.");
    	indexWriter.close();
    	return true;
    }
    
    public  static boolean indexSenateObject (Object obj, IndexWriter indexWriter, boolean closeIndex, PersistenceManager pm) throws Exception
    {
    	if (indexWriter == null) {
	    	createIndex ();
	    	
	    	Directory fsDirectory = FSDirectory.open(new File(indexDir));
	        
	        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
	        indexWriter = new IndexWriter(fsDirectory, analyzer, true, MaxFieldLength.UNLIMITED);
	        indexWriter.setMaxBufferedDeleteTerms(0);
    	}
    	
    	
    	HashMap<String,String> fields = new HashMap<String,String>();
    	StringBuilder searchContent = new StringBuilder();
    	
    	String type = null;
    	String id = null;
    	String title = null;
    	String summary = null;
    	 
    	if (obj instanceof Bill)
    	{
    		Bill bill = (Bill)obj;
    		
    		searchContent.append(bill.getSenateBillNo());
    		
    		if (bill.getSameAs() != null) {
    			searchContent.append(" - ").append(bill.getSameAs());
    			fields.put("sameas",bill.getSameAs());
    		}
    		if (bill.getSponsor()!=null) {
    			searchContent.append(" - ").append(bill.getSponsor().getFullname());
    			fields.put("sponsor", bill.getSponsor().getFullname());
    		}
    		
    		if (bill.getTitle() != null) {
    			searchContent.append(" - ").append(bill.getTitle());
//    			fields.put("title_sortby", bill.getTitle().replaceAll(" ", ""));
    		}
    		else if (bill.getSummary() != null) {
    			searchContent.append(" - ").append(bill.getSummary());
    			summary = bill.getSummary();
    		}
    		
            if (bill.getCoSponsors()!=null) {
            	StringBuilder cosponsor = new StringBuilder();
            	Iterator<Person> itCosp = bill.getCoSponsors().iterator();
            	while (itCosp.hasNext()) {
            		Person person = (Person)itCosp.next();
            		cosponsor.append(person.getFullname());
            		
            		if (itCosp.hasNext())
            			cosponsor.append(", ");
            	}
            	fields.put("cosponsors", cosponsor.toString());
            }
            
            fields.put("year", bill.getYear()+"");
            
            if (bill.getMemo()!=null)
            	fields.put("memo", bill.getMemo());
            
            if (bill.getFulltext()!=null)
            	fields.put("full", bill.getFulltext());
            
            if (bill.getCurrentCommittee()!=null)
            	fields.put("committee", bill.getCurrentCommittee());
            
            type = "bill";
            id = bill.getSenateBillNo();
            
            if (bill.getTitle()!=null)
            	title = bill.getTitle();
            else if (bill.getSummary()!=null)
            	title = bill.getSummary();
            
    	}
    	else if (obj instanceof Supplemental)
    	{
    		Supplemental supp = (Supplemental)obj;
    		Calendar calendar = supp.getCalendar();
    		type = "calendar";
    		id = calendar.getId();
    		fields.put("ctype",calendar.getType());
    		
    		
    		if (supp.getCalendarDate()!=null)
    			fields.put("when", supp.getCalendarDate().getTime()+"");
    		
    		title = calendar.getNo() + " - " + calendar.getType();
    		
    		if (supp.getCalendarDate()!=null)
    			title += " - " + DATE_FORMAT_MEDIUM.format(supp.getCalendarDate());
    		
    		
    		else if (supp.getReleaseDateTime()!=null)
    		{
    			title += " - " + DATE_FORMAT_MEDIUM.format(supp.getCalendarDate());
    		}
    		else if (supp.getSequence()!=null)
    		{
    			title += " - " + DATE_FORMAT_MEDIUM.format(supp.getSequence().getActCalDate());
    		}
    		
    		searchContent.append(title);
    		
    		StringBuilder sbSummary = new StringBuilder();
    		
    		if (supp.getSections() != null) {
    			Iterator<Section> itSections = supp.getSections().iterator();
    			while (itSections.hasNext()) {
    				Section section = itSections.next();
    				sbSummary.append(section.getName()).append(": ");
    				sbSummary.append(section.getCalendarEntries().size()).append(" bill(s); ");
    			}
    		}
    		
    		if (supp.getSequence() != null) {
    			if (supp.getSequence().getNotes()!=null)
    				sbSummary.append(supp.getSequence().getNotes());
    			
    			sbSummary.append(" ").append(supp.getSequence().getCalendarEntries().size()).append(" bill(s)");
    			
    			if (supp.getSequence().getActCalDate()!=null)
        			fields.put("when", supp.getSequence().getActCalDate().getTime()+"");	
    		}
    		
    		summary = sbSummary.toString().trim();
    	}
    	else if (obj instanceof Agenda) {
    		Agenda agenda = (Agenda)obj;

    		if (agenda.getAddendums() != null) {
	    		Iterator<Addendum> itAnd = agenda.getAddendums().iterator();
	    		
	    		while (itAnd.hasNext()) {
	    			Iterator<Meeting> itMeetings = itAnd.next().getMeetings().iterator();
	    			while (itMeetings.hasNext()) {
	    				indexSenateObject (itMeetings.next(), indexWriter, false, pm);
	    			}	
	    		}
    		}
    	}
    	else if (obj instanceof Meeting)
    	{
    		Meeting meeting = (Meeting)obj;
    		type = "meeting";
    		id = meeting.getId();
    		
    		searchContent.append(meeting.getCommitteeName()).append(" - ");
    		searchContent.append(meeting.getCommitteeChair()).append(" - ");
    		searchContent.append(meeting.getLocation()).append(" - ");
    		searchContent.append(meeting.getNotes());
    		
    		fields.put("committee",meeting.getCommitteeName());
    		fields.put("chair",meeting.getCommitteeChair());
    		fields.put("location",meeting.getLocation());
    		fields.put("notes", meeting.getNotes());
    		fields.put("when", meeting.getMeetingDateTime().getTime() + "");
    		
    		DateFormat df = java.text.DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    		
    		title = meeting.getCommitteeName() + " - " + df.format(meeting.getMeetingDateTime());
    		summary =  meeting.getLocation();
    	}
    	else if (obj instanceof Transcript)
    	{
    		Transcript transcript = (Transcript)obj;
    		type = "transcript";
    		id = transcript.getId();
    		
    		searchContent.append(transcript.getTranscriptText());
    		
    		fields.put("full", transcript.getTranscriptText());
    		
    		if (transcript.getTimeStamp()!=null)
    			fields.put("when",transcript.getTimeStamp().getTime()+"");
    		else
    			fields.put("when","");
    		
    		fields.put("location",transcript.getLocation());
    		fields.put("session-type",transcript.getType());
    	
    		DateFormat df = java.text.DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    		title = transcript.getType();
    		
    		if (transcript.getTimeStamp()!=null)
    			title += " - " + df.format(transcript.getTimeStamp());
    		
    		summary = transcript.getLocation();
    	}
    	else if (obj instanceof Vote)
    	{
    		Vote vote = (Vote)obj;
    		type = "vote";
    		id = vote.getId();
    		
    		Bill bill = vote.getBill();
    		
    		title = bill.getSenateBillNo() + " - " + DATE_FORMAT_MEDIUM.format(vote.getVoteDate());
    		
    		searchContent.append(bill.getSenateBillNo());
    		searchContent.append(" ");
    		
    		fields.put("billno", bill.getSenateBillNo());
    		
    		switch(vote.getVoteType()) {
    			case Vote.VOTE_TYPE_COMMITTEE:
    				
    				title += " - Committee Vote";
    				
    				if (vote.getDescription()!=null)
    					fields.put("committee", vote.getDescription());
    				else
    					fields.put("committee", bill.getCurrentCommittee());
    				
    				searchContent.append(" Committee Vote ");
    				searchContent.append(bill.getCurrentCommittee());
    				break;
    			case Vote.VOTE_TYPE_FLOOR:
    				title += " - Floor Vote";
    				searchContent.append(" Floor Vote ");
    				break;
			}
    		
    		Iterator<String> itVote = null;
    		StringBuilder sbVotes = null;
    		
    		if (vote.getAbstains()!=null) {
	    		sbVotes = new StringBuilder();
	    		itVote = vote.getAbstains().iterator();
	    		while (itVote.hasNext()) {
	    			sbVotes.append(itVote.next()).append(" ");
	    		}
	    		
	    		fields.put("abstain", sbVotes.toString());
    		}
    		
    		if (vote.getAyes()!=null) {
	    		sbVotes = new StringBuilder();
	    		itVote = vote.getAyes().iterator();
	    		while (itVote.hasNext()) {
	    			sbVotes.append(itVote.next()).append(" ");
	    		}
	    		
	    		fields.put("aye", sbVotes.toString());
    		}
    		
    		if (vote.getExcused()!=null) {
	    		
	    		sbVotes = new StringBuilder();
	    		itVote = vote.getExcused().iterator();
	    		while (itVote.hasNext()) {
	    			sbVotes.append(itVote.next()).append(" ");
	    		}
	    		
	    		fields.put("excused", sbVotes.toString());
    		}
    		
    		if (vote.getNays()!=null) {
	    		
	    		sbVotes = new StringBuilder();
	    		itVote = vote.getNays().iterator();
	    		while (itVote.hasNext()) {
	    			sbVotes.append(itVote.next()).append(" ");
	    		}
	    		
	    		fields.put("nay", sbVotes.toString());
    		} 
    		
    		summary = DATE_FORMAT_MEDIUM.format(vote.getVoteDate());
    		fields.put("when", vote.getVoteDate().getTime()+"");
    	}
    	else if (obj instanceof BillEvent) {

        	BillEvent billEvent = (BillEvent)obj;
    		type = "action";
    		id = billEvent.getBillEventId();
    		title = billEvent.getEventText();
    		summary = DATE_FORMAT_MEDIUM.format(billEvent.getEventDate());

    		Bill bill = PMF.getBill(pm,billEvent.getBillId(),billEvent.getEventDate().getYear());
    		
    		if (bill.getTitle()!=null) {
    			summary += " - " + bill.getTitle();
    		}
    		
    		indexBillEvent(bill, billEvent, searchContent, fields);
    	}
    	
    	if (summary != null)
    		fields.put("summary", summary);
    	
    	if (type != null) {
    		addDocument (type, id, title, searchContent.toString(), fields, indexWriter,obj);
    	}
    	
    	if (closeIndex) {
    		indexWriter.close();
    	}
    	
    	return true;
    }
    
    public  static boolean deleteSenateObject (Object obj,  PersistenceManager pm) throws Exception
    {
    	IndexWriter indexWriter;
		Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        indexWriter = new IndexWriter(fsDirectory, analyzer, true, MaxFieldLength.UNLIMITED);
        indexWriter.setMaxBufferedDeleteTerms(0);
	
    	String type = null;
    	String id = null;
    	 
    	if (obj instanceof Bill) {
    		type = "bill";
            id = ((Bill)obj).getSenateBillNo();
    	}
    	else if (obj instanceof Supplemental) {
    		type = "calendar";
    		id = ((Supplemental)obj).getCalendar().getId();
    	}
    	else if (obj instanceof Agenda) {
    		Agenda agenda = (Agenda)obj;
    		
    		if (agenda.getAddendums() != null) {
	    		Iterator<Addendum> itAnd = agenda.getAddendums().iterator();
	    		while (itAnd.hasNext()) {
	    			Iterator<Meeting> itMeetings = itAnd.next().getMeetings().iterator();
	    			while (itMeetings.hasNext()) {
	    				Meeting meeting = itMeetings.next();
	    				deleteSenateObject (meeting, pm);
	    			}
    			}
    		}
    	}
    	else if (obj instanceof Meeting) { 
    		type = "meeting";
    		id = ((Meeting)obj).getId();
		}
    	else if (obj instanceof Transcript) {
    		type = "transcript";
    		id = ((Transcript)obj).getId();
		}
    	else if (obj instanceof Vote) {
    		type = "vote";
    		id = ((Vote)obj).getId();
		}
    	else if (obj instanceof BillEvent) {
    		type = "action";
    		id = ((BillEvent)obj).getBillEventId();
    	}
    
    	if (type != null){
    		deleteDocument (type, id, indexWriter);
    	}
    	
    	indexWriter.close();
    	return true;
    }
    
    public  static boolean deleteSenateObjectById (String type, String id) throws Exception
    {
    	closeIndex();
    	
    	IndexWriter indexWriter;
		Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        indexWriter = new IndexWriter(fsDirectory, analyzer, false, MaxFieldLength.UNLIMITED);
        
       // indexWriter.setMaxBufferedDeleteTerms(1);
	
    	deleteDocument (type, id, indexWriter);
    
    	indexWriter.close();
    	
    	
    	openIndex();
    	
    	return true;
    }
    
    private static void indexBillEvent (Bill bill, BillEvent billEvent, StringBuilder searchContent, HashMap<String,String> fields)
    {
		fields.put("when", billEvent.getEventDate().getTime()+"");
		fields.put("billno", billEvent.getBillId() );
		
		searchContent.append(billEvent.getBillId()).append(" ");
		
		try
		{
			if (bill.getSponsor()!=null) {
    			searchContent.append(bill.getSponsor().getFullname()).append(" ");
    			fields.put("sponsor", bill.getSponsor().getFullname());
    		}
    		
            if (bill.getCoSponsors()!=null) {
            	StringBuilder cosponsor = new StringBuilder();
            	Iterator<Person> itCosp = bill.getCoSponsors().iterator();
            	
            	while (itCosp.hasNext()) {
            		cosponsor.append((itCosp.next()).getFullname());
            		
            		if (itCosp.hasNext())
            			cosponsor.append(", ");
            	}
            	
            	fields.put("cosponsors", cosponsor.toString());
            }
		}
		catch (Exception e)
		{
			logger.warn("couldn't get bill from BillEvent:" + billEvent.getBillEventId(),e);
		}
		
		searchContent.append(billEvent.getEventText());
    }
    /**
     * Add one document to the Lucene index
     */
    public  static void addDocument(String otype, String oid, String title, String searchString, HashMap<String,String> fields, IndexWriter indexWriter, Object o) throws IOException
    {
		Document document = new Document();
 
        oid = oid.replace(" ","+"); //need to remove spaces from id's in order to have them properly work with Lucene
 
        logger.info("indexing document: " + otype + "=" + oid);

        if (title != null) {
        	document.add(new Field("title",title,Field.Store.YES,Field.Index.ANALYZED));
        }
        document.add(new Field("oid",oid,Field.Store.YES,Field.Index.ANALYZED));
        document.add(new Field("otype",otype,Field.Store.YES,Field.Index.ANALYZED));
        document.add(new Field("osearch",searchString,Field.Store.YES,Field.Index.ANALYZED));
        document.add(new Field("title_sortby", title.replaceAll(" ", ""),Field.Store.NO, Field.Index.NOT_ANALYZED));

       try {
    	   if(otype.matches("bill|transcript|calendar|meeting")) {
           	document.add(new Field("oxml", OriginalApiConverter.doXml(o),Field.Store.YES,Field.Index.ANALYZED));
           	document.add(new Field("ojson", OriginalApiConverter.doJson(o),Field.Store.YES,Field.Index.ANALYZED));
           }
       }
       catch(Exception e) {
    	   e.printStackTrace();
       }
        
        String modified = new java.util.Date().getTime() + "";
        document.add(new Field("modified",modified,Field.Store.YES,Field.Index.ANALYZED));
        
        Iterator<Map.Entry<String, String>> itFields = fields.entrySet().iterator();
        
        while (itFields.hasNext()) {
        	Map.Entry<String,String> field = itFields.next();
        	
        	String val = field.getValue();
        	if (val != null && val.length() > 0) {
	        	Field.Store store = Field.Store.YES;
	        	
	        	try {
	        	 document.add(new Field(field.getKey(),field.getValue(),store,Field.Index.ANALYZED));
	        	}
	        	catch (Exception e) {
	        		logger.warn("error indexing document: " + otype + "=" + oid + "; field=" + field.getKey());
	        	}
        	}
        }
        
        try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, "oid", indexWriter.getAnalyzer()).parse("oid:" + oid);

            indexWriter.deleteDocuments(query);
            indexWriter.addDocument(document);
        } catch (Exception e) {
        	logger.warn("error adding document to index: " + otype + "=" + oid, e);
          }
        
    }
    
    /**
     * Add one document to the Lucene index
     */
    public  static void deleteDocument(String otype, String oid, IndexWriter indexWriter) throws IOException
    {        
		logger.info("deleting document: " + otype + "=" + oid);

        try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, "oid", indexWriter.getAnalyzer()).parse("otype:" + otype + " AND oid:" + oid);
            indexWriter.deleteDocuments(query);
	    }
        catch (Exception e) {
			logger.warn("error deleting document to index: " + otype + "=" + oid, e);
        }
    }
    
    /**
     * Add one document to the Lucene index
     */
    public static void deleteAllDocumentByType(String otype) throws IOException
    {
    	Directory fsDirectory = FSDirectory.open(new File(indexDir));
        
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, analyzer, false, MaxFieldLength.UNLIMITED);
       
		logger.info("deleting all document: " + otype);

        try {
            Query query = new QueryParser(Version.LUCENE_CURRENT, "otype", indexWriter.getAnalyzer()).parse("otype:" + otype);
            indexWriter.deleteDocuments(query);
		} 
		catch (Exception e) {
			logger.warn("error deleting document to index: " + otype, e);
		}
    	indexWriter.close();
    }
    
    public static SearchResultSet doSearch (String searchText, int start, int max, String sortField, boolean sortOrder) throws IOException, ParseException
	{
    	if(!searchText.contains("oid") || !searchText.contains("otype")) {
    		if(searchText.matches(BillCleaner.BILL_SEARCH_REGEXP)) {
    			
    			searchText = BillCleaner.billFormat(searchText);
    			searchText = "oid:" + searchText + "~";
    		}
    	}
    	
    	IndexSearcher searcher = openIndex();

        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        Query query = new QueryParser(Version.LUCENE_CURRENT, "osearch", analyzer).parse(searchText);
        
        return doPagingSearch (searcher, query, start, max, sortField, sortOrder);
    }
    
    /**
     * judge if the index exists already
     */
    public static boolean ifIndexExist(){
        File directory = new File(indexDir);
        if(0 < directory.listFiles().length){
            return true;
        } 
        else{
            return false;
        }
    }
    
    public static SearchResultSet doPagingSearch(Searcher searcher, Query query, int start, int numberOfResults, String sortField, boolean reverseSort ) throws IOException
    {
    	 SearchResultSet srs = null;
    	 
    	try {
	      // Collect enough docs to show 5 pages
	      TopScoreDocCollector collector = TopScoreDocCollector.create(numberOfResults, false);
	      searcher.search(query, collector);
	      ScoreDoc[] hits = collector.topDocs().scoreDocs;
	      
	      int numTotalHits = collector.getTotalHits();
	      
	      logger.info(numTotalHits + " total matching documents (" + query.toString() + ")");
	      
	      collector = TopScoreDocCollector.create(numTotalHits, false);
	      
	      Sort sort = null;
	      
	      if (sortField != null) {
	    	  try {
	    		  sort = new Sort(new SortField(sortField, SortField.STRING, reverseSort));
		    	  Filter filter = null;
		    	  
		    	  hits = searcher.search(query, filter, start + numberOfResults, sort).scoreDocs;
	    	  }
	    	  catch (Exception e) {
	    		  e.printStackTrace();
	    	  }
	      }
	      else {
	    	  searcher.search(query, collector);
	    	  hits = collector.topDocs().scoreDocs;
	      }
	    
	
	      srs = new SearchResultSet();
	      
	      srs.totalHitCount = numTotalHits;
	      srs.results = new ArrayList<SearchResult>();
	      	      	           
	      for (int i = start;(i < hits.length && i < start + numberOfResults); i++) {
	    	  
	    	  Document doc = searcher.doc(hits[i].doc);
	
	    	  SearchResult sr = new SearchResult();
	    	  sr.type = doc.get("otype");
	    	  sr.id = doc.get("oid");
	    	  sr.title = doc.get("title");
	    	  sr.title_sortby = doc.get("title_sortby");
	    	  sr.summary = doc.get("summary");
	    	  
	    	  sr.xml = doc.get("oxml");
	    	  sr.json = doc.get("ojson");
	    	  
	    	  sr.score = hits[i].score;
	    	  sr.fields = new HashMap<String,String>(); 
	    	 
	    	  if (doc.get("modified")!=null)
	    		  sr.lastModified = new Date(Long.parseLong(doc.get("modified")));
	    	  
	    	  Iterator<Fieldable> itFields = doc.getFields().iterator();
	    	  while (itFields.hasNext()) {
	    		  Field field = (Field)itFields.next();
	    		  sr.fields.put(field.name(), field.stringValue());
	    	  }
	    	  
	    	  srs.results.add(sr);
	      }
	      
    	}
    	catch (Exception e) {
    		logger.warn("Search Exception: " + query.toString(),e);
    	}
    	
	    return srs;
    }
    
	public static void main (String[] args) throws Exception	
	{		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		String line = null;
		int start = 1;
		int max = 10;
		
		System.out.print("openleg search > ");
		while (!(line = reader.readLine()).equals("quit"))
		{
			if (line.startsWith("index "))
				indexSenateData(line.substring(line.indexOf(" ")+1));
			else if (line.startsWith("optimize"))
				optimizeIndex();
			else if (line.startsWith("delete"))
			{
				StringTokenizer cmd = new StringTokenizer(line.substring(line.indexOf(" ")+1)," ");
				String type = cmd.nextToken();
				String id = cmd.nextToken();
				
				deleteSenateObjectById(type, id);
			}
			else if (line.startsWith("create"))
				createIndex();
			else
				doSearch(line,start, max, null, false);
			
			System.out.print("openleg search > ");
		}
		System.out.println("Exiting Search Engine");
		
	}
	
	public static void indexSenateData(String type) throws Exception
	{		
		if (type.equals("transcripts") || type.equals("*"))	{
			doIndex("transcript", Transcript.class, null, 25);
		}
		
		if (type.equals("meetings") || type.equals("*"))	{
			doIndex("meeting", Meeting.class, null, 10);
		}
		
		if (type.equals("calendars") || type.equals("*"))	{
			doIndex("calendar", Calendar.class, null, 25);
		}
		
		if (type.equals("bills") || type.equals("*"))	{
			doIndex("bill", Bill.class, SORTINDEX_DESCENDING, 25);
		}
		
		if (type.equals("billevents") || type.equals("*"))	{
			doIndex("billevent", BillEvent.class, null, 25);
		}
		
		if (type.equals("votes") || type.equals("*"))	{
			doIndex("vote", Vote.class, null, 25);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void doIndex(String type, Class objClass, String sort, int pageSize) throws IOException {	
		int start = 1;
		int end = start+pageSize;
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Collection<Object> result = null;
		
		deleteAllDocumentByType(type);
		
		do {			
			System.out.println(type + " : " + start);
			result = (Collection<Object>) PMF.getDetachedObjects(objClass, sort, start, end);
			indexSenateObjects(result, pm);
			start += pageSize;
			end = start+pageSize;
		}
		while (result.size() == pageSize);
		
	}
	
	
	public static String getApiV1Search(String codeType, String otype, String oid, String sortField, int start, int numberOfResults, boolean reverseSort) {
		try {
			SearchResultSet srs = doSearch(
					((otype != null) ? "otype:" + otype : "") +
					((oid != null) ? (
							(otype!=null) ? " AND oid:" : "")+ oid : ""),
					start, numberOfResults, sortField, reverseSort);
			
			ArrayList<SearchResult> lst = srs.getResults();
			
			if(!lst.isEmpty()) {
				return (codeType.equals("xml") ? lst.iterator().next().xml: lst.iterator().next().json);
			}
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
    }
}