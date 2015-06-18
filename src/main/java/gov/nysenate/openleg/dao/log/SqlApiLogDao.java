package gov.nysenate.openleg.dao.log;

import gov.nysenate.openleg.dao.auth.RequestResponseQuery;
import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlApiLogDao extends SqlBaseDao implements ApiLogDao
{
    @Override
    public List<ApiRequest> getRequests() {
        throw new UnsupportedOperationException();
    }

    private static final RowMapper<ApiRequest> apiRequestMapper = (rs, rowNum) -> {
        ApiRequest request = new ApiRequest();
        request.setApikey(rs.getString("apikey"));
        request.setRequestId(rs.getInt(("request_id")));
        request.setRequestMethod(rs.getString("method"));
        request.setRequestTime(getLocalDateTimeFromRs(rs, "request_time"));
        request.setUserAgent(rs.getString("agent"));
        request.setUrl(rs.getString("url"));
        request.setIpAddress((InetAddress) rs.getObject("ipaddress"));
        return request;
    };

    private static ImmutableParams getApiRequestParams(ApiRequest req) {
        return ImmutableParams.from(new MapSqlParameterSource()
            .addValue("ipAddress", req.getIpAddress().getHostAddress())
            .addValue("requestTime", toDate(req.getRequestTime()))
            .addValue("url", req.getUrl())
            .addValue("userAgent", req.getUserAgent())
            .addValue("apikey", req.getApikey())
            .addValue("requestMethod", req.getRequestMethod())
        );
    }

    private static ImmutableParams getApiResponseParams(ApiResponse response) {
        return ImmutableParams.from(new MapSqlParameterSource()
            .addValue("reqId", response.getBaseRequest().getRequestId())
            .addValue("responseTime", toDate(response.getResponseDateTime()))
            .addValue("status", response.getStatusCode())
            .addValue("content", response.getContentType())
            .addValue("processTime", response.getProcessTime()));
    }

    /**
     * Save an API Request to the database
     * @param req The API Request to save
     * @return The id of the API request to be used in creating an API response
     */
    @Override
    public int saveApiRequest(ApiRequest req) throws DataAccessException {
        return jdbcNamed.queryForObject(RequestResponseQuery.INSERT_REQUEST.getSql(schema()),
                getApiRequestParams(req), new SingleColumnRowMapper<>());
    }

    /**
     * Save an API Response
     * @param response The API response to save to the database
     * @throws DataAccessException
     */
    @Override
    public void saveApiResponse(ApiResponse response) throws DataAccessException {
        jdbcNamed.update(RequestResponseQuery.INSERT_RESPONSE.getSql(schema()),
            getApiResponseParams(response));
     }
}
