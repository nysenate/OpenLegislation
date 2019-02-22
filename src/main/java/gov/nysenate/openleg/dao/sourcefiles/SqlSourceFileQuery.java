package gov.nysenate.openleg.dao.sourcefiles;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by senateuser on 5/9/17.
 */
public enum SqlSourceFileQuery implements BasicSqlQuery {


    /** --- Sobi Files --- */

    GET_LEG_DATA_FILES_BY_FILE_NAMES(
        "SELECT * FROM ${schema}." + SqlTable.LEG_DATA_FILE + "\n" +
        "WHERE file_name IN (:fileNames)"
    ),
    GET_LEG_DATA_FILES_DURING(
        "SELECT file_name, published_date_time, staged_date_time, encoding, archived, COUNT(*) OVER () AS total_count\n" +
        "FROM ${schema}." + SqlTable.LEG_DATA_FILE + "\n" +
        "WHERE (published_date_time BETWEEN :startDate AND :endDate)"
    ),
    INSERT_LEG_DATA_FILE(
        "INSERT INTO ${schema}." + SqlTable.LEG_DATA_FILE + "\n" +
        "(file_name, published_date_time, encoding, archived) " +
        "VALUES (:fileName, :publishedDateTime, :encoding, :archived)"
    ),
    UPDATE_LEG_DATA_FILE(
        "UPDATE ${schema}." + SqlTable.LEG_DATA_FILE + "\n" +
        "SET published_date_time = :publishedDateTime," +
        "    encoding = :encoding," +
        "    archived = :archived " +
        "WHERE file_name = :fileName"
    ),
    ;

    private String sql;

    SqlSourceFileQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
