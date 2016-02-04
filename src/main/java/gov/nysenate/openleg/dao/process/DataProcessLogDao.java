package gov.nysenate.openleg.dao.process;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Layer for persisting details about each run of the data processor which is useful for
 * knowing what data has been processed.
 */
public interface DataProcessLogDao
{
    /**
     * Fetch the process run with the given id.
     *
     * @param processId int
     * @return DataProcessRun
     */
    public DataProcessRun getRun(int processId) throws DataAccessException;

    /**
     * Returns a list of DataProcessRun instances that began within the given date/time range.
     *
     * @param dateTimeRange Range<LocalDateTime>
     * @param withActivityOnly boolean - Set to true to only return runs that have units associated with them.
     * @param dateOrder SortOrder - Order the results by start date
     * @param limOff LimitOffset - Limit the result set
     * @return List<DataProcessRun>
     */
    public PaginatedList<DataProcessRun> getRuns(Range<LocalDateTime> dateTimeRange, boolean withActivityOnly,
                                        SortOrder dateOrder, LimitOffset limOff);

    /**
     * Returns a list of DataProcessUnit instances that are associated with the given process id.
     *
     * @param processId int - The id of the associated DataProcessRun
     * @param dateOrder SortOrder - Order the results by process date
     * @param limOff LimitOffset - Limit the result set
     * @return PaginatedList<DataProcessUnit>
     */
    public PaginatedList<DataProcessUnit> getUnits(int processId, SortOrder dateOrder, LimitOffset limOff);

    /**
     * Returns the first and last data process units for a given DataProcessRun.
     * @param processId - The id of the associated DataProcessRun
     * @return List<DataProcessUnit> - empty list if no units processed, single item if only one item processed,
     *                                 two items if >1 units processed where first item is first processed, second
     *                                 item is last processed.
     */
    public List<DataProcessUnit> getFirstAndLastUnits(int processId);

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
