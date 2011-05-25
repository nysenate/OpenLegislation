package gov.nysenate.openleg.model;

import java.util.HashSet;

import gov.nysenate.openleg.lucene.ILuceneObject;

public interface ISenateObject extends ILuceneObject {
	public void merge(ISenateObject obj);
	public int getYear();
	public HashSet<String> getSobiReferenceList();
	public void setSobiReferenceList(HashSet<String> sobiReferenceList);
	public void addSobiReference(String reference);
}
