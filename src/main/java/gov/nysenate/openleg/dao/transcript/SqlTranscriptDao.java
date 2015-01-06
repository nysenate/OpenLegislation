package gov.nysenate.openleg.dao.transcript;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.dao.transcript.SqlTranscriptQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlTranscriptDao extends SqlBaseDao implements TranscriptDao
{
    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("filename", sortOrder);
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
    public void updateTranscript(Transcript transcript, TranscriptFile transcriptFile) {
        MapSqlParameterSource params = getTranscriptParams(transcript, transcriptFile);
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

    private MapSqlParameterSource getTranscriptParams(Transcript transcript, TranscriptFile transcriptFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("transcriptFilename", transcriptFile.getFileName());
        params.addValue("sessionType", transcript.getSessionType());
        params.addValue("dateTime", toDate(transcript.getDateTime()));
        params.addValue("location", transcript.getLocation());
        params.addValue("text", transcript.getText());
        return params;
    }

    private MapSqlParameterSource getTranscriptIdParams(TranscriptId transcriptId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("transcriptFilename", transcriptId.getFilename());
        return params;
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<Transcript> transcriptRowMapper = (rs, rowNum) -> {
        TranscriptId id = new TranscriptId(rs.getString("transcript_filename"));
        Transcript transcript = new Transcript(id, rs.getString("session_type"), getLocalDateTimeFromRs(rs, "date_time"),
                rs.getString("location"), rs.getString("text"));
        transcript.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        transcript.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return transcript;
    };

    static RowMapper<TranscriptId> transcriptIdRowMapper = (rs, rowNum) ->
        new TranscriptId(rs.getString("filename"));


    private static RowMapper<TranscriptUpdateToken> transcriptUpdateRowMapper = (rs, rowNum) ->
            new TranscriptUpdateToken(new TranscriptId(rs.getString("transcript_filename")),
                    getLocalDateTimeFromRs(rs, "modified_date_time"));
}
