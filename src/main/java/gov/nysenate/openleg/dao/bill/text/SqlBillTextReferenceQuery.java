package gov.nysenate.openleg.dao.bill.text;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 3/26/15.
 */
public enum SqlBillTextReferenceQuery implements BasicSqlQuery {

    INSERT_BILL_TEXT_REFERENCE(         //complete
        "INSERT INTO ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
        "(bill_print_no, bill_session_year, reference_date_time, bill_amend_version, text, memo) " +
        "VALUES(:bill_print_no, :bill_session_year, :reference_date_time,:bill_amend_version, :text, :memo)"
    ),
    UPDATE_BILL_TEXT_REFERENCE(
            "UPDATE ${schema}." +SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "SET release_date_time = :release_date_time\n" +
                    "WHERE sequence_no = :sequence_no AND bill_print_no = :bill_print_no AND bill_session_year =:bill_session_year\n" +
                    " AND reference_date_time = :reference_date_time" +
                    "///////////////////////////////////////////////////////////////////////////////////////////////////////" +
                    "//////////////////////////////////////////////////////////////////////////////////////////////////////////" +
                    "//////////////////////////////////////////////////////////////////////////////////////////////////////" +
            "/////////////////////////////////////////////////////////////////////////////////////////////////////////////////"

    ),
    DELETE_REFERENCE_ENTRIES(               //works
            "DELETE FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year AND\n" +
                    "reference_date_time = :reference_date_time"
    ),
    SELECT_BILL_TEXT_REFERENCE(             //complete
            "SELECT * FROM ${schema}."+SqlTable.BILL_TEXT_REFERENCE+"\n" +
                    "WHERE bill_print_no = :bill_print_no AND bill_session_year = :bill_session_year\n" +
                    "AND bill_amend_version = :bill_amend_version AND reference_date_time = :reference_date_time"
    ),
    SELECT_ALL_BILL_TEXT_REFERENCE(             //complete
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
            " AND reference_date_time BETWEEN :begin AND :end\n" +
            "ORDER BY reference_date_time DESC\n" +
            "LIMIT 1"
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
