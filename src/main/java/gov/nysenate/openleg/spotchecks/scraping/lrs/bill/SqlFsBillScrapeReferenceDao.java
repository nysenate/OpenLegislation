package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.spotchecks.scraping.lrs.bill.SqlBillScrapeReferenceQuery.*;

/**
 * Created by kyle on 3/19/15.
 */
@Repository
public class SqlFsBillScrapeReferenceDao extends SqlBaseDao implements BillScrapeReferenceDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlFsBillScrapeReferenceDao.class);
    private static final String FILE_TEMPLATE = "${sessionYear}-${printNo}-${scrapedTime}.html";
    private static final Pattern scrapeFilePattern =
            Pattern.compile("^(\\d{4})-([A-Z]\\d+[A-Z]?)-(\\d{8}T\\d{6})\\.html$");

    @Autowired
    private OpenLegEnvironment environment;

    private File scrapedBillIncomingDir;
    private File scrapedBillArchiveDir;

    @PostConstruct
    public void init() {
        scrapedBillIncomingDir = new File(environment.getScrapedStagingDir(), "bill");
        scrapedBillArchiveDir = new File(new File(environment.getArchiveDir(), "scraped"), "bill");
        try {
            FileUtils.forceMkdir(scrapedBillIncomingDir);
            FileUtils.forceMkdir(scrapedBillArchiveDir);
        } catch (IOException ex) {
            logger.error("could not create bill scraped dirs " + scrapedBillIncomingDir.getPath() + " and " + scrapedBillArchiveDir.getPath());
        }
    }

    @Override
    public void saveScrapedBillContent(String content, BaseBillId scrapedBill) throws IOException {
        // Save file to incoming directory
        File scrapeFile = createScrapeFile(scrapedBillIncomingDir, scrapedBill);
        FileIOUtils.writeStringToFile(scrapeFile, content, StandardCharsets.UTF_8);
    }

    private File createScrapeFile(File stagingDir, BaseBillId baseBillId) {
        String file = StringSubstitutor.replace(FILE_TEMPLATE, ImmutableMap.<String, String>builder()
                .put("sessionYear", Integer.toString(baseBillId.getSession().year()))
                .put("printNo", baseBillId.getPrintNo())
                .put("scrapedTime", LocalDateTime.now().format(DateUtils.BASIC_ISO_DATE_TIME))
                .build());
        return new File(stagingDir, file);
    }

    @Override
    public List<BillScrapeFile> registerIncomingScrapedBills() throws IOException {
        Set<String> registeredFilenames = getIncomingScrapedBills().stream()
                .map(BillScrapeFile::getFileName)
                .collect(Collectors.toSet());

        Collection<File> incomingDirFiles =
                FileUtils.listFiles(scrapedBillIncomingDir, FileFilterUtils.trueFileFilter(), null);

        List<BillScrapeFile> newFiles = incomingDirFiles.stream()
                .filter(f -> scrapeFilePattern.matcher(f.getName()).matches())
                .filter(f -> !registeredFilenames.contains(f.getName()))
                .map(f -> new BillScrapeFile(f.getName(), FilenameUtils.getFullPath(f.getPath())))
                .toList();

        newFiles.forEach(this::updateScrapedBill);

        return newFiles;
    }

    @Override
    public List<BillScrapeFile> getIncomingScrapedBills() {
        String sql = SELECT_INCOMING_BILL_SCRAPE_FILES.getSql(schema());
        return jdbcNamed.query(sql, billScrapeFileMapper);
    }

    @Override
    public BillScrapeFile archiveScrapedBill(BillScrapeFile scrapedBill) throws IOException {
        // archive file
        File archivedScrapedBill = new File(scrapedBillArchiveDir, scrapedBill.getFileName());
        FileUtils.deleteQuietly(archivedScrapedBill);
        FileUtils.moveFile(scrapedBill.getFile(), archivedScrapedBill);

        // update scraped bill
        scrapedBill.setFilePath(scrapedBillArchiveDir.getPath());
        scrapedBill.setArchived(true);

        // archive in db
        updateScrapedBill(scrapedBill);

        return scrapedBill;
    }

    @Override
    public void updateScrapedBill(BillScrapeFile scrapeFile) {
        MapSqlParameterSource params = billScrapeParams(scrapeFile);
        String updateSql = UPDATE_BILL_SCRAPE_FILE.getSql(schema());
        int updated = jdbcNamed.update(updateSql, params);

        if (updated == 0) {
            // Insert if no rows updated.
            String insertSql = INSERT_BILL_SCRAPE_FILE.getSql(schema());
            jdbcNamed.update(insertSql, params);
        }
    }

    @Override
    public PaginatedList<BillScrapeFile> getPendingScrapeBills(LimitOffset limitOffset) {
        String sql = SELECT_PENDING_BILL_SCRAPE_FILES.getSql(schema(), limitOffset);
        PaginatedRowHandler<BillScrapeFile> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total", billScrapeFileMapper);
        jdbcNamed.query(sql, rowHandler);
        return rowHandler.getList();
    }

    @Override
    public int stageArchivedScrapeFiles(SessionYear sessionYear) {
        String sql = STAGE_RELEVANT_SCRAPE_FILES_FOR_SESSION.getSql(schema());
        MapSqlParameterSource params = new MapSqlParameterSource("session", sessionYear.year());
        return jdbcNamed.update(sql, params);
    }

    @Override
    public BaseBillId getScrapeQueueHead() throws EmptyResultDataAccessException {
        PaginatedList<BillScrapeQueueEntry> scrapeQueue = getScrapeQueue(LimitOffset.ONE, SortOrder.DESC);
        if (scrapeQueue.getResults().isEmpty()) {
            throw new EmptyResultDataAccessException("no bills in scrape queue", 1);
        }
        return scrapeQueue.getResults().get(0).getBaseBillId();
    }

    @Override
    public PaginatedList<BillScrapeQueueEntry> getScrapeQueue(LimitOffset limitOffset, SortOrder order) {
        PaginatedRowHandler<BillScrapeQueueEntry> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total", scrapeQueueEntryRowMapper);
        jdbcNamed.query(SELECT_SCRAPE_QUEUE.getSql(schema(),
                new OrderBy("priority", order, "added_time", SortOrder.getOpposite(order)), limitOffset), rowHandler);
        return rowHandler.getList();
    }

    @Override
    public void addBillToScrapeQueue(BaseBillId id, int priority) {
        MapSqlParameterSource params = getQueueParams(id, priority);
        int updated = jdbcNamed.update(UPDATE_SCRAPE_QUEUE.getSql(schema()), params);
        if (updated == 0) {
            jdbcNamed.update(INSERT_SCRAPE_QUEUE.getSql(schema()), params);
        }
    }

    @Override
    public void deleteBillFromScrapeQueue(BaseBillId id){
        MapSqlParameterSource params = getQueueParams(id);
        jdbcNamed.update(DELETE_SCRAPE_QUEUE.getSql(schema()), params);
    }

    /**----------   Map Parameters   -------*/

    public MapSqlParameterSource getQueueParams(BaseBillId id) {
        return getQueueParams(id, 0);
    }

    public MapSqlParameterSource billScrapeParams(BillScrapeFile file) {
        return new MapSqlParameterSource()
                .addValue("fileName", file.getFileName())
                .addValue("filePath", file.getFilePath())
                .addValue("isArchived", file.isArchived())
                .addValue("isPendingProcessing", file.isPendingProcessing());
    }

    public MapSqlParameterSource getQueueParams(BaseBillId id, int priority) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", id.getPrintNo());
        params.addValue("sessionYear", id.getSession().year());
        params.addValue("priority", priority);
        return params;
    }
    /**----------   Bill Row Mapper   -------*/

    private static final RowMapper<BillScrapeFile> billScrapeFileMapper = (rs, rowNum) ->
            new BillScrapeFile(rs.getString("file_name"),
                    rs.getString("file_path"),
                    getLocalDateTimeFromRs(rs, "staged_date_time"),
                    rs.getBoolean("is_archived"),
                    rs.getBoolean("is_pending_processing"));

    private static final RowMapper<BillScrapeQueueEntry> scrapeQueueEntryRowMapper = (rs, rowNum) ->
            new BillScrapeQueueEntry(
                    new BaseBillId(rs.getString("print_no"), rs.getInt("session_year")),
                    rs.getInt("priority"), getLocalDateTimeFromRs(rs, "added_time")
            );

}
