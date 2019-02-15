package gov.nysenate.openleg.dao.entity.committee.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlCommitteeQuery implements BasicSqlQuery
{
    SELECT_COMMITTEE_ID(
            "SELECT chamber, name FROM ${schema}." + SqlTable.COMMITTEE
    ),
    TEST_COMMITTEE_ID(
            "SELECT EXISTS (\n" +
                    "SELECT 1 FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
                    "WHERE name = :committeeName::citext AND chamber = :chamber::chamber\n" +
                    ") AS exists"
    ),
    SELECT_COMMITTEE_SESSION_IDS(
            "SELECT DISTINCT chamber, committee_name, session_year FROM ${schema}." + SqlTable.COMMITTEE_VERSION
    ),
    /** Compute the reformed column for backwards compatibility */
    SELECT_COMMITTEE_VERSION_HISTORY(
            "SELECT cv.*, (\n" +
            "  SELECT MIN(created)\n" +
            "  FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "  WHERE cv.committee_name = committee_name\n" +
            "    AND cv.chamber = chamber\n" +
            "    AND cv.session_year = session_year\n" +
            "    AND cv.created < created\n" +
            "  ) AS reformed,\n" +
            "  cm.*\n" +
            "FROM ${schema}." + SqlTable.COMMITTEE_VERSION + " cv\n" +
            "JOIN ${schema}." + SqlTable.COMMITTEE_MEMBER + " cm\n" +
            "  ON cv.chamber = cm.chamber AND cv.committee_name = cm.committee_name AND cv.created = cm.version_created\n" +
            "WHERE cv.committee_name = :committeeName::citext AND cv.chamber = :chamber::chamber\n" +
            "   AND cv.session_year = :sessionYear"
    ),
    SELECT_COMMITTEE_VERSION_FOR_DATE_SQL(
            "WITH cvh AS (\n" + SELECT_COMMITTEE_VERSION_HISTORY.sql + "\n)\n" +
            "SELECT *\n" +
            "FROM cvh\n" +
            "WHERE cvh.created <= :referenceDate\n" +
            "  AND (cvh.reformed IS NULL OR cvh.reformed > :referenceDate)"
    ),
    INSERT_COMMITTEE(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE + " (name, chamber)\n" +
            "VALUES (:committeeName, CAST(:chamber AS chamber))"
    ),
    INSERT_COMMITTEE_VERSION(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "        (committee_name, chamber, session_year, location, meetday, meettime, meetaltweek,\n" +
            "           meetaltweektext, created, last_fragment_id)\n" +
            "VALUES (:committeeName, :chamber::chamber, :sessionYear, :location, :meetday, :meettime, :meetaltweek,\n" +
            "           :meetaltweektext, :referenceDate, :lastFragmentId)"
    ),
    INSERT_COMMITTEE_MEMBER(
            "INSERT INTO ${schema}." + SqlTable.COMMITTEE_MEMBER +
            " (committee_name, chamber, version_created, session_member_id, session_year, sequence_no, title, majority)\n" +
            "VALUES (:committeeName, :chamber::chamber, :referenceDate, :session_member_id, :sessionYear, :sequence_no,\n" +
            "  :title::committee_member_title, :majority)"
    ),
    UPDATE_COMMITTEE_MEETING_INFO(
            "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "SET location = :location, meetday = :meetday, meettime = :meettime, meetaltweek = :meetaltweek,\n" +
            "       meetaltweektext = :meetaltweektext, last_fragment_id = :lastFragmentId\n" +
            "WHERE committee_name = :committeeName::citext  AND chamber = :chamber::chamber\n" +
            "       AND session_year = :sessionYear AND created = :referenceDate"
    ),
    DELETE_COMMITTEE_VERSION(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
            "WHERE committee_name = :committeeName::citext AND chamber = :chamber::chamber\n" +
            "   AND session_year = :sessionYear AND created = :referenceDate"
    ),
    DELETE_COMMITTEE_MEMBERS(
            "DELETE FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
            "WHERE committee_name = :committeeName::citext AND chamber = :chamber::chamber\n" +
            "   AND session_year = :sessionYear AND version_created = :referenceDate"
    );

    private String sql;

    SqlCommitteeQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}