package gov.nysenate.openleg.dao.sourcefiles;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SobiDao;
import gov.nysenate.openleg.dao.sourcefiles.sobi.SqlSobiQuery;
import gov.nysenate.openleg.dao.sourcefiles.xml.XmlDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
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
public class SqlSourceFileDao extends SqlBaseDao implements SourceFileDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlSourceFileDao.class);
    @Autowired private SobiDao sobiDao;
    @Autowired private XmlDao xmlDao;

    @Override
    public SourceFile getSourceFile(String fileName) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames",
                Collections.singletonList(fileName));
        return jdbcNamed.queryForObject(
                SqlSobiQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()), params,
                new SourceFileRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceFile(SourceFile sourceFile) {
        MapSqlParameterSource params = getSourceFileParams(sourceFile);
        if (jdbcNamed.update(SqlSobiQuery.UPDATE_SOBI_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(SqlSobiQuery.INSERT_SOBI_FILE.getSql(schema()), params);
        }
    }

    private MapSqlParameterSource getSourceFileParams(SourceFile sourceFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sourceFile.getFileName());
        params.addValue("encoding", sourceFile.getEncoding());
        params.addValue("publishedDateTime", toDate(sourceFile.getPublishedDateTime()));
        params.addValue("stagedDateTime", toDate(sourceFile.getStagedDateTime()));
        params.addValue("archived", sourceFile.isArchived());
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SourceFile> getSourceFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SourceFile> sourceFileMap = new HashMap<>();
        List<SourceFile> sourceList = jdbcNamed.query(
                SqlSobiQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()),
                params, new SourceFileRowMapper());
        for (SourceFile sourceFile : sourceList) {
            sourceFileMap.put(sourceFile.getFileName(), sourceFile);
        }
        return sourceFileMap;
    }

    private class SourceFileRowMapper implements RowMapper<SourceFile> {
        @Override
        public SourceFile mapRow(ResultSet rs, int rowNum) throws SQLException {
            String fileName = rs.getString("file_name");
            LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
            boolean archived = rs.getBoolean("archived");
            String extension = FilenameUtils.getExtension(fileName);
            File file;
            if (extension.toLowerCase().equals("xml")) {
                file= archived ? xmlDao.getFileInArchiveDir(fileName, publishedDateTime)
                        : xmlDao.getFileInIncomingDir(fileName);
            } else {
                file= archived ? sobiDao.getFileInArchiveDir(fileName, publishedDateTime)
                        : sobiDao.getFileInIncomingDir(fileName);
            }

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