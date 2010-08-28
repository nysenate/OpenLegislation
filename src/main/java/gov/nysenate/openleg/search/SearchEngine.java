package gov.nysenate.openleg.search;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.lucene.Lucene;
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
import gov.nysenate.openleg.util.JsonConverter;
import gov.nysenate.openleg.util.OriginalApiConverter;
import gov.nysenate.openleg.xstream.XStreamBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public abstract class SearchEngine extends Lucene implements OpenLegConstants {
	
	protected DateFormat DATE_FORMAT_MEDIUM = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
    
    public void deleteSenateObject (Object obj) throws Exception
    {
    	if (obj instanceof Agenda) {
    		Agenda agenda = (Agenda)obj;
    		if (agenda.getAddendums() != null)
	    		for( Addendum addendum : agenda.getAddendums() ) 
	    			for( Meeting meeting : addendum.getMeetings() ) {
	    				deleteSenateObject( meeting );
	    			}
    	}
    	else if (obj instanceof Bill) {
            deleteSenateObjectById("bill",((Bill)obj).getSenateBillNo());
    	}
    	else if (obj instanceof Supplemental) {
    		deleteSenateObjectById("calendar",((Supplemental)obj).getCalendar().getId());
    	}
    	else if (obj instanceof Meeting) { 
    		deleteSenateObjectById("meeting",((Meeting)obj).getId());
		}
    	else if (obj instanceof Transcript) {
    		deleteSenateObjectById("transcript",((Transcript)obj).getId());
		}
    	else if (obj instanceof Vote) {
    		deleteSenateObjectById("vote",((Vote)obj).getId());
		}
    	else if (obj instanceof BillEvent) {
    		deleteSenateObjectById("action",((BillEvent)obj).getBillEventId());
    	}
    }
    
    public void deleteSenateObjectById (String type, String id) throws Exception {
    	closeIndex();
    	deleteDocuments(type, id);
    	openIndex();
    }
    
    private void indexBillEvent (Bill bill, BillEvent billEvent, StringBuilder searchContent, HashMap<String,String> fields)
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
	
	public void indexSenateData(String type) throws Exception
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
	public void doIndex(String type, Class objClass, String sort, int pageSize) throws IOException {	
		int start = 1;
		int end = start+pageSize;
		
		PersistenceManager pm = PMF.getPersistenceManager();
		Collection<?> result = null;
		
		deleteDocuments(type,null);
		
		do {			
			System.out.println(type + " : " + start);
			result = PMF.getDetachedObjects(objClass, sort, start, end);
			indexSenateObjects(result, pm);
			start += pageSize;
			end = start+pageSize;
		}
		while (result.size() == pageSize);
		
	}	
	
    public  boolean indexSenateObjects (Collection<?> objects, PersistenceManager pm) throws IOException
    {
    	createIndex ();
    	
    	boolean overwrite = false;
    	
        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
        IndexWriter indexWriter = new IndexWriter(getDirectory(), analyzer, overwrite, MaxFieldLength.UNLIMITED);
       
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
    	    			indexSenateObject(supp, indexWriter, pm);
    	    		}
    	    		catch (Exception e) {
    	    			logger.warn("unable to index senate supp",e);
    	    		}
    			}
    		}
    		else {
	    		try {
	    			indexSenateObject(obj, indexWriter, pm);
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
    
    public boolean indexSenateObject (Object obj, IndexWriter indexWriter, PersistenceManager pm) throws Exception
    {
    	if (indexWriter == null) {
	    	createIndex ();
	    	
	        Analyzer  analyzer    = new StandardAnalyzer(Version.LUCENE_CURRENT);
	        indexWriter = new IndexWriter(getDirectory(), analyzer, true, MaxFieldLength.UNLIMITED);
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
	    				indexSenateObject (itMeetings.next(), indexWriter, pm);
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

    		Bill bill = PMF.getBill(pm,billEvent.getBillId());
    		
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
    	
    	return true;
    }
	
    public void addDocument(String otype, String oid, String title, String searchString, HashMap<String,String> fields, IndexWriter indexWriter, Object o) throws IOException
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
           	document.add(new Field("oxml_new",XStreamBuilder.xml(o),Field.Store.YES,Field.Index.ANALYZED));
            document.add(new Field("ojson_new",JsonConverter.getJson(o).toString(),Field.Store.YES,Field.Index.ANALYZED));
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
	
}