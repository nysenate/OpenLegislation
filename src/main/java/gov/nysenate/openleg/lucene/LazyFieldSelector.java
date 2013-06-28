package gov.nysenate.openleg.lucene;

import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;

@SuppressWarnings("serial")
public class LazyFieldSelector implements FieldSelector
{
    public FieldSelectorResult accept(String field) {
        return FieldSelectorResult.LAZY_LOAD;
    }
}
