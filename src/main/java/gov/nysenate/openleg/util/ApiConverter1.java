package gov.nysenate.openleg.util;

import gov.nysenate.openleg.lucene.LuceneSerializer;

public class ApiConverter1 implements LuceneSerializer {

	public String toJson(Object o) {
		return OriginalApiConverter.doJson(o);
	}

	public String toXml(Object o) {
		return OriginalApiConverter.doXml(o);
	}

}
