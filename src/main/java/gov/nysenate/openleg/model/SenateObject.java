package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.LuceneObject;

import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

public class SenateObject extends LuceneObject implements ISenateObject {
    HashSet<String> sobiReferenceList = new HashSet<String>();

    @Override
    @XmlTransient
    public int getYear() {
        return 0;
    }

    @Override
    public void setYear(int year) {

    }

    @Override
    public void merge(ISenateObject obj) {
        this.sobiReferenceList.addAll(obj.getSobiReferenceList());
    }

    @Override
    public HashMap<String, Fieldable> luceneFields() {
        return null;
    }

    @Override
    public String luceneOid() {
        return null;
    }

    @Override
    public String luceneOsearch() {
        return null;
    }

    @Override
    public String luceneOtype() {
        return null;
    }

    @Override
    public String luceneSummary() {
        return null;
    }

    @Override
    public String luceneTitle() {
        return null;
    }

    @Override
    public HashSet<String> getSobiReferenceList() {
        return sobiReferenceList;
    }

    @Override
    public void setSobiReferenceList(HashSet<String> sobiReferenceList) {
        this.sobiReferenceList = sobiReferenceList;
    }

    @Override
    public void addSobiReference(String reference) {
        sobiReferenceList.add(reference);
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public String fileSystemId() {
        return null;
    }
}
