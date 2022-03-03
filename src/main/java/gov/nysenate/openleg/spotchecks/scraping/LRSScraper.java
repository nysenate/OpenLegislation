package gov.nysenate.openleg.spotchecks.scraping;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by kyle on 11/3/14.
 */
public abstract class LRSScraper {

    private static final Logger logger = LogManager.getLogger(LRSScraper.class);

    protected final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("'D'yyyyMMdd'.T'HHmmss");

    @Autowired protected OpenLegEnvironment environment;
    @Autowired private EventBus eventBus;

    /**
     * Duration of tolerance for scrape failure to account for routine outages.
     */
    private static final Duration scrapeFailTolerance = Duration.ofHours(12);

    /** Tracks the start of a consecutive run of scrape failures */
    private LocalDateTime outageStart = null;

    /** Number of consecutive scrape failures that have occurred since the last scrape success */
    private int consecutiveFails = 0;

    /**
     * Attempts to scrape LRS data.
     * any scraping exceptions encountered are logged
     * @return the number of scraped files
     * @throws IOException
     */
    public int scrape() throws IOException {
        int scraped = 0;
        try {
            scraped = doScrape();
            outageStart = null;
            consecutiveFails = 0;
        } catch (ScrapingException ex) {
            handleScrapeException(ex);
        }
        return scraped;
    }

    /**
     * An abstract method that performs the scraping
     */
    protected abstract int doScrape() throws IOException, ScrapingException;

    /**
     * Logs and sends a notification for a scraping exception
     */
    private void handleScrapeException(ScrapingException ex) {
        if (outageStart == null) {
            outageStart = LocalDateTime.now();
        }
        consecutiveFails++;

        Duration outageDuration = Duration.between(outageStart, LocalDateTime.now());
        String outageSummary = consecutiveFails + " consecutive scrape errors since " + outageStart +
                " (tolerated up to " +
                DurationFormatUtils.formatDurationWords(scrapeFailTolerance.toMillis(), true, true) +
                ")";

        // If there has been a long outage with multiple failures, raise the alarm.
        if (consecutiveFails > 2 && outageDuration.compareTo(scrapeFailTolerance) > 0) {
            String notifSummary = "Abnormal LRS Outage Detected: " + outageSummary;
            String notifBody = notifSummary + "\nLast error:\n" + ExceptionUtils.getStackTrace(ex);
            logger.error(notifSummary, ex);
            eventBus.post(new Notification(
                    NotificationType.LRS_OUTAGE,
                    LocalDateTime.now(),
                    notifSummary,
                    notifBody
            ));
            // Reset outage so as to not spam error notifications
            outageStart = LocalDateTime.now();
            consecutiveFails = 0;
        } else {
            // If it isn't a big outage, just log a summary of the exception.
            logger.warn("Ignoring scraping exception: " + ExceptionUtils.getStackFrames(ex)[0]);
            logger.warn(outageSummary);
        }
    }

    protected Document getJsoupDocument(String url) {
        try {
            return Jsoup.connect(url).timeout(10000).get();
        } catch (IOException ex) {
            throw new ScrapingException(url, ex);
        }
    }

    protected String getUrlContents(URL url) {
        try {
            return IOUtils.toString(url, Charset.defaultCharset());
        } catch (IOException ex) {
            throw new ScrapingException(url, ex);
        }
    }

    protected void copyUrlToFile(URL url, File file) {
        try {
            FileUtils.copyURLToFile(url, file, 10000, 10000);
        } catch (IOException ex) {
            throw new ScrapingException(url, ex);
        }
    }
}
