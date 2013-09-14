package gov.nysenate.openleg.lucene;

import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;

/**
 * Used to load document field lazily and prevent unnecessary IO when
 * retrieving documents from Lucene queries. This causes the SearcherManager
 * to hold Searchers open for longer than it otherwise might have.
 *
 * @author GraylinKim
 *
 */
@SuppressWarnings("serial")
public class LazyFieldSelector implements FieldSelector
{
    /**
     * Accepts a field name for a document.
     *
     * @return the selector to use for the given field.
     */
    public FieldSelectorResult accept(String field) {
        return FieldSelectorResult.LAZY_LOAD;
    }
}
