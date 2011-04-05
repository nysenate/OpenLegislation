package gov.nysenate.openleg.lucene;

import java.util.HashMap;
import org.apache.lucene.document.Fieldable;

public interface ILuceneObject {
	public String luceneOid();
	public String luceneOsearch();
	public String luceneOtype();
	public String luceneSummary();
	public String luceneTitle();
	public HashMap<String, Fieldable> luceneFields();
	public void setLuceneActive(boolean active);
	public boolean getLuceneActive();
	public void setLuceneModified(long modified);
	public long getLuceneModified();
}