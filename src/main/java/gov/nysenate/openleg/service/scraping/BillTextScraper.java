package gov.nysenate.openleg.service.scraping;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.bill.text.BillTextReferenceDao;
import gov.nysenate.openleg.dao.scraping.LRSScraper;
import gov.nysenate.openleg.dao.scraping.ScrapingIOException;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Created by kyle on 1/29/15.
 */
@Repository
public class BillTextScraper extends LRSScraper {

    private static final Logger logger = Logger.getLogger(BillTextScraper.class);

    private static final String billUrlTemplate = "http://public.leginfo.state.ny.us/navigate.cgi?NVDTO:=&" +
            "QUERYDATA=${printNo}&QUERYTYPE=BILLNO&SESSYR=${sessionYear}&CBTEXT=Y&CBSPONMEMO=Y";

    private static final String billFileTemplate = "${sessionYear}-${printNo}-${scrapedTime}.html";

    @Autowired
    BillTextReferenceDao btrDao;

    File billScrapedDir;

    @PostConstruct
    public void init() {
        billScrapedDir = new File(environment.getScrapedStagingDir(), "bill");
        try {
            FileUtils.forceMkdir(billScrapedDir);
        } catch (IOException ex) {
            logger.error("could not create bill scraped staging dir " + billScrapedDir.getPath());
        }
    }

    /**
     * Attempts to get the LRS html for the first bill in the scrape queue
     * @return the number of bills scraped
     * @throws IOException If there is an error while downloading or saving the bill html file
     */
    @Override
    protected int doScrape() throws IOException {
        try {
            BaseBillId billId = btrDao.getScrapeQueueHead();

            scrapeBill(billId, billScrapedDir);

            btrDao.deleteBillFromScrapeQueue(billId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
        return 1;
    }

    /**
     * Attempts to scrape html bill data for the given base bill id, saves the html file to the given directory
     * @param billId BaseBillId
     * @param destinationDir File
     * @throws IOException If there is an error while downloading or saving the bill html file
     */
    public void scrapeBill(BaseBillId billId, File destinationDir) throws IOException {
        String path = StrSubstitutor.replace(billUrlTemplate,
                ImmutableMap.of("printNo", billId.getPrintNo(),
                        "sessionYear", Integer.toString(billId.getSession().getYear())));

        logger.info("FETCHING landing page.");
        logger.info(path);

        URL billUrl = new URL(path);

        String filename = StrSubstitutor.replace(billFileTemplate, ImmutableMap.<String, String>builder()
                .put("sessionYear", Integer.toString(billId.getSession().getYear()))
                .put("printNo", billId.getPrintNo())
                .put("scrapedTime", LocalDateTime.now().format(DateUtils.BASIC_ISO_DATE_TIME))
                .build());

        File file = new File(destinationDir, filename);

        copyUrlToFile(billUrl, file);
    }
}