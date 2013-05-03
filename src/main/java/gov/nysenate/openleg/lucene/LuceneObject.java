package gov.nysenate.openleg.lucene;

import java.util.HashMap;

import org.apache.lucene.document.Fieldable;

public class LuceneObject implements ILuceneObject {

	private boolean active = true;
	private long modified = 0;

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
	public boolean getActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public long getModified() {
		return modified;
	}

	@Override
	public void setModified(long modified) {
		this.modified = modified;
	}

}
