package gov.nysenate.openleg.legislation.law.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlLawDataQuery implements BasicSqlQuery
{
    /** --- Law Documents --- */

    SELECT_LAW_DOCUMENT(
            "WITH all_dates AS (\n" +
            "  SELECT document_id, \n" +
            "         array_agg(published_date ORDER BY published_date) AS all_published_dates\n" +
            "  FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
            "  WHERE document_id = :docId\n" +
            "  GROUP BY document_id\n" +
            "),\n" +
            "latest AS (\n" +
            "  SELECT *\n" +
            "  FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
            "  WHERE document_id = :docId\n" +
            "    AND published_date <= :endPublishedDate\n" +
            "  ORDER BY published_date DESC\n" +
            "  LIMIT 1\n" +
            ")\n" +
            "SELECT latest.*, all_dates.all_published_dates\n" +
            "FROM latest\n" +
            "JOIN all_dates USING(document_id)"
    ),
    SELECT_ALL_LAW_DOCUMENTS(
            "WITH latest_laws AS (\n" +
            "  SELECT document_id,\n" +
            "         max(published_date) AS published_date\n" +
            "  FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
            "  WHERE law_id = :lawId\n" +
            "    AND published_date <= :endPublishedDate\n" +
            "    AND law_file_name LIKE :lawFilenamePattern\n" +
            "  GROUP BY document_id\n" +
            "),\n" +
            "all_dates AS (\n" +
            "  SELECT document_id,\n" +
            "         array_agg(published_date ORDER BY published_date) AS all_published_dates\n" +
            "  FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
            "  WHERE law_id = :lawId\n" +
            "    AND law_file_name LIKE :lawFilenamePattern\n" +
            "  GROUP BY document_id\n" +
            ")\n" +
            "SELECT ld.*, ad.all_published_dates\n" +
            "FROM ${schema}." + SqlTable.LAW_DOCUMENT + " ld\n" +
            "JOIN latest_laws ll\n" +
            "  ON ld.document_id = ll.document_id\n" +
            " AND ld.published_date = ll.published_date\n" +
            "JOIN all_dates ad\n" +
            "  ON ad.document_id = ld.document_id\n"
    ),

    INSERT_LAW_DOCUMENT(
        "INSERT INTO ${schema}." + SqlTable.LAW_DOCUMENT +
        "(document_id, published_date, document_type, law_id, location_id, document_type_id, title, text, dummy, law_file_name)\n" +
        "VALUES (:documentId, :publishedDate, :documentType, :lawId, :locationId, :documentTypeId, :title, :text, :dummy, :lawFileName)"
    ),
    UPDATE_LAW_DOCUMENT(
        "UPDATE ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "SET document_type = :documentType, law_id = :lawId, location_id = :locationId, document_type_id = :documentTypeId,\n" +
        "    title = :title, text = :text, dummy = :dummy, law_file_name = :lawFileName\n" +
        "WHERE document_id = :documentId AND published_date = :publishedDate"
    ),

    /** --- Law Trees --- */

    SELECT_MAX_PUB_DATE(
        "SELECT law_id, MAX(published_date) AS max_pub_date\n" +
        "FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
        "WHERE parent_doc_id IS NULL\n" +
        "GROUP by law_id"
    ),
    SELECT_ALL_PUB_DATES(
        "SELECT DISTINCT published_date \n" +
        "FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
        "WHERE law_id = :lawId"
    ),
    SELECT_LAW_TREE(
            "WITH max_date AS (\n" +
            "    SELECT max(published_date) AS pub_date\n" +
            "    FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
            "    WHERE law_id = :lawId\n" +
            "      AND published_date <= :endPublishedDate\n" +
            "),\n" +
            "all_dates AS (\n" +
            "    SELECT document_id,\n" +
            "           array_agg(published_date ORDER BY published_date) AS all_published_dates\n" +
            "    FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
            "    GROUP BY document_id\n" +
            ")\n" +
            "SELECT t.law_id,\n" +
            "       t.published_date AS tree_published_date,\n" +
            "       t.is_root,\n" +
            "       t.sequence_no,\n" +
            "       t.repealed_date,\n" +
            "       d1.document_id,\n" +
            "       d1.published_date,\n" +
            "       d1.document_type,\n" +
            "       d1.location_id,\n" +
            "       d1.title,\n" +
            "       d1.document_type_id,\n" +
            "       d1.dummy,\n" +
            "       ad.all_published_dates,\n" +
            "       t.parent_doc_id\n" +
            "FROM max_date\n" +
            "JOIN ${schema}." + SqlTable.LAW_TREE + " t\n" +
            "  ON t.published_date = max_date.pub_date\n" +
            "LEFT JOIN ${schema}." + SqlTable.LAW_DOCUMENT + " d1\n" +
            "  ON t.doc_id = d1.document_id\n" +
            " AND t.doc_published_date = d1.published_date\n" +
            "LEFT JOIN all_dates ad\n" +
            "  ON ad.document_id = d1.document_id\n" +
            "WHERE t.law_id = :lawId"
    ),

    SELECT_REPEALED_LAWS(
        "SELECT doc_id AS document_id, published_date, repealed_date\n" +
        "FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
        "WHERE repealed_date IS NOT NULL" +
        "  AND repealed_date BETWEEN :startDateTime AND :endDateTime"
    ),
    INSERT_LAW_TREE(
        "INSERT INTO ${schema}." + SqlTable.LAW_TREE + "\n" +
        "(law_id, published_date, doc_id, doc_published_date, parent_doc_id, parent_doc_published_date, is_root, " +
        " sequence_no, repealed_date, law_file)\n" +
        "VALUES (:lawId, :publishedDate, :docId, :docPublishedDate, :parentDocId, :parentDocPublishedDate, :isRoot, " +
        "        :sequenceNo, :repealedDate, :lawFileName)"
    ),
    DELETE_TREE(
        "DELETE FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
        "WHERE law_id = :lawId AND published_date = :publishedDate"
    ),

    /** --- Law Chapters --- */

    SELECT_LAW_INFO(
        "SELECT * FROM ${schema}." + SqlTable.LAW_INFO
    ),
    SELECT_LAW_INFO_BY_ID(
        SELECT_LAW_INFO.sql + " WHERE law_id = :lawId"
    ),
    SELECT_LAW_INFO_BY_TYPE(
        SELECT_LAW_INFO.sql + " WHERE law_type = :lawType"
    ),
    UPDATE_LAW_INFO(
        "UPDATE ${schema}." + SqlTable.LAW_INFO + "\n" +
        "SET chapter_id = :chapterId, law_type = :lawType, name = :name\n" +
        "WHERE law_id = :lawId"
    ),
    INSERT_LAW_INFO(
        "INSERT INTO ${schema}." + SqlTable.LAW_INFO + " (law_id, chapter_id, law_Type, name)\n" +
        "VALUES (:lawId, :chapterId, :lawType, :name)\n"
    );

    private final String sql;

    SqlLawDataQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
