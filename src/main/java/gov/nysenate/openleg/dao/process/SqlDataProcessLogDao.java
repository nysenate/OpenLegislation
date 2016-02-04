package gov.nysenate.openleg.dao.process;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.process.DataProcessAction;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnit;
import org.apache.shiro.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.dao.process.SqlDataProcessLogQuery.*;
import static gov.nysenate.openleg.util.DateUtils.*;

@Repository
public class SqlDataProcessLogDao extends SqlBaseDao implements DataProcessLogDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlDataProcessLogDao.class);

    /** {@inheritDoc} */
    @Override
    public DataProcessRun getRun(int processId) throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource("processId", processId);
        return jdbcNamed.queryForObject(SELECT_DATA_PROCESS_RUN.getSql(schema()), params, processRunRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<DataProcessRun> getRuns(Range<LocalDateTime> dateTimeRange, boolean withActivityOnly,
                                        SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDateTime", toDate(startOfDateTimeRange(dateTimeRange)))
            .addValue("endDateTime", toDate(endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("process_start_date_time", dateOrder);
        SqlDataProcessLogQuery sqlQuery = (withActivityOnly) ? SELECT_DATA_PROCESS_RUNS_WITH_ACTIVITY
                                                             : SELECT_DATA_PROCESS_RUNS_DURING;
        PaginatedRowHandler<DataProcessRun> handler = new PaginatedRowHandler<>(limOff, "total_count", processRunRowMapper);
        jdbcNamed.query(sqlQuery.getSql(schema(), orderBy, limOff), params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<DataProcessUnit> getUnits(int processId, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource("processId", processId);
        OrderBy orderBy = new OrderBy("start_date_time", dateOrder);
        PaginatedRowHandler<DataProcessUnit> handler = new PaginatedRowHandler<>(limOff, "total_count", processUnitRowMapper);
        jdbcNamed.query(SELECT_DATA_PROCESS_UNITS.getSql(schema(), orderBy, limOff), params, handler);
        return handler.getList();
    }

    @Override
    public List<DataProcessUnit> getFirstAndLastUnits(int processId) {
        MapSqlParameterSource params = new MapSqlParameterSource("processId", processId);
        return jdbcNamed.query(SELECT_FIRST_AND_LAST_DATA_PROCESS_UNITS.getSql(schema()), params, processUnitRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void insertRun(DataProcessRun run) {
        MapSqlParameterSource params = getDataProcessRunParams(run);
        int id = jdbcNamed.queryForObject(INSERT_DATA_PROCESS_RUN.getSql(schema()), params, new SingleColumnRowMapper<>());
        run.setProcessId(id);
    }

    /** {@inheritDoc} */
    public void insertUnit(int processId, DataProcessUnit unit) {
        jdbcNamed.update(INSERT_PROCESS_UNIT.getSql(schema()), getDataProcessUnitParams(processId, unit));
    }

    /** {@inheritDoc} */
    @Override
    public void updateRun(DataProcessRun run) {
        jdbcNamed.update(UPDATE_DATA_PROCESS_RUN.getSql(schema()), getDataProcessRunParams(run));
    }

    /** --- Internal --- */

    private static final RowMapper<DataProcessRun> processRunRowMapper = (rs, rowNum) -> {
        DataProcessRun run = new DataProcessRun(rs.getInt("id"), getLocalDateTimeFromRs(rs, "process_start_date_time"),
            rs.getString("invoked_by"));
        run.setEndDateTime(getLocalDateTimeFromRs(rs, "process_end_date_time"));
        run.getExceptions().append(rs.getString("exceptions"));
        return run;
    };

    private static final RowMapper<DataProcessUnit> processUnitRowMapper = (rs, rowNum) -> {
        DataProcessUnit unit = new DataProcessUnit(
            rs.getString("source_type"), rs.getString("source_id"), getLocalDateTimeFromRs(rs, "start_date_time"),
                DataProcessAction.valueOf(rs.getString("action")));
        unit.setEndDateTime(getLocalDateTimeFromRs(rs, "end_date_time"));
        unit.setErrors(new StringBuilder(rs.getString("errors")));
        unit.setMessages(new StringBuilder(rs.getString("messages")));
        return unit;
    };

    private MapSqlParameterSource getDataProcessRunParams(DataProcessRun run) {
        return new MapSqlParameterSource()
            .addValue("id", run.getProcessId())
            .addValue("startDateTime", toDate(run.getStartDateTime()))
            .addValue("endDateTime", toDate(run.getEndDateTime()))
            .addValue("invokedBy", run.getInvokedBy())
            .addValue("exceptions", run.getExceptions().toString());
    }

    private MapSqlParameterSource getDataProcessUnitParams(int processId, DataProcessUnit unit) {
        return new MapSqlParameterSource("processId", processId)
            .addValue("sourceType", unit.getSourceType())
            .addValue("sourceId", unit.getSourceId())
            .addValue("action", unit.getAction().name())
            .addValue("startDateTime", toDate(unit.getStartDateTime()))
            .addValue("endDateTime", toDate(unit.getEndDateTime()))
            .addValue("messages", unit.getMessages())
            .addValue("errors", unit.getErrorsBuilder());
    }
}
