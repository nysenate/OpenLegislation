package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlLawDataQuery implements BasicSqlQuery
{
    SELECT_LAW_DOCUMENT(
        ""
    ),
    INSERT_LAW_DOCUMENT(
        "INSERT INTO ${schema}." + SqlTable.LAW_DOCUMENT +
        " (document_id, published_date, document_type, law_id, location_id, document_type_id, title, text, law_file_name)\n" +
        "VALUES (:documentId, :publishedDate, :documentType, :lawId, :locationId, :documentTypeId, :title, :text, :lawFileName)"
    ),
    UPDATE_LAW_DOCUMENT(
        "UPDATE ${schema}." + SqlTable.LAW_DOCUMENT + "\n" +
        "SET document_type = :documentType, law_id = :lawId, location_id = :locationId, document_type_id = :documentTypeId,\n" +
        "    title = :title, text = :text, law_file_name = :lawFileName\n" +
        "WHERE document_id = :documentId AND published_date = :publishedDate"
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
