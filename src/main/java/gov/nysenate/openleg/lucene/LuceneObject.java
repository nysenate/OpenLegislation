package gov.nysenate.openleg.lucene;

import java.util.HashMap;

import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;

public class LuceneObject implements ILuceneObject {
	private boolean active = true;
	private long modified;

	@Override
	public HashMap<String, Field> luceneFields() {
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
	public boolean getLuceneActive() {
		return active;
	}

	@Override
	@JsonIgnore
	public void setLuceneActive(boolean active) {
		this.active = active;
	}

	@Override
	@JsonIgnore
	public long getLuceneModified() {
		return modified;
	}

	@Override
	public void setLuceneModified(long modified) {
		this.modified = modified;
	}

}
