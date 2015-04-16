package gov.nysenate.openleg.dao.activelist;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

/**
 * Created by kyle on 11/21/14.
 */
public enum SqlActiveListReferenceQuery implements BasicSqlQuery {

    INSERT_ACTIVE_LIST_REFERENCE(
            "INSERT INTO ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+"(sequence_no, calendar_no, calendar_year, " +
                    "calendar_date, release_date_time, reference_date)\n"+
                    "VALUES(:sequence_no, :calendar_no, :calendar_year, :calendar_date, " +
                    ":release_date_time, :reference_date)"
    ),
    INSERT_ACTIVE_LIST_REFERENCE_ENTRY(
            "INSERT INTO ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE_ENTRY+"(active_list_reference_id, " +
                    "bill_calendar_no, bill_print_no, bill_amend_version, bill_session_year)\n"+
                    "VALUES(:active_list_reference_id, :bill_calendar_no, :bill_print_no, :bill_amend_version, " +
                    ":bill_session_year)"
    ),
    SELECT_ACTIVE_LIST(
            "SELECT * FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+"\n"+
                    "WHERE calendar_year = :calendar_year AND calendar_no = :calendar_no AND " +
                    "sequence_no = :sequence_no AND reference_date = :reference_date"
    ),
    SELECT_MOST_RECENT_REPORT(
            "SELECT * FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+"\n"+
                    "WHERE calendar_year = :calendar_year AND calendar_no = :calendar_no AND " +
                    "sequence_no = :sequence_no\n" +
                    "ORDER BY reference_date DESC\n" +
                    "LIMIT 1"
    ),
    SELECT_MOST_RECENT_FROM_EACH_YEAR(
            "SELECT * FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+" list1\n"+
                    "INNER JOIN(\n" +
                        "SELECT calendar_no, sequence_no, MAX(reference_date) maxdate\n" +
                        "FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+"\n" +
                        "GROUP BY calendar_no, sequence_no) list2 on list1.sequence_no = list2.sequence_no AND " +
                        "list1.calendar_no = list2.calendar_no and list1.reference_date = list2.maxdate\n" +
                    "WHERE calendar_year = :calendar_year"
    ),
    SELECT_ACTIVE_LIST_REFERENCE_ENTRIES(
            "SELECT * FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE_ENTRY+" refEntry, " +
                    "${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+" ref\n"+
                    "WHERE refEntry.active_list_reference_id = ref.id AND calendar_year = :calendar_year" +
                    "AND calendar_no = :calendar_no AND sequence_no = :sequence_no AND reference_date = :reference_date"
    ),
    SELECT_RANGE_ACTIVE_LIST(
            "SELECT * FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE+"\n" +
                    "WHERE releasedatetime > :begin AND releasedatetime < :end"
    ),
    DELETE_REFERENCE_ENTRIES(
            "DELETE FROM ${schema}."+SqlTable.ACTIVE_LIST_REFERENCE_ENTRY+"\n" +
                    "WHERE active_list_reference_id = :active_list_reference_id"
    ),
    UPDATE_ACTIVE_LIST(
            "UPDATE ${schema}." +SqlTable.ACTIVE_LIST_REFERENCE+"\n" +
                    "SET release_date_time = :release_date_time, calendar_date = :calendar_date\n" +
                    "WHERE sequence_no = :sequence_no AND calendar_no = :calendar_no AND calendar_year =:calendar_year" +
                    " AND reference_date = :reference_date"
    )
    ;


    private String sql;
    SqlActiveListReferenceQuery(String sql) {
        this.sql = sql;
    }
    @Override
    public String getSql() {
        return this.sql;
    }


}
