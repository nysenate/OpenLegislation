package gov.nysenate.openleg.processor.daybreak;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.daybreak.DaybreakFragment;
import gov.nysenate.openleg.model.daybreak.DaybreakFragmentId;

import java.util.List;

/**
 * This service is responsible for the parsing of daybreak files into daybreak bills for use in the spot check
 * During the collate step, the daybreak files are parsed into stored daybreak fragments and archived.
 * In the process step, further parsing is done on daybreak fragments producing daybreak bills, which are subsequently stored
 */
public interface DaybreakProcessService {

    /**
     * Identifies daybreak files in the incoming daybreak directory, groups them into sets, and parses complete
     * sets into DaybreakFragments and PageFileEntries.  The files are then stored in the archive directory
     * and the DaybreakFragments are placed in the store as pending processing.
     *
     * @return int - the number of collated daybreak sets
     */
    public int collateDaybreakFiles();

    /**
     * Retrieves DaybreakFragments that are pending processing.
     *
     * @param limitOffset - Specifies a range of fragments to prevent loading all of them to memory
     * @return
     */
    public List<DaybreakFragment> getPendingDaybreakFragments(LimitOffset limitOffset);

    /**
     * Parses the given DaybreakFragments and stores them as DaybreakBills
     *
     * @param fragments
     */
    public void processFragments(List<DaybreakFragment> fragments);

    /**
     * Retrieves and processes all DaybreakFragments marked as pending processing
     */
    public void processPendingFragments();

    /**
     * Sets the pending processing flag for the given fragment id in the store
     * @param fragmentId
     */
    public void updatePendingProcessing(DaybreakFragmentId fragmentId);
}
