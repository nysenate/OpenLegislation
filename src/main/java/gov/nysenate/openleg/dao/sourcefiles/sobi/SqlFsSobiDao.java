package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.Range;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.PaginatedRowHandler;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.util.DateUtils;

import static gov.nysenate.openleg.util.DateUtils.toDate;
import static gov.nysenate.openleg.util.FileIOUtils.getSortedFiles;

/**
 * Sobi files are stored in the file system to preserve their original formatting but metadata
 * for the files are stored in the database. The returned SobiFile instances are constructed
 * utilizing both data sources.
 */
//@Repository
public class SqlFsSobiDao extends SqlBaseDao implements SobiDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlFsSobiDao.class);
    
    /**
     * Directory where new sobi files come in from external sources.
     */
    private File incomingSobiDir;
    
    /**
     * Directory where sobi files that have been processed are stored.
     */
    private File archiveSobiDir;
    
    @PostConstruct
    protected void init(){
        incomingSobiDir = new File(environment.getStagingDir(), "sobis");
        archiveSobiDir = new File(environment.getArchiveDir(), "sobis");
    }
    
    /** --- Implemented Methods --- */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SobiFile getSobiFile(String fileName){
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames",
                Collections.singletonList(fileName));
        return jdbcNamed.queryForObject(
                SqlSobiQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()), params,
                new SobiFileRowMapper());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SobiFile> getSobiFiles(List<String> fileNames){
        MapSqlParameterSource params = new MapSqlParameterSource("fileNames", fileNames);
        Map<String, SobiFile> sobiFileMap = new HashMap<>();
        List<SobiFile> sobiList = jdbcNamed.query(
                SqlSobiQuery.GET_SOBI_FILES_BY_FILE_NAMES.getSql(schema()),
                params, new SobiFileRowMapper());
        for(SobiFile sobiFile : sobiList){
            sobiFileMap.put(sobiFile.getFileName(), sobiFile);
        }
        return sobiFileMap;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<SobiFile> getSobiFilesDuring(Range<LocalDateTime> dateTimeRange,
                                                      SortOrder sortByPubDate,
                                                      LimitOffset limitOffset){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        OrderBy orderBy = new OrderBy("published_date_time", sortByPubDate);
        PaginatedRowHandler<SobiFile> handler = new PaginatedRowHandler<>(limitOffset,
                "total_count", new SobiFileRowMapper());
        jdbcNamed.query(SqlSobiQuery.GET_SOBI_FILES_DURING.getSql(schema(), orderBy, limitOffset),
                params,
                handler);
        return handler.getList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SobiFile> getIncomingSobiFiles(SortOrder sortByFileName,
                                               LimitOffset limitOffset) throws IOException{
        List<File> files = new ArrayList<>(getSortedFiles(incomingSobiDir, false, null));
        if(sortByFileName == SortOrder.DESC){
            Collections.reverse(files);
        }
        files = LimitOffset.limitList(files, limitOffset);
        List<SobiFile> sobiFiles = new ArrayList<>();
        for(File file : files){
            sobiFiles.add(new SobiFile(file));
        }
        return sobiFiles;
    }
    
    /** --- Update/Insert Methods --- */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void archiveAndUpdateSobiFile(SobiFile sobiFile) throws IOException{
        File stageFile = sobiFile.getFile();
        // Archive the file only if the current one is residing in the incoming sobis directory.
        if(stageFile.getParentFile().compareTo(incomingSobiDir) == 0){
            File archiveFile = getFileInArchiveDir(sobiFile.getFileName(),
                    sobiFile.getPublishedDateTime());
            moveFile(stageFile, archiveFile);
            sobiFile.setFile(archiveFile);
            sobiFile.setArchived(true);
            updateSobiFile(sobiFile);
        }
        else{
            throw new FileNotFoundException("SobiFile " + stageFile + " must be in the incoming sobis directory in " +
                    "order to be archived.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSobiFile(SobiFile sobiFile){
        MapSqlParameterSource params = getSobiFileParams(sobiFile);
        if(jdbcNamed.update(SqlSobiQuery.UPDATE_SOBI_FILE.getSql(schema()), params) == 0){
            jdbcNamed.update(SqlSobiQuery.INSERT_SOBI_FILE.getSql(schema()), params);
        }
    }
    
    /** --- Helper Classes --- */
    
    /**
     * Get file handle from incoming sobi directory.
     */
    private File getFileInIncomingDir(String fileName){
        return new File(incomingSobiDir, fileName);
    }
    
    /**
     * Get file handle from the sobi archive directory.
     */
    private File getFileInArchiveDir(String fileName, LocalDateTime publishedDateTime){
        String year = Integer.toString(publishedDateTime.getYear());
        File dir = new File(archiveSobiDir, year);
        return new File(dir, fileName);
    }
    
    /** --- Internal Methods --- */
    
    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFile values.
     */
    private MapSqlParameterSource getSobiFileParams(SobiFile sobiFile){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fileName", sobiFile.getFileName());
        params.addValue("encoding", sobiFile.getEncoding());
        params.addValue("publishedDateTime", toDate(sobiFile.getPublishedDateTime()));
        params.addValue("stagedDateTime", toDate(sobiFile.getStagedDateTime()));
        params.addValue("archived", sobiFile.isArchived());
        return params;
    }
    
    /** --- Param Source Methods --- */
    
    /**
     * Maps rows from the sobi file table to SobiFile objects.
     */
    protected class SobiFileRowMapper implements RowMapper<SobiFile> {
        @Override
        public SobiFile mapRow(ResultSet rs, int rowNum) throws SQLException{
            String fileName = rs.getString("file_name");
            LocalDateTime publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
            boolean archived = rs.getBoolean("archived");
            File file = archived ? getFileInArchiveDir(fileName, publishedDateTime)
                    : getFileInIncomingDir(fileName);
            String encoding = rs.getString("encoding");
            try{
                SobiFile sobiFile = new SobiFile(file, encoding);
                sobiFile.setArchived(archived);
                sobiFile.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
                return sobiFile;
            }
            catch(FileNotFoundException ex){
                logger.error(
                        "SOBI file " + rs.getString("file_name") +
                                " was not found in the expected location! \n" +
                                "This could be a result of modifications to the sobi file directory that were not synced with " +
                                "the database.", ex);
            }
            catch(IOException ex){
                logger.error("{}", ex);
            }
            return null;
        }
    }
}
