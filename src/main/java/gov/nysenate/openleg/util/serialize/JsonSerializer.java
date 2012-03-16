package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;

public class JsonSerializer implements LuceneSerializer {

    @Override
    public String getData(ILuceneObject o) {
        return JsonConverter.getJson(o).toString();
    }

    @Override
    public String getType() {
        return "ojson";
    }

}
