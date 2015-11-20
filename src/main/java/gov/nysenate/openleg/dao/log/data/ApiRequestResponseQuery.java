package gov.nysenate.openleg.dao.log.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum ApiRequestResponseQuery implements BasicSqlQuery
{
    SELECT_BY_KEY(
        "SELECT * FROM public." + SqlTable.API_REQUEST + " where apikey = :apikey"
     ),
    GET_ALL_REQUESTS(
        "SELECT * FROM public." + SqlTable.API_REQUEST
    ),
    GET_ALL_REQUESTS_BY_DATETIME(
        GET_ALL_REQUESTS.sql + " WHERE request_time BETWEEN :startDateTime AND :endDateTime"
    ),

    GET_ALL_RESPONSES(
        "SELECT * FROM public." + SqlTable.API_RESPONSE + " res\n" +
        "JOIN public." + SqlTable.API_REQUEST + " req ON res.req_id = req.request_id"
    ),
    GET_ALL_RESPONSES_BY_DATETIME(
        GET_ALL_RESPONSES.sql + " WHERE req.request_time BETWEEN :startDateTime AND :endDateTime"
    ),

    INSERT_REQUEST(
        "INSERT INTO public." + SqlTable.API_REQUEST + "\n"+
        "(request_time, url, ipaddress, method, agent, apikey)" + "\n"+
        "VALUES (:requestTime, :url, :ipAddress::inet, :requestMethod, :userAgent, :apikey)" +"\n"+
        "RETURNING request_id"
    ),
    INSERT_RESPONSE(
        "INSERT INTO public." + SqlTable.API_RESPONSE +"\n"+
        "(req_id, response_time, status_code, content_type, process_time)" + "\n"+
        "VALUES (:reqId, :responseTime, :status, :content, :processTime)" + "\n"
    );

    @Override
    public String getSql() { return this.sql; }

    private String sql;

    ApiRequestResponseQuery(String sql) {
        this.sql = sql;
    }
}
