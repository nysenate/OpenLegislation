package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.SqlHearingHostQuery.*;

@Repository
public class SqlHearingHostDao extends SqlBaseDao implements HearingHostDao {
    @Override
    public List<HearingHost> getHearingHosts(PublicHearingId id) {
        return jdbcNamed.query(SELECT_HOSTS_BY_HEARING_ID.getSql(schema()),
                new MapSqlParameterSource("public_hearing_id", id.getId()), hearingHostRowMapper);
    }

    @Override
    public void updateHearingHosts(PublicHearingId hearingId, List<HearingHost> hosts) {
        for (var host : hosts) {
            MapSqlParameterSource params = new MapSqlParameterSource("name", host.getName())
                    .addValue("chamber", host.getChamber().name().toLowerCase())
                    .addValue("type", host.getType().name());
            Integer hostId;
            try {
                hostId = getHostId(params);
            }
            catch (EmptyResultDataAccessException e) {
                jdbcNamed.update(INSERT_HOST.getSql(schema()), params);
                hostId = getHostId(params);
            }
            params = new MapSqlParameterSource().addValue("hearing_host_id", hostId)
                    .addValue("public_hearing_id", hearingId.getId());
            jdbcNamed.update(INSERT_HOST_HEARING_ID_PAIR.getSql(schema()), params);
        }
    }

    private Integer getHostId(MapSqlParameterSource params) {
        return jdbcNamed.queryForObject(SELECT_HOST_ID.getSql(schema()), params, Integer.class);
    }

    private static final RowMapper<HearingHost> hearingHostRowMapper = (rs, rowNum) -> {
        String name = rs.getString("name");
        var chamber = Chamber.getValue(rs.getString("chamber"));
        var type = HearingHostType.toType(rs.getString("type"));
        return new HearingHost(chamber, type, name);
    };
}
