package gov.nysenate.openleg.lucenemodel;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.document.Field;

import gov.nysenate.openleg.abstractmodel.AbstractBill;
import gov.nysenate.openleg.lucene.LuceneObject;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Person;

public abstract class LuceneBill extends AbstractBill implements LuceneObject {
	
	public String lucene_otype() { return "bill"; }
	
	public String lucene_oid() { return senateBillNo+"-"+year; }
	
	public String lucene_osearch() { return "osearch here..."; }
	
	public java.lang.reflect.Field[] lucene_fields() { return AbstractBill.class.getDeclaredFields(); }
	
	public String lucene_cosponsors() {
		StringBuilder response = new StringBuilder();
		for( Person sponsor: coSponsors) {
			response.append(sponsor.getId());
		}
		return response.toString();
	}
	
	public String lucene_ammendments() {
		StringBuilder response = new StringBuilder();
		for(Bill amendment: amendments) {
			response.append(amendment.getSenateBillNo()).append(" ");
		}
		return response.toString();
	}
	
	public Collection<Field> getLuceneFields() {
		return new ArrayList<Field>();
	}
	
}
