package gov.nysenate.openleg.dao.transcript;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.dao.transcript.SqlTranscriptUpdatesQuery.SELECT_TRANSCRIPTS_UPDATED_DURING;

@Repository
public class SqlTranscriptUpdatesDao extends SqlBaseDao implements TranscriptUpdatesDao
{
    /** {@inheritDoc} */
    @Override
    public List<TranscriptUpdateToken> transcriptsUpdatedDuring(Range<LocalDateTime> dateRange, SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = dateTimeRangeParams(dateRange);
        OrderBy orderBy = new OrderBy("published_date_time", dateOrder);
        return jdbcNamed.query(SELECT_TRANSCRIPTS_UPDATED_DURING.getSql(schema(), orderBy, limOff), params, transcriptUpdateRowMapper);
    }

    private MapSqlParameterSource dateTimeRangeParams(Range<LocalDateTime> dateRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDateTime", DateUtils.toDate(DateUtils.startOfDateTimeRange(dateRange)));
        params.addValue("endDateTime", DateUtils.toDate(DateUtils.endOfDateTimeRange(dateRange)));
        return params;
    }

    private static RowMapper<TranscriptUpdateToken> transcriptUpdateRowMapper = (rs, rowNum) ->
            new TranscriptUpdateToken(new TranscriptId(rs.getString("transcript_filename")),
                    getLocalDateTimeFromRs(rs, "modified_date_time"));
}
