package gov.nysenate.openleg.dao.bill.text;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 3/26/15.
 */
public enum SqlBillTextReferenceQuery implements BasicSqlQuery {

    INSERT_BILL_TEXT_REFERENCE(
        "INSERT INTO ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
              "(bill_print_no, bill_session_year, reference_date_time, bill_amend_version, text, memo) " +
        "VALUES(:bill_print_no, :bill_session_year, :reference_date_time,:bill_amend_version, :text, :memo)"
    ),
    SELECT_BILL_TEXT_REFERENCE(
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year\n" +
                    "AND bill_amend_version = :bill_amend_version AND reference_date_time = :reference_date_time"
    ),
    SELECT_PK_BILL_TEXT_REFERENCE(
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year\n" +
                    "AND reference_date_time = :reference_date_time"
    ),
    SELECT_ALL_BILL_TEXT_REFERENCE(
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year\n" +
                    "AND bill_amend_version = :bill_amend_version"
    ),
    SELECT_BILL_TEXT_BY_PRINT_NO(
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
            "WHERE bill_print_no = :bill_print_no"
    ),
    SELECT_BILL_TEXT_BY_SESSION_YEAR(
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
            "WHERE bill_session_year = :bill_session_year"
    ),
    SELECT_BILL_TEXT_RANGE(
            "Select * FROM ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    " WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year\n" +
                    " AND reference_date_time BETWEEN :startDateTime AND :endDateTime\n" +
                    "ORDER BY reference_date_time DESC\n" +
                    "LIMIT 1"
    ),
    SELECT_ALL_BILL_TEXT_RANGE(
            "Select * FROM ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    " WHERE reference_date_time BETWEEN :startDateTime AND :endDateTime\n" +
                    "ORDER BY reference_date_time DESC\n" +
                    "LIMIT 1"
    ),
    DELETE_BILL_REFERENCE(
           "DELETE FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
            "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year AND\n" +
            "reference_date_time = :reference_date_time"
    ),
    UPDATE_BILL_REFERENCE(      //probably works, I assume
            "UPDATE ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "SET text = :text, memo = :memo, bill_amend_version = :bill_amend_version\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year =:bill_session_year\n" +
                    "AND reference_date_time = :reference_date_time"
    ),
    INSERT_SCRAPE_QUEUE(
            "INSERT INTO ${schema}."+SqlTable.BILL_SCRAPE_QUEUE+"\n" +
                    "(bill_print_no, bill_session_year, added_time) " +
                    "VALUES(:bill_print_no, :bill_session_year, :added_time)"
    ),
    SELECT_SCRAPE_QUEUE(
            "SELECT * FROM ${schema}."+SqlTable.BILL_SCRAPE_QUEUE+"\n" +
                    "ORDER BY added_time asc"
    ),
    DELETE_SCRAPE_QUEUE(
            "DELETE FROM ${schema}."+SqlTable.BILL_SCRAPE_QUEUE+"\n" +
                    "WHERE print_no =:print_no AND session_year = :session_year"

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









