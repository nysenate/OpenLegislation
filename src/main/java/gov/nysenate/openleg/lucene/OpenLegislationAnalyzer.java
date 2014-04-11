package gov.nysenate.openleg.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.util.Version;

public class OpenLegislationAnalyzer extends Analyzer
{
    private final Version matchVersion;

    public OpenLegislationAnalyzer(Version matchVersion)
    {
        this.matchVersion = matchVersion;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader)
    {
        Tokenizer source = new OpenLegislationTokenizer(matchVersion, reader);
        TokenFilter filter = new StopFilter(matchVersion, source, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        filter = new LowerCaseFilter(matchVersion, filter);
        return new TokenStreamComponents(source, filter);
    }
}
