package gov.nysenate.openleg.dao.process;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.process.DataProcessAction;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.model.process.DataProcessUnit;
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
    public List<DataProcessRun> getRuns(Range<LocalDateTime> dateTimeRange, boolean withActivityOnly,
                                        SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDateTime", toDate(startOfDateTimeRange(dateTimeRange)))
            .addValue("endDateTime", toDate(endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("process_start_date_time", dateOrder);
        SqlDataProcessLogQuery sqlQuery = (withActivityOnly) ? SELECT_DATA_PROCESS_RUNS_WITH_ACTIVITY
                                                             : SELECT_DATA_PROCESS_RUNS_DURING;
        return jdbcNamed.query(sqlQuery.getSql(schema(), orderBy, limOff), params, processRunRowMapper);
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
        MapSqlParameterSource processIdParam = new MapSqlParameterSource("processId", run.getProcessId());
        jdbcNamed.update(DELETE_PROCESS_UNITS.getSql(schema()), processIdParam);
    }

    /** --- Internal --- */

    private static final RowMapper<DataProcessRun> processRunRowMapper = (rs, rowNum) -> {
        DataProcessRun run = new DataProcessRun(rs.getInt("id"), getLocalDateTimeFromRs(rs, "process_start_date_time"),
            rs.getString("invoked_by"));
        run.setEndDateTime(getLocalDateTimeFromRs(rs, "process_end_date_time"));
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
            .addValue("errors", unit.getErrors());
    }
}
