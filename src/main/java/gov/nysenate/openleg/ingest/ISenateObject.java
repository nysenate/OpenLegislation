package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.lucene.ILuceneObject;

public interface ISenateObject extends ILuceneObject {
	public void merge(ISenateObject obj);
	public int getYear();
}
