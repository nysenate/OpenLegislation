package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.legislation.transcripts.session.dao.SqlTranscriptQuery.*;

@Repository
public class SqlTranscriptDao extends SqlBaseDao implements TranscriptDao
{
    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("date_time", sortOrder);
        return jdbcNamed.query(SELECT_TRANSCRIPT_IDS_BY_YEAR.getSql(schema(), orderBy, limOff), transcriptIdRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public Transcript getTranscript(TranscriptId transcriptId) throws DataAccessException {
        MapSqlParameterSource params = getTranscriptIdParams(transcriptId);
        return jdbcNamed.queryForObject(SELECT_TRANSCRIPT_BY_ID.getSql(schema()), params, transcriptRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTranscript(Transcript transcript) {
        MapSqlParameterSource params = getTranscriptParams(transcript);
        if (jdbcNamed.update(UPDATE_TRANSCRIPT.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_TRANSCRIPT.getSql(schema()), params);
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

    private MapSqlParameterSource getTranscriptParams(Transcript transcript) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("transcriptFilename", transcript.getFilename());
        params.addValue("sessionType", transcript.getSessionType());
        params.addValue("dateTime", toDate(transcript.getDateTime()));
        params.addValue("location", transcript.getLocation());
        params.addValue("text", transcript.getText());
        params.addValue("modified_date_time", toDate(LocalDateTime.now()));
        return params;
    }

    private MapSqlParameterSource getTranscriptIdParams(TranscriptId transcriptId) {
        return new MapSqlParameterSource("dateTime", DateUtils.toDate(transcriptId.dateTime()));
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<Transcript> transcriptRowMapper = (rs, rowNum) -> {
        LocalDateTime dateTime = getLocalDateTimeFromRs(rs, "date_time");
        TranscriptId id = new TranscriptId(dateTime);
        Transcript transcript = new Transcript(id, rs.getString("transcript_filename"), rs.getString("session_type"),
                rs.getString("location"), rs.getString("text"));
        transcript.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        transcript.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return transcript;
    };

    static RowMapper<TranscriptId> transcriptIdRowMapper = (rs, rowNum) ->
        new TranscriptId(getLocalDateTimeFromRs(rs, "date_time"));


    private static final RowMapper<TranscriptUpdateToken> transcriptUpdateRowMapper = (rs, rowNum) ->
            new TranscriptUpdateToken(new TranscriptId(getLocalDateTimeFromRs(rs, "date_time")),
                    getLocalDateTimeFromRs(rs, "modified_date_time"));
}
