package gov.nysenate.openleg.dao.transcript;

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

@Repository
public class SqlTranscriptDao extends SqlBaseDao implements TranscriptDao
{

    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(int year) {
        MapSqlParameterSource params = getTranscriptIdYearParams(year);
        List<TranscriptId> transcriptIds = jdbcNamed.query(SELECT_TRANSCRIPT_IDS_BY_YEAR.getSql(schema()), params, transcriptIdRowMapper);
        return transcriptIds;
    }

    /** {@inheritDoc} */
    @Override
    public Transcript getTranscript(TranscriptId transcriptId) throws DataAccessException {
        MapSqlParameterSource params = getTranscriptIdParams(transcriptId);
        Transcript transcript = jdbcNamed.queryForObject(SELECT_TRANSCRIPT_BY_ID.getSql(schema()), params, transcriptRowMapper);
        return transcript;
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
        TranscriptId id = new TranscriptId(rs.getString("session_type"), getLocalDateTime(rs, "date_time"));
        Transcript transcript = new Transcript(id);
        transcript.setLocation(rs.getString("location"));
        transcript.setTranscriptText(rs.getString("text"));
        transcript.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
        transcript.setPublishedDateTime(getLocalDateTime(rs, "published_date_time"));
        return transcript;
    };

    static RowMapper<TranscriptId> transcriptIdRowMapper = (rs, rowNum) ->
        new TranscriptId(rs.getString("session_type"), getLocalDateTime(rs, "date_time"));

}
