package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLawDataQuery implements BasicSqlQuery
{
    /** --- Law Documents --- */

    SELECT_LAW_DOCUMENT(
        ""
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

    SELECT_LAW_TREE(
        "SELECT * \n" +
        "FROM ${schema}." + SqlTable.LAW_TREE + " t \n" +
        "LEFT JOIN ${schema}." + SqlTable.LAW_DOCUMENT + " d1 \n" +
        "     ON t.doc_id = d1.document_id AND t.doc_published_date = d1.published_date\n" +
        "LEFT JOIN ${schema}." + SqlTable.LAW_DOCUMENT + " d2 \n" +
        "     ON t.parent_doc_id = d2.document_id AND t.parent_doc_published_date = d2.published_date\n" +
        "WHERE law_id = :lawId AND published_date <= :endPublishedDate"
    ),
    INSERT_LAW_TREE(
        "INSERT INTO ${schema}." + SqlTable.LAW_TREE + "\n" +
        "(law_id, published_date, doc_id, doc_published_date, parent_doc_id, parent_doc_published_date, is_root, law_file)\n" +
        "VALUES (:lawId, :publishedDate, :docId, :docPublishedDate, :parentDocId, :parentDocPublishedDate, :isRoot, :lawFileName)"
    ),
    UPDATE_LAW_TREE(
        "UPDATE ${schema}." + SqlTable.LAW_TREE + "\n" +
        "SET parent_doc_id = :parentDocId, parent_doc_published_date = :parentDocPublishedDate, is_root = :isRoot, " +
        "    law_file = :lawFileName \n" +
        "WHERE law_id = :lawId AND published_date = :publishedDate AND \n" +
        "      doc_id = :docId AND doc_published_date = :docPublishedDate"
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
