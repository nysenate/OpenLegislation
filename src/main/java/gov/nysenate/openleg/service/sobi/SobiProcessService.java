package gov.nysenate.openleg.service.sobi;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SobiProcessService
{
    /**
     * Looks for sobi files that have been placed in the incoming directory and
     * parses them out into SobiFragments. The sobi files are then placed into an
     * archive directory and the SobiFragments are recorded in the backing store
     * as pending processing.
     *
     * @return int - The number of sobi files that have been collated.
     */
    public int collateSobiFiles();

    /**
     * Retrieves the SobiFragments that are awaiting processing.
     *
     * @param sortByPubDate SortOrder - Sort order for the fragment id.
     * @param limitOffset LimitOffset - Restrict the results list.
     * @return List<SobiFragment>
     */
    public List<SobiFragment> getPendingFragments(SortOrder sortByPubDate, LimitOffset limitOffset);

    /**
     * Retrieves the pending fragments are processes them.
     *
     * @return int - The number of fragments that have been processed.
     */
    public int processPendingFragments();
}
