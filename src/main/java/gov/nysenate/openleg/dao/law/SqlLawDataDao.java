package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.law.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static gov.nysenate.openleg.dao.law.SqlLawDataQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlLawDataDao extends SqlBaseDao implements LawDataDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlLawDataDao.class);

    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishDate) throws DataAccessException {
        ImmutableParams treeParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("lawId", lawId)
            .addValue("endPublishedDate", toDate(endPublishDate)));
        OrderBy orderBy = new OrderBy("sequence_no", SortOrder.ASC);
        LawTreeRowCallbackHandler lawTreeHandler = new LawTreeRowCallbackHandler();
        jdbcNamed.query(SELECT_LAW_TREE.getSql(schema(), orderBy, LimitOffset.ALL), treeParams, lawTreeHandler);
        return lawTreeHandler.getLawTree();
    }

    /** {@inheritDoc} */
    @Override
    public LawDocument getLawDocument(String documentId, LocalDate endPublishDate) {
        ImmutableParams lawDocParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("docId", documentId)
            .addValue("publishedDate", toDate(endPublishDate)));
        return jdbcNamed.queryForObject(SELECT_LAW_DOCUMENT.getSql(schema()), lawDocParams, lawDocRowMapper);
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
        ImmutableParams treeIdParams = ImmutableParams.from(getLawTreeParams(lawTree));
        // Delete the existing tree if it exists
        jdbcNamed.update(DELETE_TREE.getSql(schema()), treeIdParams);
        // Insert all the nodes in the tree
        lawTree.getRootNode().getAllNodes().forEach(n -> {
            ImmutableParams treeNodeParams = ImmutableParams.from(getLawTreeNodeParams(lawFile, lawTree, n));
            jdbcNamed.update(INSERT_LAW_TREE.getSql(schema()), treeNodeParams);
        });
    }

    /**
     * Constructs a LawTree from the result set.
     */
    protected class LawTreeRowCallbackHandler implements RowCallbackHandler
    {
        private Map<String, LawTreeNode> treeNodeMap = new HashMap<>();
        private LawTreeNode root = null;
        private String lawId;
        private LocalDate publishedDate;

        /** {@inheritDoc} */
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            String docId = rs.getString("document_id");
            String parentDocId = rs.getString("parent_doc_id");

            LawTreeNode node = new LawTreeNode(lawInfoRowMapper.mapRow(rs, 0), rs.getInt("sequence_no"));
            treeNodeMap.put(docId, node);
            if (root == null) {
                root = node;
                lawId = rs.getString("law_id");
                publishedDate = getLocalDateFromRs(rs, "tree_published_date");
            }
            if (parentDocId != null) {
                if (!treeNodeMap.containsKey(parentDocId)) {
                    throw new DataIntegrityViolationException("Error while constructing tree, parent ref " + parentDocId +
                                                              " not in result set.");
                }
                treeNodeMap.get(parentDocId).addChild(node);
            }
        }

        /**
         * Returns a newly constructed LawTree based on the rows processed from the result set.
         * @return LawTree
         */
        public LawTree getLawTree() {
            if (lawId == null) {
                throw new DataRetrievalFailureException("Failed to construct LawTree, since there was no " +
                                                        "matching root node");
            }
            return new LawTree(lawId, publishedDate, root);
        }
    }

    /**
     * Constructs LawDocInfo from result set.
     */
    protected static RowMapper<LawDocInfo> lawInfoRowMapper = (rs, rowNum) ->
        new LawDocInfo(rs.getString("document_id"), rs.getString("law_id"), rs.getString("location_id"), rs.getString("title"),
            LawDocumentType.valueOf(rs.getString("document_type")), rs.getString("document_type_id"),
            getLocalDateFromRs(rs, "published_date"));

    /**
     * Constructs LawDocInfo from result set.
     */
    protected static RowMapper<LawDocument> lawDocRowMapper = (rs, rowNum) ->
        new LawDocument(lawInfoRowMapper.mapRow(rs, rowNum), rs.getString("text"));


    /** --- Param Source Methods --- */

    protected MapSqlParameterSource getLawDocumentParams(LawFile lawFile, LawDocument lawDocument) {
        return new MapSqlParameterSource()
            .addValue("documentId", lawDocument.getDocumentId())
            .addValue("publishedDate", toDate(lawDocument.getPublishedDate()))
            .addValue("documentType", lawDocument.getDocType().name())
            .addValue("lawId", lawDocument.getLawId())
            .addValue("locationId", lawDocument.getLocationId())
            .addValue("documentTypeId", lawDocument.getDocTypeId())
            .addValue("title", lawDocument.getTitle())
            .addValue("text", lawDocument.getText())
            .addValue("lawFileName", (lawFile != null) ? lawFile.getFileName() : null);
    }

    protected MapSqlParameterSource getLawTreeParams(LawTree lawTree) {
        return new MapSqlParameterSource()
            .addValue("lawId", lawTree.getLawId())
            .addValue("publishedDate", toDate(lawTree.getPublishedDate()));
    }

    protected MapSqlParameterSource getLawTreeNodeParams(LawFile lawFile, LawTree lawTree, LawTreeNode lawTreeNode) {
        return getLawTreeParams(lawTree)
            .addValue("docId", lawTreeNode.getDocumentId())
            .addValue("docPublishedDate", toDate(lawTreeNode.getPublishDate()))
            .addValue("parentDocId", (lawTreeNode.getParent() != null) ?
                                      lawTreeNode.getParent().getDocumentId() : null)
            .addValue("parentDocPublishedDate", (lawTreeNode.getParent() != null) ?
                                                 toDate(lawTreeNode.getParent().getPublishDate()) : null)
            .addValue("isRoot", lawTreeNode.isRootNode())
            .addValue("sequenceNo", lawTreeNode.getSequenceNo())
            .addValue("lawFileName", lawFile.getFileName());
    }
}
