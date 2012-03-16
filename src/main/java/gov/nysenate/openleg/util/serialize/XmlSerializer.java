package gov.nysenate.openleg.util.serialize;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.xstream.XStreamBuilder;

public class XmlSerializer implements LuceneSerializer {

    @Override
    public String getData(ILuceneObject o) {
        return XStreamBuilder.xml(o);
    }

    @Override
    public String getType() {
        return "oxml";
    }
}
