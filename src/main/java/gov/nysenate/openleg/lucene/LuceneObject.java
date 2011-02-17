package gov.nysenate.openleg.lucene;

import java.util.HashMap;

import org.apache.lucene.document.Field;

public class LuceneObject implements ILuceneObject {
	private boolean active = true;
	
	@Override
	public boolean getLuceneActive() {
		return active;
	}

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
	public void setLuceneActive(boolean active) {
		this.active = active;
	}

}
