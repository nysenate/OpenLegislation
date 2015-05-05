package gov.nysenate.openleg.dao.law.data;

import com.google.common.collect.Maps;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.law.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.law.data.SqlLawDataQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlLawDataDao extends SqlBaseDao implements LawDataDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlLawDataDao.class);

    /** {@inheritDoc} */
    @Override
    public LawInfo getLawInfo(String lawId) throws DataAccessException {
        ImmutableParams lawIdParam = ImmutableParams.from(new MapSqlParameterSource("lawId", lawId));
        return jdbcNamed.queryForObject(SqlLawDataQuery.SELECT_LAW_INFO_BY_ID.getSql(schema()), lawIdParam, lawInfoRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public List<LawInfo> getLawInfos() {
        return jdbcNamed.query(SqlLawDataQuery.SELECT_LAW_INFO.getSql(schema()), lawInfoRowMapper);
    }

    @Override
    public Map<String, LocalDate> getLastPublishedMap() {
        List<Pair<String, LocalDate>> res = jdbcNamed.query(SqlLawDataQuery.SELECT_MAX_PUB_DATE.getSql(schema()),
            (rs, rowNum) -> {
                return Pair.of(rs.getString("law_id"), getLocalDateFromRs(rs, "max_pub_date"));
        });
        return res.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishDate) throws DataAccessException {
        ImmutableParams treeParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("lawId", lawId)
            .addValue("endPublishedDate", toDate(endPublishDate)));
        OrderBy orderBy = new OrderBy("sequence_no", SortOrder.ASC);
        // Retrieve the law info first
        LawInfo lawInfo = getLawInfo(lawId);
        // Handle the tree retrieval
        LawTreeRowCallbackHandler lawTreeHandler = new LawTreeRowCallbackHandler(lawInfo);
        jdbcNamed.query(SqlLawDataQuery.SELECT_LAW_TREE.getSql(schema(), orderBy, LimitOffset.ALL), treeParams, lawTreeHandler);
        LawTree lawTree = lawTreeHandler.getLawTree();
        // Set all available published dates using a separate query
        lawTree.setPublishedDates(jdbcNamed.query(SqlLawDataQuery.SELECT_ALL_PUB_DATES.getSql(
            schema(), new OrderBy("published_date", SortOrder.ASC), LimitOffset.ALL), treeParams,
                (rs, rowNum) -> getLocalDateFromRs(rs, "published_date")));
        return lawTree;
    }

    /** {@inheritDoc} */
    @Override
    public LawDocument getLawDocument(String documentId, LocalDate endPublishDate) {
        ImmutableParams lawDocParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("docId", documentId)
            .addValue("endPublishedDate", toDate(endPublishDate)));
        return jdbcNamed.queryForObject(SqlLawDataQuery.SELECT_LAW_DOCUMENT.getSql(schema()), lawDocParams, lawDocRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, LawDocument> getLawDocuments(String lawId, LocalDate endPublishDate) throws DataAccessException {
        ImmutableParams lawDocParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("lawId", lawId)
            .addValue("endPublishedDate", toDate(endPublishDate)));
        List<LawDocument> docs = jdbcNamed.query(SqlLawDataQuery.SELECT_ALL_LAW_DOCUMENTS.getSql(schema()), lawDocParams, lawDocRowMapper);
        return Maps.uniqueIndex(docs, LawDocument::getDocumentId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawDocument(LawFile lawFile, LawDocument lawDocument) {
        ImmutableParams lawDocParams = ImmutableParams.from(getLawDocumentParams(lawFile, lawDocument));
        if (jdbcNamed.update(SqlLawDataQuery.UPDATE_LAW_DOCUMENT.getSql(schema()), lawDocParams) == 0) {
            jdbcNamed.update(SqlLawDataQuery.INSERT_LAW_DOCUMENT.getSql(schema()), lawDocParams);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateLawTree(LawFile lawFile, LawTree lawTree) {
        ImmutableParams lawInfoParams = ImmutableParams.from(getLawInfoParams(lawTree.getLawInfo()));
        // Update the law info or insert it
        if (jdbcNamed.update(SqlLawDataQuery.UPDATE_LAW_INFO.getSql(schema()), lawInfoParams) == 0) {
            jdbcNamed.update(SqlLawDataQuery.INSERT_LAW_INFO.getSql(schema()), lawInfoParams);
        }
        ImmutableParams treeIdParams = ImmutableParams.from(getLawTreeParams(lawTree));
        // Delete the existing tree if it exists
        jdbcNamed.update(SqlLawDataQuery.DELETE_TREE.getSql(schema()), treeIdParams);
        // Insert all the nodes in the tree
        lawTree.getRootNode().getAllNodes().forEach(n -> {
            ImmutableParams treeNodeParams = ImmutableParams.from(getLawTreeNodeParams(lawFile, lawTree, n));
            jdbcNamed.update(SqlLawDataQuery.INSERT_LAW_TREE.getSql(schema()), treeNodeParams);
        });
    }

    /**
     * Constructs a LawTree from the result set.
     */
    protected class LawTreeRowCallbackHandler implements RowCallbackHandler
    {
        private LinkedHashMap<String, LawTreeNode> treeNodeMap = new LinkedHashMap<>();
        private LawInfo info;
        private LawTreeNode root = null;
        private String lawId;
        private LocalDate publishedDate;

        public LawTreeRowCallbackHandler(LawInfo info) {
            this.info = info;
        }

        /** {@inheritDoc} */
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            String docId = rs.getString("document_id");
            String parentDocId = rs.getString("parent_doc_id");

            LawTreeNode node = new LawTreeNode(lawDocInfoRowMapper.mapRow(rs, 0), rs.getInt("sequence_no"));
            node.setRepealedDate(getLocalDateFromRs(rs, "repealed_date"));
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
            LawTree tree = new LawTree(new LawVersionId(lawId, publishedDate), root, info);
            tree.rebuildLookupMap();
            return tree;
        }
    }

    /**
     * Constructs LawDocInfo from result set.
     */
    protected static RowMapper<LawDocInfo> lawDocInfoRowMapper = (rs, rowNum) ->
        new LawDocInfo(rs.getString("document_id"), rs.getString("law_id"), rs.getString("location_id"), rs.getString("title"),
            LawDocumentType.valueOf(rs.getString("document_type")), rs.getString("document_type_id"),
            getLocalDateFromRs(rs, "published_date"));

    /**
     * Constructs a LawInfo from the result set.
     */
    protected static RowMapper<LawInfo> lawInfoRowMapper = (rs, rowNum) -> {
        LawInfo lawInfo = new LawInfo();
        lawInfo.setLawId(rs.getString("law_id"));
        lawInfo.setChapterId(rs.getString("chapter_id"));
        lawInfo.setName(rs.getString("name"));
        lawInfo.setType(LawType.valueOf(rs.getString("law_type")));
        return lawInfo;
    };

    /**
     * Constructs LawDocInfo from result set.
     */
    protected static RowMapper<LawDocument> lawDocRowMapper = (rs, rowNum) ->
        new LawDocument(lawDocInfoRowMapper.mapRow(rs, rowNum), rs.getString("text"));


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

    protected MapSqlParameterSource getLawInfoParams(LawInfo lawInfo) {
        return new MapSqlParameterSource()
            .addValue("lawId", lawInfo.getLawId())
            .addValue("chapterId", lawInfo.getChapterId())
            .addValue("lawType", lawInfo.getType().name())
            .addValue("name", lawInfo.getName());
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
            .addValue("repealedDate", toDate(lawTreeNode.getRepealedDate()))
            .addValue("lawFileName", lawFile.getFileName());
    }
}
