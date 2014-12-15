package gov.nysenate.openleg.dao.process;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Layer for persisting details about each run of the data processor which is useful for
 * knowing what data has been processed.
 */
public interface DataProcessLogDao
{
    /**
     * Returns a list of DataProcessRun instances that began within the given date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @param withActivityOnly boolean - Set to true to only return runs that have units associated with them.
     * @param dateOrder SortOrder - Order the results by start date
     * @param limOff LimitOffset
     * @return List<DataProcessRun>
     */
    public PaginatedList<DataProcessRun> getRuns(Range<LocalDateTime> dateTimeRange, boolean withActivityOnly,
                                        SortOrder dateOrder, LimitOffset limOff);

    /**
     * Insert a run into the persistence layer.
     *
     * @param run DataProcessRun
     */
    public void insertRun(DataProcessRun run);

    /**
     * Insert a unit into the persistence layer.
     *
     * @param processId int - DataProcessRun id to associate this unit with
     * @param unit DataProcessUnit
     */
    public void insertUnit(int processId, DataProcessUnit unit);

    /**
     * Updates the run.
     *
     * @param run DataProcessRun
     */
    public void updateRun(DataProcessRun run);
}
