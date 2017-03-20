package gov.nysenate.openleg.service.scraping;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.bill.text.BillTextReferenceDao;
import gov.nysenate.openleg.dao.scraping.LRSScraper;
import gov.nysenate.openleg.dao.scraping.ScrapingIOException;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
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
     *
     * @return the number of bills scraped
     * @throws IOException If there is an error while downloading or saving the bill html file
     */
    @Override
    protected int doScrape() throws IOException {
        try {
            BaseBillId billId = btrDao.getScrapeQueueHead();
            HttpResponse response = makeRequest(constructUrl(billId));
            File file = getSaveFile(billScrapedDir, billId);
            saveResponseToFile(response, file);
            btrDao.deleteBillFromScrapeQueue(billId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
        return 1;
    }

    public void saveResponseToFile(HttpResponse response, File file) throws IOException {
        FileUtils.copyInputStreamToFile(response.getEntity().getContent(), file);
    }

    /**
     * Returns the HttpResponse received when calling a GET request on the given url.
     *
     * @throws ScrapingIOException If response status code != 200
     */
    public HttpResponse makeRequest(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new ScrapingIOException("Cannot scrape url " + url + ". Response status code was " + response.getStatusLine().getStatusCode());
        }
        return response;
    }

    public String constructUrl(BaseBillId billId) {
        return StrSubstitutor.replace(billUrlTemplate,
                ImmutableMap.of("printNo", billId.getPrintNo(),
                        "sessionYear", Integer.toString(billId.getSession().getYear())));
    }

    public File getSaveFile(File dir, BaseBillId billId) {
        String file = StrSubstitutor.replace(billFileTemplate, ImmutableMap.<String, String>builder()
                .put("sessionYear", Integer.toString(billId.getSession().getYear()))
                .put("printNo", billId.getPrintNo())
                .put("scrapedTime", LocalDateTime.now().format(DateUtils.BASIC_ISO_DATE_TIME))
                .build());
        return new File(dir, file);
    }
}