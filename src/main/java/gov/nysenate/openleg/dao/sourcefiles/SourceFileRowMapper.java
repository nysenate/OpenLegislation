package gov.nysenate.openleg.dao.sourcefiles;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.xml.XmlFile;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SourceFileRowMapper implements RowMapper<SourceFile> {

    private static final Logger logger = LoggerFactory.getLogger(SourceFileRowMapper.class);

    private ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap;

    public SourceFileRowMapper(ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap) {
        this.sourceFileDaoMap = sourceFileDaoMap;
    }

    @Override
    public SourceFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        String fileName = rs.getString("file_name");
        LocalDateTime publishedDateTime = SqlBaseDao.getLocalDateTimeFromRs(rs, "published_date_time");
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

        // TODO It would be nice to not read from disk while a database connection is open. (i.e. move this out of the row mapper)
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
            sourceFile.setStagedDateTime(SqlBaseDao.getLocalDateTimeFromRs(rs, "staged_date_time"));
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
