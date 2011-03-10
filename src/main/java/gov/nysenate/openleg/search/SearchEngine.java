package gov.nysenate.openleg.search;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.lucene.Lucene;
import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;

public abstract class SearchEngine extends Lucene implements OpenLegConstants {
	
	public SearchEngine(String indexDir) {
		super(indexDir);
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
    	
    	logger.info("done indexing objects(" + objects.size() + "). Closing index.");
    	indexWriter.close();
    	return true;
    }
	
}