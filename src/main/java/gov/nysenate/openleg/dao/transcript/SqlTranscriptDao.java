package gov.nysenate.openleg.dao.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.openleg.dao.transcript.SqlTranscriptQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlTranscriptDao extends SqlBaseDao implements TranscriptDao
{
    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(int year, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = getTranscriptIdYearParams(year);
        OrderBy orderBy = new OrderBy("date_time", dateOrder);
        return jdbcNamed.query(SELECT_TRANSCRIPT_IDS_BY_YEAR.getSql(schema(), orderBy, limOff), params, transcriptIdRowMapper);
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

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getTranscriptParams(Transcript transcript, TranscriptFile transcriptFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sessionType", transcript.getSessionType());
        params.addValue("dateTime", toDate(transcript.getDateTime()));
        params.addValue("location", transcript.getLocation());
        params.addValue("text", transcript.getTranscriptText());
        params.addValue("transcriptFile", transcriptFile.getFileName());
        return params;
    }

    private MapSqlParameterSource getTranscriptIdParams(TranscriptId transcriptId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sessionType", transcriptId.getSessionType());
        params.addValue("dateTime", toDate(transcriptId.getDateTime()));
        return params;
    }


    private MapSqlParameterSource getTranscriptIdYearParams(int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("year", year);
        return params;
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<Transcript> transcriptRowMapper = (rs, rowNum) -> {
        TranscriptId id = new TranscriptId(rs.getString("session_type"), getLocalDateTimeFromRs(rs, "date_time"));
        Transcript transcript = new Transcript(id);
        transcript.setLocation(rs.getString("location"));
        transcript.setTranscriptText(rs.getString("text"));
        transcript.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        transcript.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return transcript;
    };

    static RowMapper<TranscriptId> transcriptIdRowMapper = (rs, rowNum) ->
        new TranscriptId(rs.getString("session_type"), getLocalDateTimeFromRs(rs, "date_time"));

}
