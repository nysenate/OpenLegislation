package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.ILuceneObject;

import java.util.HashSet;

public interface ISenateObject extends ILuceneObject {
    public void merge(ISenateObject obj);
    public int getYear();
    public void setYear(int year);
    public HashSet<String> getSobiReferenceList();
    public void setSobiReferenceList(HashSet<String> sobiReferenceList);
    public void addSobiReference(String reference);
    public String fileSystemId();
}
