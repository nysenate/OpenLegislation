package gov.nysenate.openleg.util;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializerType;

public class JsonSerializer implements LuceneSerializerType {

	@Override
	public String getData(LuceneObject o) {
		return JsonConverter.getJson(o).toString();
	}

	@Override
	public String getType() {
		return "ojson";
	}

}
