package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlTable;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum SqlCommitteeQuery {

    SELECT_COMMITTEE_CURRENT_SQL(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE + " JOIN ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "ON " + SqlTable.COMMITTEE + ".name=" + SqlTable.COMMITTEE_VERSION + ".committee_name" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".chamber=" + SqlTable.COMMITTEE_VERSION + ".chamber" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_version=" + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_session=" + SqlTable.COMMITTEE_VERSION + ".session_year" + "\n" +
        "WHERE " + SqlTable.COMMITTEE + ".name=:name" + "\n" + 
            "AND " + SqlTable.COMMITTEE + ".chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_COMMITTEE_AT_DATE_SQL(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year" + "\n" +
            "AND :date >= " + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND :date < " + SqlTable.COMMITTEE_VERSION + ".reformed"
    ),
    SELECT_COMMITTEE_MEMBERS(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber)" + "\n" +
            "AND session_year=:session_year AND version_created=:version_created"
    ),
    SELECT_COMMITTEE_CURRENT_VERSION(
        "SELECT current_version FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
        "WHERE name=:name AND chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_ALL_COMMITTEES(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE + " JOIN ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "ON " + SqlTable.COMMITTEE + ".name=" + SqlTable.COMMITTEE_VERSION + ".committee_name" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".chamber=" + SqlTable.COMMITTEE_VERSION + ".chamber" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_version=" + SqlTable.COMMITTEE_VERSION + ".created" + "\n" +
            "AND " + SqlTable.COMMITTEE + ".current_session=" + SqlTable.COMMITTEE_VERSION + ".session_year" + "\n" +
        "WHERE " + SqlTable.COMMITTEE + ".chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_ALL_COMMITTEE_VERSIONS(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber)"
    ),
    SELECT_PREVIOUS_COMMITTEE_VERSION(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND :date > created" + "\n" +
        "ORDER BY created DESC" + "\n" +
        "LIMIT 1"
    ),
    SELECT_NEXT_COMMITTEE_VERSION(
        "SELECT * FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND :date < created" + "\n" +
        "ORDER BY created" + "\n" +
        "LIMIT 1"
    ),
    INSERT_COMMITTEE(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE + " (name, chamber)" + "\n" +
        "VALUES (:name, CAST(:chamber AS chamber))"
    ),
    INSERT_COMMITTEE_VERSION(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE_VERSION +
        " (committee_name, chamber, session_year, location, meetday, meettime, meetaltweek, meetaltweektext, created)" + "\n" +
        "VALUES (:committee_name, CAST(:chamber AS chamber), :session_year, :location, :meetday, :meettime, :meetaltweek, :meetaltweektext, :created)"
    ),
    INSERT_COMMITTEE_MEMBER(
        "INSERT INTO ${schema}." + SqlTable.COMMITTEE_MEMBER +
        " (committee_name, chamber, version_created, member_id, session_year, sequence_no, title, majority)" + "\n" +
        "VALUES (:committee_name, CAST(:chamber AS chamber), :version_created, :member_id, :session_year, :sequence_no, CAST(:title AS committee_member_title), :majority)"
    ),
    UPDATE_COMMITTEE_MEETING_INFO(
        "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "SET location=:location, meetday=:meetday, meettime=:meettime, meetaltweek=:meetaltweek, meetaltweektext=:meetaltweektext" + "\n" +
        "WHERE committee_name=:committee_name  AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND created=:created"
    ),
    UPDATE_COMMITTEE_CURRENT_VERSION(
        "UPDATE ${schema}." + SqlTable.COMMITTEE + "\n" +
        "SET current_version=:current_version, current_session=:session" + "\n" +
        "WHERE name=:name AND chamber=CAST(:chamber AS chamber) AND current_session<=:session"
    ),
    UPDATE_COMMITTEE_VERSION_REFORMED(
        "UPDATE ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "SET reformed=:reformed" + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND created=:created"
    ),
    DELETE_COMMITTEE(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE + "\n" +
        "WHERE name=:name AND chamber=CAST(:chamber AS chamber)"
    ),
    DELETE_COMMITTEE_VERSION(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE_VERSION + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND created=:created"
    ),
    DELETE_COMMITTEE_MEMBERS(
        "DELETE FROM ${schema}." + SqlTable.COMMITTEE_MEMBER + "\n" +
        "WHERE committee_name=:committee_name AND chamber=CAST(:chamber AS chamber) AND session_year=:session_year AND version_created=:version_created"
    )
    ;
    private String sql;

    SqlCommitteeQuery(String sql) {
        this.sql=sql;
    }

    public String getSql(String environmentSchema) {
        Map<String, String> replaceMap=new HashMap<>();
        replaceMap.put("schema", environmentSchema);
        return new StrSubstitutor(replaceMap).replace(this.sql);
    }
}
