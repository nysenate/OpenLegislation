package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

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
            "SELECT file_name, file_path, staged_date_time, is_archived, is_pending_processing,\n" +
                    "  COUNT(*) OVER () AS total\n" +
                    "FROM ${schema}." + SqlTable.BILL_SCRAPE_FILE + "\n" +
                    "WHERE is_pending_processing = true \n" +
                    "AND is_archived = true"
    ),

    STAGE_RELEVANT_SCRAPE_FILES_FOR_SESSION("" +
            "WITH fe AS (\n" +
            "  SELECT\n" +
            "    regexp_replace(file_name, '^(\\d{4})-.*$', '\\1')::INT bill_session_year,\n" +
            "    regexp_replace(file_name, '^\\d+-([A-Z]\\d+)-.*$', '\\1') bill_print_no,\n" +
            "    file_name \n" +
            "  FROM master.bill_scrape_file \n" +
            "  WHERE file_name LIKE :session || '%' \n" +
            "), lf AS (\n" +
            "  SELECT bill_print_no, bill_session_year, MAX(file_name) AS latest_file_name,\n" +
            "    regexp_replace(MAX(file_name), '^\\d+-[A-Z]\\d+-([0-9T]+)\\.html$', '\\1')::TIMESTAMP WITHOUT TIME zone pdt \n" +
            "  FROM fe \n" +
            "  GROUP BY bill_print_no, bill_session_year \n" +
            "), tu AS (\n" +
            "  SELECT bill_print_no, bill_session_year, MAX(published_date_time) AS last_update \n" +
            "  FROM master.bill_change_log \n" +
            "  WHERE\n" +
            "    bill_session_year = :session \n" +
            "    AND (table_name = 'bill_amendment' AND (action = 'DELETE'  OR data ?? 'full_text')\n" +
            "      OR table_name = 'bill_amendment_vote_info'\n)" +
            "  GROUP BY bill_print_no, bill_session_year \n" +
            "), gf AS (\n" +
            "  SELECT lf.*\n" +
            "  FROM lf \n" +
            "  LEFT JOIN tu \n" +
            "     ON lf.bill_print_no = tu.bill_print_no \n" +
            "    AND lf.bill_session_year = tu.bill_session_year \n" +
            "  WHERE tu.last_update IS NULL OR lf.pdt > tu.last_update \n" +
            ")\n" +
            "UPDATE master.bill_scrape_file f \n" +
            "SET is_pending_processing = TRUE \n" +
            "WHERE EXISTS (\n" +
            "  SELECT 1 \n" +
            "  FROM gf \n" +
            "  WHERE f.file_name = gf.latest_file_name \n" +
            ")"
    ),

    /** --- Scrape Queue --- */

    INSERT_SCRAPE_QUEUE(
        "INSERT INTO ${schema}." + SqlTable.BILL_SCRAPE_QUEUE+"\n" +
        "(print_no, session_year, priority) " +
        "VALUES(:printNo, :sessionYear, :priority)"
    ),
    UPDATE_SCRAPE_QUEUE(
        "UPDATE ${schema}." + SqlTable.BILL_SCRAPE_QUEUE + "\n" +
        "SET priority = GREATEST(:priority, priority)\n" +
        "WHERE session_year = :sessionYear AND print_no = :printNo"
    ),
    SELECT_SCRAPE_QUEUE(
        "SELECT *, COUNT(*) OVER () AS total FROM ${schema}."+ SqlTable.BILL_SCRAPE_QUEUE
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









