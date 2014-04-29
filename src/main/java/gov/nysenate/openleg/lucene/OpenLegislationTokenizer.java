package gov.nysenate.openleg.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

public class OpenLegislationTokenizer extends CharTokenizer
{
    public OpenLegislationTokenizer(Version matchVersion, Reader in) {
        super(matchVersion, in);
    }

    public OpenLegislationTokenizer(Version matchVersion, AttributeFactory factory, Reader in) {
        super(matchVersion, factory, in);
    }

    @Override
    protected boolean isTokenChar(int c)
    {
        return !Character.isWhitespace(c)
               && c != ','
               && c != '.'
               && c != '"'
               && c != '\''
               && c != ';'
               && c != '`';
    }

}
