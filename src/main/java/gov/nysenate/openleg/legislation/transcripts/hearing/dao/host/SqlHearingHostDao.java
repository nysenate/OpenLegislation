package gov.nysenate.openleg.legislation.transcripts.hearing.dao.host;

import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.host.SqlHearingHostQuery.*;

@Repository
public class SqlHearingHostDao extends SqlBaseDao implements HearingHostDao {
    @Override
    public Set<HearingHost> getHearingHosts(HearingId id) {
        return new HashSet<>(jdbcNamed.query(SELECT_HOSTS_BY_HEARING_ID.getSql(schema()),
                new MapSqlParameterSource("public_hearing_id", id.id()), HEARING_HOST_ROW_MAPPER));
    }

    @Override
    public void updateHearingHosts(HearingId hearingId, Set<HearingHost> hosts) {
        for (var host : hosts) {
            MapSqlParameterSource params = new MapSqlParameterSource("name", host.name())
                    .addValue("chamber", host.chamber().name().toLowerCase())
                    .addValue("type", host.type().name());
            Integer hostId;
            try {
                hostId = getHostId(params);
            }
            catch (EmptyResultDataAccessException e) {
                jdbcNamed.update(INSERT_HOST.getSql(schema()), params);
                hostId = getHostId(params);
            }
            params = new MapSqlParameterSource().addValue("hearing_host_id", hostId)
                    .addValue("public_hearing_id", hearingId.id());
            jdbcNamed.update(INSERT_HOST_HEARING_ID_PAIR.getSql(schema()), params);
        }
    }

    @Override
    public void deleteHearingHosts(HearingId id) {
        List<Integer> hostIds = jdbcNamed.query(SELECT_HOSTS_BY_HEARING_ID.getSql(schema()),
                new MapSqlParameterSource("public_hearing_id", id.id()), ID_HOST_ROW_MAPPER);
        jdbcNamed.update(DELETE_HOSTS_WITH_HEARING_ID.getSql(schema()), Map.of("hearing_id", id.id()));
        for (int hostId : hostIds) {
            // If a host no longer has any associated hearings, it should be deleted.
            String sql = SELECT_HEARING_ID_BY_HOST_ID.getSql(schema());
            if (jdbcNamed.queryForList(sql, Map.of("hearing_host_id", hostId)).isEmpty())
                jdbcNamed.update(DELETE_HOST_BY_ID.getSql(schema()), Map.of("id", hostId));
        }
    }

    private Integer getHostId(MapSqlParameterSource params) {
        return jdbcNamed.queryForObject(SELECT_HOST_ID.getSql(schema()), params, Integer.class);
    }

    private static final RowMapper<HearingHost> HEARING_HOST_ROW_MAPPER = (rs, rowNum) -> {
        String name = rs.getString("name");
        var chamber = Chamber.getValue(rs.getString("chamber"));
        var type = HearingHostType.toType(rs.getString("type"));
        return new HearingHost(chamber, type, name);
    };

    private static final RowMapper<Integer> ID_HOST_ROW_MAPPER = (rs, rowNum) -> rs.getInt("id");
}
