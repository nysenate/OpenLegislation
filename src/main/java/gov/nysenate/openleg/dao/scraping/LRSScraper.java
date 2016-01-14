package gov.nysenate.openleg.dao.scraping;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.notification.Notification;
import gov.nysenate.openleg.model.notification.NotificationType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Created by kyle on 11/3/14.
 */
public abstract class LRSScraper {
    private static final Logger logger = Logger.getLogger(LRSScraper.class);
    @Autowired protected Environment environment;
    @Autowired private EventBus eventBus;

    protected final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("'D'yyyyMMdd'.T'HHmmss");
    protected final Pattern relativeBasePattern = Pattern.compile("(http://.+/).*");
    protected final Pattern absoluteBasePattern = Pattern.compile("(http://.+?)/.*");
    protected final Pattern linkPattern = Pattern.compile("<a href=\\\"(.*?)\\\">(.+?)</a>");
    protected final Pattern bottomPattern = Pattern.compile("src=\\\"(frmload\\.cgi\\?BOT-([0-9]+))\\\">");

    /**
     * Attempts to scrape LRS data.
     * any scraping exceptions encountered are logged
     * @return the number of scraped files
     * @throws IOException
     */
    public int scrape() throws IOException {
        try {
            return doScrape();
        } catch (ScrapingIOException ex) {
            handleScrapingTimeout(ex);
            return 0;
        }
    }

    /**
     * An abstract method that performs the scraping
     */
    protected abstract int doScrape() throws IOException, ScrapingIOException;

    /**
     * Logs and sends a notification for a scraping exception
     */
    protected void handleScrapingTimeout(ScrapingIOException ex) {
        logger.error("scraping exception: \n" + ExceptionUtils.getStackTrace(ex));
        eventBus.post(
            new Notification(
                    NotificationType.SCRAPING_EXCEPTION,
                    LocalDateTime.now(),
                    "Scraping exception: " + ExceptionUtils.getStackFrames(ex)[0],
                    ExceptionUtils.getStackTrace(ex)
        ));
    }

    protected Document getJsoupDocument(String url) {
        try {
            return Jsoup.connect(url).timeout(10000).get();
        } catch (IOException ex) {
            throw new ScrapingIOException(url, ex);
        }
    }

    protected String getUrlContents(URL url) {
        try {
            return IOUtils.toString(url);
        } catch (IOException ex) {
            throw new ScrapingIOException(url, ex);
        }
    }

    protected void copyUrlToFile(URL url, File file) {
        try {
            FileUtils.copyURLToFile(url, file, 10000, 10000);
        } catch (IOException ex) {
            throw new ScrapingIOException(url, ex);
        }
    }
}
