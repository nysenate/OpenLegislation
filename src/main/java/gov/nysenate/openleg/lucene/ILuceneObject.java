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
	public void setActive(boolean active);
	public boolean getActive();
	public void setModified(long modified);
	public long getModified();
}