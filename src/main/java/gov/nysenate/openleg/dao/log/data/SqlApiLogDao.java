package gov.nysenate.openleg.dao.log.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.auth.ApiRequest;
import gov.nysenate.openleg.model.auth.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.util.DateUtils.*;

@Repository
public class SqlApiLogDao extends SqlBaseDao implements ApiLogDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlApiLogDao.class);

    private static final RowMapper<ApiRequest> apiRequestMapper = (rs, rowNum) -> {
        ApiRequest request = new ApiRequest();
        request.setApiKey(rs.getString("apikey"));
        request.setRequestId(rs.getInt(("request_id")));
        request.setRequestMethod(rs.getString("method"));
        request.setRequestTime(getLocalDateTimeFromRs(rs, "request_time"));
        request.setUserAgent(rs.getString("agent"));
        request.setUrl(rs.getString("url"));
        String ipAddress = rs.getString("ipaddress");
        if (ipAddress != null) {
            try {
                request.setIpAddress(InetAddress.getByName(ipAddress));
            } catch (UnknownHostException e) {
                logger.error("Failed to parse IP", e);
            }
        }
        return request;
    };

    private static final RowMapper<ApiResponse> apiResponseMapper = (rs, rowNum) -> {
        ApiRequest apiRequest = apiRequestMapper.mapRow(rs, rowNum);
        ApiResponse response = new ApiResponse(apiRequest);
        response.setContentType(rs.getString("content_type"));
        response.setStatusCode(rs.getInt("status_code"));
        response.setProcessTime(rs.getDouble("process_time"));
        response.setResponseDateTime(getLocalDateTimeFromRs(rs, "response_time"));
        return response;
    };

    private static ImmutableParams getApiRequestParams(ApiRequest req) {
        return ImmutableParams.from(new MapSqlParameterSource()
            .addValue("ipAddress", req.getIpAddress().getHostAddress())
            .addValue("requestTime", toDate(req.getRequestTime()))
            .addValue("url", req.getUrl())
            .addValue("userAgent", req.getUserAgent())
            .addValue("apikey", req.getApiKey())
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

    /** {@inheritDoc} */
    @Override
    public List<ApiResponse> getResponses(LimitOffset limOff, SortOrder order) {
        OrderBy orderBy = new OrderBy("request_time", order);
        return jdbcNamed.query(
                ApiRequestResponseQuery.GET_ALL_RESPONSES.getSql(schema(), orderBy, limOff), apiResponseMapper);
    }

    /** {@inheritDoc} */
    @Override
    public List<ApiResponse> getResponses(Range<LocalDateTime> dateTimeRange, LimitOffset limOff, SortOrder order) {
        OrderBy orderBy = new OrderBy("request_time", order);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDateTime", startOfDateTimeRange(dateTimeRange));
        params.addValue("endDateTime", endOfDateTimeRange(dateTimeRange));
        return jdbcNamed.query(
                ApiRequestResponseQuery.GET_ALL_RESPONSES_BY_DATETIME.getSql(schema(), orderBy, limOff), params, apiResponseMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void saveApiResponse(ApiResponse response) throws DataAccessException {
        if (response != null) {
            ApiRequest apiRequest = response.getBaseRequest();
            Integer requestId = jdbcNamed.queryForObject(ApiRequestResponseQuery.INSERT_REQUEST.getSql(schema()),
                                getApiRequestParams(apiRequest), new SingleColumnRowMapper<>());
            apiRequest.setRequestId(requestId);
            jdbcNamed.update(ApiRequestResponseQuery.INSERT_RESPONSE.getSql(schema()),
                    getApiResponseParams(response));
        }
     }
}
