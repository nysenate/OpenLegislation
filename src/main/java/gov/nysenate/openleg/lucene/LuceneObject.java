package gov.nysenate.openleg.lucene;

import java.util.HashMap;
import org.apache.lucene.document.Field;

public interface LuceneObject {
	public String luceneOid();
	public String luceneOsearch();
	public String luceneOtype();
	public String luceneSummary();
	public String luceneTitle();
	public HashMap<String,Field> luceneFields();
}