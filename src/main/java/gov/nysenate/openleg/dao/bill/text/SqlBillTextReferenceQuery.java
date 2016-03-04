package gov.nysenate.openleg.dao.bill.text;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 3/26/15.
 */
public enum SqlBillTextReferenceQuery implements BasicSqlQuery {

    INSERT_BILL_TEXT_REFERENCE(
        "INSERT INTO ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
              "(bill_print_no, bill_session_year, reference_date_time, bill_amend_version, text, memo, not_found) " +
        "VALUES(:bill_print_no, :bill_session_year, :reference_date_time,:bill_amend_version, :text, :memo, :not_found)"
    ),
    SELECT_UNCHECKED_BTR (
        "SELECT * FROM (\n" +
        "   SELECT bill_print_no, bill_session_year, MAX(reference_date_time) AS reference_date_time\n" +
        "       FROM ${schema}." + SqlTable.BILL_TEXT_REFERENCE + "\n" +
        "   WHERE checked = FALSE\n" +
        "   GROUP BY bill_print_no, bill_session_year" +
        ") as mru JOIN ${schema}." + SqlTable.BILL_TEXT_REFERENCE + " btr\n" +
        "   ON mru.bill_print_no = btr.bill_print_no AND mru.bill_session_year = btr.bill_session_year\n" +
        "       AND mru.reference_date_time = btr.reference_date_time"
    ),
    SELECT_BTR_BY_PRINT_NO (
        "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year"
    ),
    SELECT_BILL_TEXT_REFERENCE(
        SELECT_BTR_BY_PRINT_NO.sql + "\n" +
        "AND reference_date_time = :reference_date_time"
    ),
    SELECT_BILL_TEXT_RANGE(
        SELECT_BTR_BY_PRINT_NO.sql + "\n" +
        "   AND reference_date_time BETWEEN :startDateTime AND :endDateTime"
    ),
    SELECT_ALL_BILL_TEXT_RANGE(
        "SELECT * FROM ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "WHERE reference_date_time BETWEEN :startDateTime AND :endDateTime"
    ),
    DELETE_BILL_REFERENCE(
        "DELETE FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year AND\n" +
        "reference_date_time = :reference_date_time"
    ),
    UPDATE_BILL_REFERENCE(      //probably works, I assume
        "UPDATE ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "SET text = :text, memo = :memo, bill_amend_version = :bill_amend_version, not_found = :not_found\n" +
        "WHERE bill_print_no = :bill_print_no AND bill_session_year =:bill_session_year\n" +
        "AND reference_date_time = :reference_date_time"
    ),
    SET_REF_CHECKED(
        "UPDATE ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "SET checked = TRUE\n" +
        "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year"
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
    SqlBillTextReferenceQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}









