package gov.nysenate.openleg.dao.xml;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.util.DateUtils.toDate;
import static gov.nysenate.openleg.util.FileIOUtils.getSortedFiles;

@Repository
public class SqlFsXmlDao extends SqlBaseDao implements XmlDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlFsXmlDao.class);

    /** Directory where new xml files come in from external sources. */
    private File incomingXmlDir;

    /** Directroy where xml files that have been processed are stored. */
    private File archiveXmlDir;

    @PostConstruct
    protected void init() {
        this.incomingXmlDir = new File(environment.getStagingDir(), "xmls");
        this.archiveXmlDir = new File(environment.getArchiveDir(), "xmls");
    }

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public XmlFile getXmlFile(String fileName) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", Arrays.asList(fileName));
        return jdbcNamed.queryForObject(
                GET_XML_FILES_BY_FILE_NAMES.getSql(schema()), params, new XmlFileRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, XmlFile> getXmlFiles(List<String> fileNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, XmlFile> xmlFileMap = new HashMap<>();
        List<XmlFile> xmlList = jdbcNamed.query(GET_XML_FILES_BY_FILE_NAMES.getSql(schema()),
                                                params, new XmlFileRowMapper());
        for (XmlFile xmlFile: xmlList) {
            xmlFileMap.put(xmlFile.getFileName(), xmlFile);
        }
        return xmlFileMap;
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<XmlFile> getXmlFilesDuring(Range<LocalDateTime> dateTimeRange, SortOrder sortByPubDate,
                                                    LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("published_date_time", sortByPubDate);
        PaginatedRowHandler<XmlFile> handler = new PaginatedRowHandler<XmlFile>(limitOffset, "total_count", new XmlFileRowMapper());
        jdbcNamed.query(GET_XML_FILES_DURING.getSql(schema(), orderBy, limitOffset), params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public List<XmlFile> getIncomingXmlFiles(SortOrder sortByFileName, LimitOffset limitOffset) throws IOException {
        List<File> files = new ArrayList<>(getSortedFiles(this.incomingXmlDir, false, null));
        if (sortByFileName.equals(SortOrder.DESC)) {
            Collections.reverse(files);
        }
        files = LimitOffset.limitList(files, limitOffset);
        List<XmlFile> xmlFiles = new ArrayList<XmlFile>();
        for (File file : files) {
            xmlFiles.add(new XmlFile(file));
        }
        return xmlFiles;
    }

    /** {@inheritDoc} */
    @Override
    public List<XmlFile> getPendingXmlFiles(SortOrder sortById, LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("file_id", sortById);
        return jdbcNamed.query(
                GET_PENDING_XML_FILES.getSql(schema(), orderBy, limitOffset), new XmlFileRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<XmlFile> getPendingXmlFiles(ImmutableSet<XmlFileType> restrict, SortOrder sortById,
                                            LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("file_id", sortById);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileTypes", restrict.stream().map(Enum::name).collect(Collectors.toSet()));
        return jdbcNamed.query(
                GET_PENDING_XML_FILES_BY_TYPE.getSql(schema(), orderBy, limitOffset), params, new XmlFileRowMapper()));
    }

    /** --- Update/Insert Methods --- */

    /** {@inheritDoc} */
    @Override
    public void archiveAndUpdateXmlFile(XmlFile xmlFile) throws IOException {
        File stageFile = xmlFile.getFile();
        // Archive the file only if the current one is residing in the incoming xmls directory.
        if (stageFile.getParentFile().compareTo(incomingXmlDir) == 0) {
            File archiveFile = getFileInArchiveDir(xmlFile.getFileName(), xmlFile.getPublishedDateTime());
            moveFile(stageFile, archiveFile);
            xmlFile.setFile(archiveFile);
            xmlFile.setArchived(true);
            updateXmlFile(xmlFile);
        }
        else {
            throw new FileNotFoundException(
                    "XmlFile " + stageFile + " must be in the incoming xmls directory in order to be archived.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateXmlFile(XmlFile xmlFile) {
        MapSqlParameterSource params = getXmlFileParams(xmlFile);
        if (jdbcNamed.update(UPDATE_XML_FILE.getSql(schema()), params) == 0) {
            jdbcNamed.update(INSERT_XML_FILE.getSql(schema()), params);
        }
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<XmlFile> xmlFileRowMapper = (rs, rowNum) -> {
        String fileName = rs.getString("file_name");
        LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
        boolean archived = rs.getBoolean("archived");
        File file = (archived) ? getFileInArchiveDir(fileName, publishedDateTime)
                : getFileInIncomingDir(fileName);
        String encoding = rs.getString("encoding");
        try {
            XmlFile xmlFile = new XmlFile(file, encoding);
            XmlFile.setArchived(archived);
            xmlFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
            return xmlFile;
        }
        catch (FileNotFoundException ex) {
            logger.error(
                    "XML file " + rs.getString("file_name") + " was not found in the expected location! \n" +
                            "This could be a result of modifications to the xml file directory that were not synced with " +
                            "the database.", ex);
        }
        catch (IOException ex) {
            logger.error("{}", ex);
        }
        return null;
    };

    /** --- Internal Methods --- */

    /**
     * Get file handle from incoming xml directory.
     */
    private File getFileInIncomingDir(String fileName) {
        return new File(this.incomingXmlDir, fileName);
    }

    /**
     * Get file handle from the xml archive directory.
     */
    private File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime) {
        String year = Integer.toString(publishedDateTime.getYear());
        File dir = new File(this.archiveXmlDir, year);
        return new File(dir, fileName);
    }

    /** --- Param Source Methods --- */

    /**
     * Returns a MapSqlParameterSource with columns mapped to XmlFile values.
     */
    private MapSqlParameterSource getXmlFileParams(XmlFile xmlFile) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", xmlFile.getFileName());
        params.addValue("fileId", xmlFile.getFileId());
        params.addValue("encoding", xmlFile.getEncoding());
        params.addValue("fileType", xmlFile.getType().name());
        params.addValue("publishedDateTime", toDate(xmlFile.getPublishedDateTime()));
        params.addValue("stagedDateTime", toDate(xmlFile.getStagedDateTime()));
        params.addValue("processedDateTime", toDate(xmlFile.getProcessedDateTime()));
        params.addValue("processedCount", xmlFile.getProcessedCount());
        params.addValue("pendingProcessing", xmlFile.isPendingProcessing());
        params.addValue("archived", xmlFile.isArchived());
        // Replace all null characters with empty string.
        params.addValue("text", xmlFile.getText().replace('\0', ' '));
        params.addValue("manualFix", xmlFile.isManualFix());
        params.addValue("manualFixNotes", xmlFile.getManualFixNotes());
        return params;
    }

}
