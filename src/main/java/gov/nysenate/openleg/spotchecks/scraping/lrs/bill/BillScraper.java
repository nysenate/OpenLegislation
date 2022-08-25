package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.spotchecks.scraping.LRSScraper;
import gov.nysenate.openleg.spotchecks.scraping.ScrapingException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by kyle on 1/29/15.
 */
@Repository
public class BillScraper extends LRSScraper {
    private static final String URL_TEMPLATE = "http://public.leginfo.state.ny.us/navigate.cgi?NVDTO:=&" +
            "QUERYDATA=${printNo}&QUERYTYPE=BILLNO&SESSYR=${sessionYear}&CBSTATUS=Y&CBTEXT=Y&CBSUMMARY=Y&CBSPONMEMO=Y&CBVOTING=Y";
    private final BillScrapeReferenceDao scrapeDao;
    private final BillScrapeReferenceHtmlParser htmlParser;

    @Autowired
    public BillScraper(BillScrapeReferenceDao scrapeDao, BillScrapeReferenceHtmlParser htmlParser) {
        this.scrapeDao = scrapeDao;
        this.htmlParser = htmlParser;
    }

    /**
     * Attempts to get the LRS html for the first bill in the scrape queue
     *
     * @return the number of bills scraped
     * @throws IOException If there is an error while downloading or saving the bill html file
     */
    @Override
    protected int doScrape() throws IOException, ScrapingException {
        try {
            BaseBillId baseBillId = scrapeDao.getScrapeQueueHead();
            HttpResponse response = makeRequest(constructUrl(baseBillId));
            // Verify bill data is in the file before saving.
            String content = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
            if (!htmlParser.isLrsOutage(content)) {
                scrapeDao.saveScrapedBillContent(content, baseBillId);
                scrapeDao.deleteBillFromScrapeQueue(baseBillId);
            }
            else {
                throw new ScrapingException("Received response indicating LRS outage for bill " + baseBillId);
            }
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
        return 1;
    }

    /**
     * Returns the HttpResponse received when calling a GET request on the given url.
     *
     * @throws ScrapingException If response status code != 200
     */
    public HttpResponse makeRequest(String url) {
        try {
            HttpResponse response = HttpClientBuilder.create().build().execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new ScrapingException("Cannot scrape url " + url +
                        ". Response status code was " + response.getStatusLine().getStatusCode());
            }
            return response;
        } catch (IOException ex) {
            throw new ScrapingException(url, ex);
        }
    }

    private static String constructUrl(BaseBillId billId) {
        return StringSubstitutor.replace(URL_TEMPLATE,
                ImmutableMap.of("printNo", billId.getPrintNo(),
                        "sessionYear", Integer.toString(billId.getSession().year())));
    }
}