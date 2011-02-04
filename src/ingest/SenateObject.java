package ingest;

import gov.nysenate.openleg.lucene.LuceneObject;

public interface SenateObject extends LuceneObject {
	public void merge(SenateObject obj);
	public int getYear();
}
