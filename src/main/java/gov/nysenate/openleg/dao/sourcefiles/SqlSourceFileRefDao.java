package gov.nysenate.openleg.dao.sourcefiles;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                SqlSourceFileQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()), params,
                new SourceFileRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceFile(SourceFile sourceFile) {
        MapSqlParameterSource params = getSourceFileParams(sourceFile);
        if (jdbcNamed.update(SqlSourceFileQuery.UPDATE_SOBI_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(SqlSourceFileQuery.INSERT_SOBI_FILE.getSql(schema()), params);
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
                SqlSourceFileQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()),
                params, new SourceFileRowMapper());
        for (SourceFile sourceFile : sourceList) {
            sourceFileMap.put(sourceFile.getFileName(), sourceFile);
        }
        return sourceFileMap;
    }

    @Override
    public PaginatedList<SourceFile> getSobiFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByPubDate, LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("published_date_time", sortByPubDate);
        PaginatedRowHandler<SourceFile> handler = new PaginatedRowHandler<>(limOff,
                "total_count", new SourceFileRowMapper());
        final String query = SqlSourceFileQuery.GET_SOBI_FILES_DURING.getSql(schema(), orderBy, limOff);
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

    private class SourceFileRowMapper implements RowMapper<SourceFile> {
        @Override
        public SourceFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
            boolean archived = rs.getBoolean("archived");
            String extension = FilenameUtils.getExtension(fileName);
            File file;

            SourceType sourceType = SourceType.ofFile(fileName);

            if (sourceType == null) {
                throw new IllegalStateException(
                        "Could not determine " + SourceType.class.getSimpleName() +
                        " for filename: " + fileName);
            }

            SourceFileFsDao fsDao = sourceFileDaoMap.get(sourceType);

            file = archived
                    ? fsDao.getFileInArchiveDir(fileName, publishedDateTime)
                    : fsDao.getFileInIncomingDir(fileName);

            String encoding = rs.getString("encoding");
            try {

                SourceFile sourceFile;
                if (extension.toLowerCase().equals("xml")) {
                    sourceFile = new XmlFile(file, encoding);
                } else {
                    sourceFile = new SobiFile(file, encoding);
                }
                sourceFile.setArchived(archived);
                sourceFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                return sourceFile;
            } catch (FileNotFoundException ex) {
                logger.error(
                        "Source File " + rs.getString("file_name") +
                                " was not found in the expected location! \n" +
                                "This could be a result of modifications to the source file directory that were not synced with " +
                                "the database.", ex);
            } catch (IOException ex) {
                logger.error("{}", ex);
            }
            return null;
        }
    }
}