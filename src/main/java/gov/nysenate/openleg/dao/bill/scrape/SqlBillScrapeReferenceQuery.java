package gov.nysenate.openleg.dao.bill.scrape;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 3/26/15.
 */
public enum SqlBillScrapeReferenceQuery implements BasicSqlQuery {

    INSERT_BILL_SCRAPE_FILE(
            "INSERT INTO ${schema}." + SqlTable.BILL_SCRAPE_FILE + " \n" +
                    "(file_name, file_path) VALUES (:fileName, :filePath)"
    ),

    UPDATE_BILL_SCRAPE_FILE(
            "UPDATE ${schema}." + SqlTable.BILL_SCRAPE_FILE + "\n" +
                    "SET file_path = :filePath, \n" +
                    "is_archived = :isArchived, \n" +
                    "is_pending_processing = :isPendingProcessing \n" +
                    "WHERE file_name = :fileName"
    ),

    SELECT_INCOMING_BILL_SCRAPE_FILES(
            "SELECT file_name, file_path, staged_date_time, is_archived, is_pending_processing \n" +
                    "FROM ${schema}." + SqlTable.BILL_SCRAPE_FILE + "\n" +
                    "WHERE is_archived = false"
    ),

    SELECT_PENDING_BILL_SCRAPE_FILES(
            "SELECT file_name, file_path, staged_date_time, is_archived, is_pending_processing \n" +
                    "FROM ${schema}." + SqlTable.BILL_SCRAPE_FILE + "\n" +
                    "WHERE is_pending_processing = true \n" +
                    "AND is_archived = true"
    ),

    /** --- Scrape Queue --- */

    INSERT_SCRAPE_QUEUE(
        "INSERT INTO ${schema}."+SqlTable.BILL_SCRAPE_QUEUE+"\n" +
        "(print_no, session_year, priority) " +
        "VALUES(:printNo, :sessionYear, :priority)"
    ),
    UPDATE_SCRAPE_QUEUE(
        "UPDATE ${schema}." + SqlTable.BILL_SCRAPE_QUEUE + "\n" +
        "SET priority = :priority\n" +
        "WHERE session_year = :sessionYear AND print_no = :printNo"
    ),
    SELECT_SCRAPE_QUEUE(
        "SELECT *, COUNT(*) OVER () AS total FROM ${schema}."+SqlTable.BILL_SCRAPE_QUEUE
    ),
    DELETE_SCRAPE_QUEUE(
        "DELETE FROM ${schema}."+SqlTable.BILL_SCRAPE_QUEUE+"\n" +
        "WHERE print_no =:printNo AND session_year = :sessionYear"
    )


    ;
    private String sql;
    SqlBillScrapeReferenceQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}









