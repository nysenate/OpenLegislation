package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.legislation.transcripts.session.dao.SqlTranscriptQuery.*;

@Repository
public class SqlTranscriptDao extends SqlBaseDao implements TranscriptDao {
    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("date_time", sortOrder, "session_type", SortOrder.getOpposite(sortOrder));
        return jdbcNamed.query(SELECT_TRANSCRIPT_IDS_BY_YEAR.getSql(schema(), orderBy, limOff), transcriptIdRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public Transcript getTranscript(TranscriptId transcriptId) throws DataAccessException {
        var params = new MapSqlParameterSource("dateTime", DateUtils.toDate(transcriptId.dateTime()));
        SqlTranscriptQuery query = SELECT_TRANSCRIPT_BY_DATE_TIME;
        if (transcriptId.sessionType() != null) {
            params.addValue("sessionType", transcriptId.sessionType().toString());
            query = SELECT_TRANSCRIPTS_AND_BILLS;
            return jdbcNamed.query(query.getSql(schema()), params, transcriptResultSetExtractor);
        }
        return jdbcNamed.queryForObject(query.getSql(schema()), params, transcriptRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscript(Transcript transcript) {
        LinkedHashSet<BaseBillId> billIds = transcript.getLinkedBills();
        MapSqlParameterSource params = getTranscriptParams(transcript);
        if (jdbcNamed.update(UPDATE_TRANSCRIPT.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_TRANSCRIPT.getSql(schema()), params);
        }
        else {
            for (BaseBillId billId : billIds) {
                params.addValue("billPrintNo", billId.getBasePrintNo())
                        .addValue("billSessionYear", billId.getSession().year());
                jdbcNamed.update(DELETE_TRANSCRIPT_BILL_IDS.getSql(schema()), params);

            }
        }
        for (BaseBillId billId : billIds) {
            params.addValue("billPrintNo", billId.getBasePrintNo())
                    .addValue("billSessionYear", billId.getSession().year());
            jdbcNamed.update(INSERT_TRANSCRIPT_BILL_IDS.getSql(schema()), params);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<TranscriptUpdateToken> transcriptsUpdatedDuring(Range<LocalDateTime> dateRange, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateRange);
        OrderBy orderBy = new OrderBy("modified_date_time", dateOrder);
        PaginatedRowHandler<TranscriptUpdateToken> handler = new PaginatedRowHandler<>(limOff, "total_updated", transcriptUpdateRowMapper);
        jdbcNamed.query(SELECT_TRANSCRIPTS_UPDATED_DURING.getSql(schema(), orderBy, limOff), params, handler);
        return handler.getList();
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getTranscriptParams(Transcript transcript) {
        return new MapSqlParameterSource().addValue("transcriptFilename", transcript.getFilename())
                .addValue("sessionType", transcript.getSessionType())
                .addValue("dateTime", toDate(transcript.getDateTime()))
                .addValue("dayType", transcript.getDayType().toString())
                .addValue("location", transcript.getLocation())
                .addValue("text", transcript.getText())
                .addValue("modified_date_time", toDate(LocalDateTime.now()));
    }

    /** --- Row Mapper Instances --- */

    // gets parsed bill data associated with the transcript
    private static final ResultSetExtractor<Transcript> transcriptResultSetExtractor = (rs) -> {
        if (!rs.next()) {
            throw new EmptyResultDataAccessException(1);
        }

        // read first row of result set for most transcript values
        LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "date_time");
        LocalDateTime modifiedDateTime = getLocalDateTimeFromRs(rs, "modified_date_time");
        LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
        TranscriptId id = TranscriptId.from(dateTime, rs.getString("session_type"));
        String dayTypeStr = rs.getString("day_type");
        String filename = rs.getString("transcript_filename");
        String location = rs.getString("location");
        String text = rs.getString("text");

        LinkedHashSet<BaseBillId> linkedBills = new LinkedHashSet<>();
        // do-while reuses first row for bill data
        do {
            String printNo = rs.getString("bill_print_no");
            int year = rs.getInt("bill_session_year");

            if (printNo != null && year != 0) {
                linkedBills.add(new BaseBillId(printNo, year));
            }
        }
        while (rs.next());

        Transcript transcript = new Transcript(id, DayType.valueOf(dayTypeStr), filename, location, text, linkedBills);
        transcript.setModifiedDateTime(modifiedDateTime);
        transcript.setPublishedDateTime(publishedDateTime);
        return transcript;
    };

    private static final RowMapper<Transcript> transcriptRowMapper = (rs, rowNum) -> {
        LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "date_time");
        TranscriptId id = TranscriptId.from(dateTime, rs.getString("session_type"));
        String dayTypeStr = rs.getString("day_type");
        Transcript transcript = new Transcript(id, DayType.valueOf(dayTypeStr),
                rs.getString("transcript_filename"), rs.getString("location"),
                rs.getString("text"));
        transcript.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        transcript.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return transcript;
    };

    private static final RowMapper<TranscriptId> transcriptIdRowMapper = (rs, rowNum) ->
        TranscriptId.from(getLocalDateTimeFromRs(rs, "date_time"), rs.getString("session_type"));


    private static final RowMapper<TranscriptUpdateToken> transcriptUpdateRowMapper = (rs, rowNum) ->
            new TranscriptUpdateToken(TranscriptId.from(getLocalDateTimeFromRs(rs, "date_time"),
                    rs.getString("session_type")),
                    getLocalDateTimeFromRs(rs, "modified_date_time"));
}
