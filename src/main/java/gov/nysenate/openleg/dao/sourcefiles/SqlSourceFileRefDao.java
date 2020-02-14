package gov.nysenate.openleg.dao.sourcefiles;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.util.DateUtils.toDate;

/**
 * Created by Robert Bebber on 4/3/17.
 */
@Repository
public class SqlSourceFileRefDao extends SqlBaseDao implements SourceFileRefDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlSourceFileRefDao.class);

    @Autowired private List<SourceFileFsDao> sourceFileFsDaos;

    private ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap;

    @PostConstruct
    protected void init() {
        sourceFileDaoMap = Maps.uniqueIndex(sourceFileFsDaos, SourceFileFsDao::getSourceType);
    }

    @Override
    public SourceFile getSourceFile(String fileName) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames",
                Collections.singletonList(fileName));
        return jdbcNamed.queryForObject(
                SqlSourceFileQuery.GET_LEG_DATA_FILES_BY_FILE_NAMES.getSql(schema()), params,
                new SourceFileRowMapper(sourceFileDaoMap));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceFile(SourceFile sourceFile) {
        MapSqlParameterSource params = getSourceFileParams(sourceFile);
        if (jdbcNamed.update(SqlSourceFileQuery.UPDATE_LEG_DATA_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(SqlSourceFileQuery.INSERT_LEG_DATA_FILE.getSql(schema()), params);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SourceFile> getSourceFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SourceFile> sourceFileMap = new HashMap<>();
        List<SourceFile> sourceList = jdbcNamed.query(
                SqlSourceFileQuery.GET_LEG_DATA_FILES_BY_FILE_NAMES.getSql(schema()),
                params, new SourceFileRowMapper(sourceFileDaoMap));
        for (SourceFile sourceFile : sourceList) {
            sourceFileMap.put(sourceFile.getFileName(), sourceFile);
        }
        return sourceFileMap;
    }

    @Override
    public PaginatedList<SourceFile> getSourceFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByPubDate, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("published_date_time", sortByPubDate);
        PaginatedRowHandler<SourceFile> handler = new PaginatedRowHandler<>(limOff,
                "total_count", new SourceFileRowMapper(sourceFileDaoMap));
        final String query = SqlSourceFileQuery.GET_LEG_DATA_FILES_DURING.getSql(schema(), orderBy, limOff);
        jdbcNamed.query(query, params, handler);
        return handler.getList();
    }

    /* --- Internal Methods --- */

    private MapSqlParameterSource getSourceFileParams(SourceFile sourceFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sourceFile.getFileName());
        params.addValue("encoding", sourceFile.getEncoding());
        params.addValue("publishedDateTime", toDate(sourceFile.getPublishedDateTime()));
        params.addValue("stagedDateTime", toDate(sourceFile.getStagedDateTime()));
        params.addValue("archived", sourceFile.isArchived());
        return params;
    }
}
