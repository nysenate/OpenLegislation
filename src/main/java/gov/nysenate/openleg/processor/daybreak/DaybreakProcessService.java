package gov.nysenate.openleg.processor.daybreak;

import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakBillId;
import gov.nysenate.openleg.model.spotcheck.daybreak.DaybreakFragment;
import gov.nysenate.openleg.processor.base.ProcessService;

import java.util.List;

/**
 * This service is responsible for the parsing of daybreak files into daybreak bills for use in the spot check
 * During the collate step, the daybreak files are parsed into stored daybreak fragments and archived.
 * In the process step, further parsing is done on daybreak fragments producing daybreak bills, which are subsequently stored
 */
public interface DaybreakProcessService extends ProcessService
{
    /**
     * Identifies daybreak files in the incoming daybreak directory, groups them into sets, and parses complete
     * sets into DaybreakFragments and PageFileEntries.  The files are then stored in the archive directory
     * and the DaybreakFragments are placed in the store as pending processing.
     *
     * @return int - the number of collated daybreak sets
     */
    public int collateDaybreakReports();

    /**
     * Retrieves DaybreakFragments that are pending processing.
     *
     * @return List<DaybreakFragment>
     */
    public List<DaybreakFragment> getPendingDaybreakFragments();

    /**
     * Parses the given DaybreakFragments and stores them as DaybreakBills
     *
     * @param fragments List<DaybreakFragment>
     * @return the number of processed fragments
     */
    public int processFragments(List<DaybreakFragment> fragments);

    /**
     * Retrieves and processes all DaybreakFragments marked as pending processing
     * @return the number of processd fragments
     */
    public int processPendingFragments();

    /**
     * Sets the pending processing flag for the given fragment id in the store to the given boolean
     *
     * @param fragmentId DaybreakBillId
     * @param pendingProcessing boolean
     */
    public void updatePendingProcessing(DaybreakBillId fragmentId, boolean pendingProcessing);
}