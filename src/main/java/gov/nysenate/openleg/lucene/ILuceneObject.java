package gov.nysenate.openleg.lucene;

import java.util.HashMap;
import org.apache.lucene.document.Field;

public interface ILuceneObject {
	public String luceneOid();
	public String luceneOsearch();
	public String luceneOtype();
	public String luceneSummary();
	public String luceneTitle();
	public HashMap<String,Field> luceneFields();
	public void setLuceneActive(boolean active);
	public boolean getLuceneActive();
	public void setLuceneModified(long modified);
	public long getLuceneModified();
}