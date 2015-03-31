package gov.nysenate.openleg.dao.law.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLawDataQuery implements BasicSqlQuery
{
    /** --- Law Documents --- */

    SELECT_LAW_DOCUMENT(
        "WITH max_date AS (\n" +
        "    SELECT max(published_date) AS pub_date FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "    WHERE document_id = :docId AND published_date <= :endPublishedDate" +
        ")\n" +
        "SELECT * FROM max_date, ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "WHERE document_id = :docId AND published_date = max_date.pub_date"
    ),
    SELECT_ALL_LAW_DOCUMENTS(
        "WITH latest_laws AS (\n" +
        "    SELECT document_id, max(published_date) AS published_date " +
        "    FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "    WHERE law_id = :lawId AND published_date <= :endPublishedDate \n" +
        "    GROUP BY document_id" +
        ")\n" +
        "SELECT * FROM ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "JOIN latest_laws USING (document_id, published_date)"
    ),
    INSERT_LAW_DOCUMENT(
        "INSERT INTO ${schema}." + SqlTable.LAW_DOCUMENT +
        "(document_id, published_date, document_type, law_id, location_id, document_type_id, title, text, law_file_name)\n" +
        "VALUES (:documentId, :publishedDate, :documentType, :lawId, :locationId, :documentTypeId, :title, :text, :lawFileName)"
    ),
    UPDATE_LAW_DOCUMENT(
        "UPDATE ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "SET document_type = :documentType, law_id = :lawId, location_id = :locationId, document_type_id = :documentTypeId,\n" +
        "    title = :title, text = :text, law_file_name = :lawFileName\n" +
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
        "    SELECT max(published_date) AS pub_date FROM ${schema}." + SqlTable.LAW_TREE + "\n" +
        "    WHERE law_id = :lawId AND published_date <= :endPublishedDate" +
        ")\n" +
        "SELECT t.law_id, t.published_date AS tree_published_date, t.is_root, t.sequence_no, t.repealed_date, " +
        "       d1.document_id, d1.published_date, d1.document_type, d1.location_id, d1.title, d1.document_type_id, " +
        "       t.parent_doc_id\n" +
        "FROM max_date, ${schema}." + SqlTable.LAW_TREE + " t\n" +
        "LEFT JOIN ${schema}." + SqlTable.LAW_DOCUMENT + " d1 \n" +
        "     ON t.doc_id = d1.document_id AND t.doc_published_date = d1.published_date\n" +
        "WHERE t.law_id = :lawId AND t.published_date = max_date.pub_date"
    ),
    INSERT_LAW_TREE(
        "INSERT INTO ${schema}." + SqlTable.LAW_TREE + "\n" +
        "(law_id, published_date, doc_id, doc_published_date, parent_doc_id, parent_doc_published_date, is_root, " +
        " sequence_no, repealed_date, law_file)\n" +
        "VALUES (:lawId, :publishedDate, :docId, :docPublishedDate, :parentDocId, :parentDocPublishedDate, :isRoot, " +
        "        :sequenceNo, :repealedDate, :lawFileName)"
    ),
    UPDATE_LAW_TREE(
        "UPDATE ${schema}." + SqlTable.LAW_TREE + "\n" +
        "SET parent_doc_id = :parentDocId, parent_doc_published_date = :parentDocPublishedDate, is_root = :isRoot, " +
        "    sequence_no = :sequenceNo, repealed_date = :repealedDate, law_file = :lawFileName \n" +
        "WHERE law_id = :lawId AND published_date = :publishedDate AND \n" +
        "      doc_id = :docId AND doc_published_date = :docPublishedDate"
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
    )
    ;

    private String sql;

    SqlLawDataQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
