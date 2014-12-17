package gov.nysenate.openleg.dao.bill.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlVetoQuery implements BasicSqlQuery
{
    SELECT_VETO_MESSAGE_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_VETO + "\n" +
        "WHERE veto_number = :vetoNumber AND year = :year"
    ),
    SELECT_BILL_VETOES_SQL(
        "SELECT * FROM ${schema}." + SqlTable.BILL_VETO + "\n" +
        "WHERE bill_print_no = :printNum AND bill_session_year = :sessionYear" + "\n"
    ),
    UPDATE_VETO_MESSAGE_SQL(
        "UPDATE ${schema}." + SqlTable.BILL_VETO + "\n" +
        "SET bill_print_no = :printNum, bill_session_year = :sessionYear, type = CAST(:type AS ${schema}.veto_type), " + "\n" +
            "chapter = :chapter, page = :page, line_start = :lineStart, line_end = :lineEnd, " + "\n" +
            "date = :date, signer = :signer, memo_text = :memoText, " + "\n" +
            "modified_date_time = :modifiedDateTime, last_fragment_id = :lastFragmentId" + "\n" +
        "WHERE veto_number = :vetoNumber AND year = :year"
    ),
    INSERT_VETO_MESSAGE_SQL(
        "INSERT INTO ${schema}." + SqlTable.BILL_VETO + "\n" +
            "(veto_number, year, bill_print_no, bill_session_year, chapter, page, line_start, line_end, signer, date," + "\n" +
            " memo_text, type, modified_date_time, published_date_time, last_fragment_id )" +"\n" +
        "VALUES (:vetoNumber, :year, :printNum, :sessionYear, :chapter, :page, :lineStart, :lineEnd, :signer, :date, " + "\n" +
            " :memoText, CAST(:type AS ${schema}.veto_type), :modifiedDateTime, :publishedDateTime, :lastFragmentId)"
    ),
    DELETE_VETO_MESSAGE(
        "DELETE FROM ${schema}." + SqlTable.BILL_VETO + "\n" +
        "WHERE veto_number = :vetoNumber AND year = :year"
    ),
    DELETE_BILL_VETOES(
        "DELETE FROM ${schema}." + SqlTable.BILL_VETO + "\n" +
        "WHERE bill_print_no = :printNum AND bill_session_year = :sessionYear"
    )
    ;

    private String sql;

    SqlVetoQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
