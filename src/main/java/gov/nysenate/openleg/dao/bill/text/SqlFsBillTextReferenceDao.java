package gov.nysenate.openleg.dao.bill.text;

import com.google.common.collect.Range;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillScrapeQueueEntry;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.FileIOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static gov.nysenate.openleg.dao.bill.text.SqlBillTextReferenceQuery.*;

/**
 * Created by kyle on 3/19/15.
 */
@Repository
public class SqlFsBillTextReferenceDao extends SqlBaseDao implements BillTextReferenceDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlFsBillTextReferenceDao.class);

    @Autowired
    Environment environment;

    File scrapedBillIncomingDir;
    File scrapedBillArchiveDir;

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
    public Collection<File> getIncomingScrapedBills() throws IOException{
        return FileIOUtils.safeListFiles(scrapedBillIncomingDir, false, new String[]{});
    }

    @Override
    public void archiveScrapedBill(File scrapedBill) throws IOException {
        FileUtils.moveFileToDirectory(scrapedBill, scrapedBillArchiveDir, true);
    }

    @Override
    public List<BillTextReference> getUncheckedBillTextReferences() {
        return jdbcNamed.query(SELECT_UNCHECKED_BTR.getSql(schema()), billTextRefRowMapper);
    }

    @Override
    public void insertBillTextReference(BillTextReference ref) {
        MapSqlParameterSource params = getParams(ref);
        if (jdbcNamed.update(UPDATE_BILL_REFERENCE.getSql(schema()), params) == 0){
            jdbcNamed.update(INSERT_BILL_TEXT_REFERENCE.getSql(schema()), params);
        }
    }

    @Override
    public void setChecked(BaseBillId billId) {
        MapSqlParameterSource params = getParams(billId);
        jdbcNamed.update(SET_REF_CHECKED.getSql(schema()), params);
    }

    @Override
    public void deleteBillTextReference(BillTextReference ref) {
        MapSqlParameterSource params = getParams(ref);
        jdbcNamed.update(DELETE_BILL_REFERENCE.getSql(schema()), params);
    }

    @Override
    public BillTextReference getMostRecentBillTextReference(BaseBillId id, LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = getParams(id);       //not sure if need this or need to change
        Range<LocalDateTime> range = Range.closed(start, end);
        addDateTimeRangeParams(params, range);
        return jdbcNamed.queryForObject(SELECT_BILL_TEXT_RANGE.getSql(schema(),
                    new OrderBy("reference_date_time", SortOrder.DESC), LimitOffset.ONE),
                params, billTextRefRowMapper);
    }
    @Override
    public BillTextReference getMostRecentBillTextReference(LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        Range<LocalDateTime> range = Range.closed(start, end);
        addDateTimeRangeParams(params,  range);
        return jdbcNamed.queryForObject(SELECT_ALL_BILL_TEXT_RANGE.getSql(schema(),
                        new OrderBy("reference_date_time", SortOrder.DESC), LimitOffset.ONE),
                params, billTextRefRowMapper);
    }

    @Override
    public List<BillTextReference> getBillTextReference(BaseBillId id) {
        MapSqlParameterSource params = getParams(id);
        return jdbcNamed.query(SELECT_BTR_BY_PRINT_NO.getSql(schema()), params, billTextRefRowMapper);
    }

    @Override
    public BillTextReference getBillTextReference(BaseBillId id, LocalDateTime refDateTime) {
        MapSqlParameterSource params = getParams(id, refDateTime);
        return jdbcNamed.queryForObject(SELECT_BILL_TEXT_REFERENCE.getSql(schema()), params, billTextRefRowMapper);
    }

    @Override
    public void addBillToScrapeQueue(BaseBillId id, int priority) {
        MapSqlParameterSource params = getQueueParams(id, priority);
        try {
            jdbcNamed.update(INSERT_SCRAPE_QUEUE.getSql(schema()), params);
        }catch(DuplicateKeyException ex){
            jdbcNamed.update(UPDATE_SCRAPE_QUEUE.getSql(schema()), params);
        }
    }

    @Override
    public void deleteBillFromScrapeQueue(BaseBillId id){
        MapSqlParameterSource params = getQueueParams(id);
        jdbcNamed.update(DELETE_SCRAPE_QUEUE.getSql(schema()), params);
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
    public BaseBillId getScrapeQueueHead() throws EmptyResultDataAccessException {
        PaginatedList<BillScrapeQueueEntry> scrapeQueue = getScrapeQueue(LimitOffset.ONE, SortOrder.DESC);
        if (scrapeQueue.getResults().isEmpty()) {
            throw new EmptyResultDataAccessException("no bills in scrape queue", 1);
        }
        return scrapeQueue.getResults().get(0).getBaseBillId();
    }

    /**----------   Map Parameters   -------*/
    public MapSqlParameterSource getParams(BillTextReference entry){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", entry.getPrintNo());
        params.addValue("bill_session_year", entry.getSessionYear());
        params.addValue("reference_date_time", DateUtils.toDate(entry.getReferenceDate()));
        params.addValue("bill_amend_version", entry.getActiveVersion().getValue());
        params.addValue("text", entry.getText());
        params.addValue("memo", entry.getMemo());
        params.addValue("not_found", entry.isNotFound());
        return params;
    }
    public MapSqlParameterSource getParams(BaseBillId id, LocalDateTime refDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", id.getPrintNo());
        params.addValue("bill_session_year", id.getSession().getYear());
        params.addValue("bill_amend_version", id.getVersion().getValue());
        params.addValue("reference_date_time", DateUtils.toDate(refDateTime));

        return params;
    }
    public MapSqlParameterSource getParams(BaseBillId id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", id.getPrintNo());
        params.addValue("bill_session_year", id.getSession().getYear());
        params.addValue("bill_amend_version", id.getVersion().getValue());

        return params;
    }

    public MapSqlParameterSource getQueueParams(BaseBillId id) {
        return getQueueParams(id, 0);
    }

    public MapSqlParameterSource getQueueParams(BaseBillId id, int priority) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("printNo", id.getPrintNo());
        params.addValue("sessionYear", id.getSession().getYear());
        params.addValue("priority", priority);
        return params;
    }
    /**----------   Bill Row Mapper   -------*/

    private static final RowMapper<BillTextReference> billTextRefRowMapper = (rs, rowNum) -> {
        BillTextReference ref = new BillTextReference();
        ref.setBaseBillId(new BaseBillId(rs.getString("bill_print_no"), SessionYear.of(rs.getInt("bill_session_year"))));
        ref.setText(rs.getString("text"));
        ref.setMemo(rs.getString("memo"));
        ref.setReferenceDate(DateUtils.getLocalDateTime(rs.getTimestamp("reference_date_time")));
        ref.setActiveVersion(Version.of(rs.getString("bill_amend_version")));
        ref.setNotFound(rs.getBoolean("not_found"));
        return ref;
    };

    private static final RowMapper<BillScrapeQueueEntry> scrapeQueueEntryRowMapper = (rs, rowNum) ->
            new BillScrapeQueueEntry(
                    new BaseBillId(rs.getString("print_no"), rs.getInt("session_year")),
                    rs.getInt("priority"), getLocalDateTimeFromRs(rs, "added_time")
            );

}
