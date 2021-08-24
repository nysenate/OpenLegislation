package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.PUBLIC_HEARING;

public class SqlPublicHearingQuery implements BasicSqlQuery {
    public static final String SCHEMA = "master",
        TABLE = SCHEMA + "." + PUBLIC_HEARING,

        RESET_ID = "ALTER SEQUENCE " + SCHEMA + ".public_hearing_id_seq RESTART",

        SELECT_HEARING_IDS = "SELECT id FROM " + TABLE,

        SELECT_HEARING_ID_BY_FILENAME =
            "SELECT id FROM " + TABLE + "\n" +
            "WHERE filename = :filename",

        SELECT_HEARING_BY_ID =
            "SELECT * FROM " + TABLE + "\n" +
            "WHERE id = :id",

        UPDATE_HEARING =
            "UPDATE " + TABLE + "\n" +
            "SET filename = :filename, title = :title, date = :date, address = :address, text = :text, " +
                "start_time = :startTime, end_time = :endTime, modified_date_time = :modifiedDateTime," +
                "published_date_time = :publishedDateTime" + "\n" +
            "WHERE filename LIKE :filenameStart",

        INSERT_HEARING =
            "INSERT INTO " + TABLE + "\n" +
                "(filename, date, title, address, text, start_time, end_time)" + "\n" +
            "VALUES (:filename, :date, :title, :address, :text, :startTime, :endTime)",

        SELECT_HEARING_UPDATES =
            "SELECT filename, modified_date_time, COUNT(*) OVER() as total_updated " +
            "FROM " + TABLE + "\n" +
            "WHERE modified_date_time BETWEEN :startDateTime AND :endDateTime";

    @Override
    public String getSql() {
        return null;
    }
}
