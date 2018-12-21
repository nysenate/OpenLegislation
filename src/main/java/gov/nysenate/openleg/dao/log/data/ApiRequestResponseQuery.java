package gov.nysenate.openleg.dao.log.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum ApiRequestResponseQuery implements BasicSqlQuery
{

    GET_ALL_RESPONSES(
        "SELECT req.request_id, req.request_time, req.url, req.ipaddress, req.method, req.agent,\n" +
        "  res.response_time, res.status_code, res.content_type, res.process_time,\n" +
        "  au.apikey, au.email_addr, au.authenticated, au.users_name, au.reg_token, au.org_name\n" +
        "FROM public." + SqlTable.API_RESPONSE + " res\n" +
        "JOIN public." + SqlTable.API_REQUEST + " req ON res.req_id = req.request_id\n" +
        "LEFT JOIN public." + SqlTable.API_USER + " au ON req.apikey = au.apikey"
    ),

    GET_ALL_RESPONSES_BY_DATETIME(
        GET_ALL_RESPONSES.sql + "\n" +
                "WHERE req.request_time BETWEEN :startDateTime AND :endDateTime"
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
