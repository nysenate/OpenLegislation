package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static gov.nysenate.openleg.dao.law.SqlLawDataQuery.INSERT_LAW_DOCUMENT;
import static gov.nysenate.openleg.dao.law.SqlLawDataQuery.UPDATE_LAW_DOCUMENT;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlLawDataDao extends SqlBaseDao implements LawDataDao
{
    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishDate) throws DataAccessException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public LawDocument getLawDocument(String documentId, LocalDate endPublishDate) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawDocument(LawFile lawFile, LawDocument lawDocument) {
        ImmutableParams lawDocParams = ImmutableParams.from(getLawDocumentParams(lawFile, lawDocument));
        if (jdbcNamed.update(UPDATE_LAW_DOCUMENT.getSql(schema()), lawDocParams) == 0) {
            jdbcNamed.update(INSERT_LAW_DOCUMENT.getSql(schema()), lawDocParams);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawTree(LawFile lawFile, LawTree lawTree) {

    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getLawDocumentParams(LawFile lawFile, LawDocument lawDocument) {
        return new MapSqlParameterSource()
            .addValue("documentId", lawDocument.getDocumentId())
            .addValue("publishedDate", toDate(lawDocument.getPublishDate()))
            .addValue("documentType", lawDocument.getDocType().name())
            .addValue("lawId", lawDocument.getLawId())
            .addValue("locationId", lawDocument.getLocationId())
            .addValue("documentTypeId", lawDocument.getDocTypeId())
            .addValue("title", lawDocument.getTitle())
            .addValue("text", lawDocument.getText())
            .addValue("lawFileName", lawFile.getFileName());
    }
}
