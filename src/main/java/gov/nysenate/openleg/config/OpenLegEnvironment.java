package gov.nysenate.openleg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The OpenLegEnvironment class contains various configuration options to be used throughout the application.
 * This class is mutable during runtime so that hot config changes can be made to the fields here whereas
 * the property file is only checked during initialization.
 *
 * The fields in this class are primarily wired directly to values in the property file. Many fields
 * have setters to allow for changes while the application is running.
 */
@Component
public class OpenLegEnvironment {
    /** The database schema where the legislative data is stored. */
    @Value("${env.schema:master}") private String schema;

    /* --- File system configuration --- */

    /** The directories for incoming and archived files. */
    @Value("${env.staging}") private String stagingDirPath;
    @Value("${env.archive}") private String archiveDirPath;

    private File stagingDir;
    private File archiveDir;
    private File scrapedStagingDir;

    private LocalDateTime deployedDateTime;

    /** --- Admin Auth --- */

    @Value("${default.admin.user}") private String defaultAdminName;

    /** --- Search Index settings --- */

    /** Allow elastic search to index documents. */
    @Value("${elastic.search.enabled}") private boolean elasticIndexing;

    /** --- Processing settings --- */

    /** Enable processing of data. */
    @Value("${leg.data.process.enabled:true}") private boolean legDataProcessEnabled;

    @Value("${data.process.enabled}") private boolean processingEnabled;

    /** Allows for the option to enable/disable logging. */
    @Value("${data.process.log.enabled}") private boolean processLoggingEnabled;

    /** Enable batch processing of legislative data files. */
    @Value("${leg.data.batch.process.enabled}") private boolean legDataBatchEnabled;

    /** If leg data batch is enabled, this specifies the maximum batch size. */
    @Value("${leg.data.batch.process.size}") private int legDataBatchSize;

    /* --- Scheduling Settings --- */

    /** Enable processing of data at scheduled intervals. */
    @Value("${scheduler.process.enabled}") private boolean processingScheduled;

    /** Enable spot-check report runs at scheduled intervals. */
    @Value("${scheduler.spotcheck.enabled}") private boolean spotcheckScheduled;

    /** --- Spotcheck Settings --- */

    @Value("${spotcheck.alert.grace.period}") private int rawAlertGracePeriod;
    private Duration spotcheckAlertGracePeriod;

    /** Allows bills to be automatically added to the scrape queue if true */
    @Value("${bill.scrape.queue.enabled}") private boolean billScrapeQueueEnabled;

    /** Enables periodic checking for email spotcheck references */
    @Value("${spotcheck.checkmail.enabled:true}") private boolean checkmailEnabled;

    /** ---- Openleg Reference ---*/

    @Value("${spotcheck.openleg_ref.api.key}") private String openlegRefApiKey;

    @Value("${spotcheck.openleg_ref.url}") private String openlegRefUrl;

    /** Sets queue sizes for nysenate.gov bill report */
    @Value("${spotcheck.website.bill.ref_queue_size:500}") private int sensiteBillRefQueueSize;
    @Value("${spotcheck.website.bill.data_queue_size:500}") private int sensiteBillDataQueueSize;

    /* --- Email Settings --- */

    /** Imaps host, username, and password for the application's email account. */
    @Value("${checkmail.host}") private String emailHost;
    @Value("${checkmail.user}") private String emailUser;
    @Value("${checkmail.pass}") private String emailPass;

    /** Incoming emails are stored in the receiving folder and archived in the processed folder */
    @Value("${checkmail.receiving}") private String emailReceivingFolder;
    @Value("${checkmail.processed}") private String emailProcessedFolder;
    @Value("${checkmail.partial}") private String emailPartialDaybreakFolder;

    /** The return address on outbound emails */
    @Value("${mail.smtp.from:}") private String emailFromAddress;

    /** The contact email address on outbound emails */
    @Value("${contact.email:}") private String contactEmailAddress;

    /** --- Notifications --- */
    @Value("${notifications.enabled}") private boolean notificationsEnabled;

    @Value("${slack.notification.line.limit}") private int slackLineLimit;

    /* --- Domain Url --- */

    /** The domain and the context path of the application */
    @Value("${domain.url}") private String url;

    /** The base url of the NYSenate.gov public website */
    @Value("${nysenate.gov.url:https://www.NYSenate.gov}") private String senSiteUrl;

    /** --- Constructors --- */

    public OpenLegEnvironment() {}

    @PostConstruct
    private void init() {
        this.deployedDateTime = LocalDateTime.now();
        this.stagingDir = new File(stagingDirPath);
        this.archiveDir = new File(archiveDirPath);
        this.scrapedStagingDir = new File(stagingDir, "scraped");
        this.spotcheckAlertGracePeriod = Duration.ofMinutes(rawAlertGracePeriod);
    }

    /** --- Basic Getters/Setters --- */

    public String getSchema() {
        return schema;
    }

    public File getStagingDir() {
        return stagingDir;
    }

    public File getArchiveDir() {
        return archiveDir;
    }

    public boolean isElasticIndexing() {
        return elasticIndexing;
    }

    public void setElasticIndexing(boolean elasticIndexing) {
        this.elasticIndexing = elasticIndexing;
    }

    public boolean isProcessingEnabled() {
        return processingEnabled;
    }

    public void setProcessingEnabled(boolean processingEnabled) {
        this.processingEnabled = processingEnabled;
    }

    public boolean isProcessLoggingEnabled() {
        return processLoggingEnabled;
    }

    public boolean isLegDataBatchEnabled() {
        return legDataBatchEnabled;
    }

    public void setLegDataBatchEnabled(boolean legDataBatchEnabled) {
        this.legDataBatchEnabled = legDataBatchEnabled;
    }

    public int getLegDataBatchSize() {
        return legDataBatchSize;
    }

    public boolean isProcessingScheduled() {
        return processingScheduled;
    }

    public void setProcessingScheduled(boolean processingScheduled) {
        this.processingScheduled = processingScheduled;
    }

    public boolean isSpotcheckScheduled() {
        return spotcheckScheduled;
    }

    public void setSpotcheckScheduled(boolean spotcheckScheduled) {
        this.spotcheckScheduled = spotcheckScheduled;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public String getUrl() {
        return url;
    }

    public int getSlackLineLimit() {
        return slackLineLimit;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public String getEmailPass() {
        return emailPass;
    }

    public String getEmailReceivingFolder() {
        return emailReceivingFolder;
    }

    public String getEmailProcessedFolder() {
        return emailProcessedFolder;
    }

    public String getEmailPartialDaybreakFolder() {
        return emailPartialDaybreakFolder;
    }

    public Duration getSpotcheckAlertGracePeriod() {
        return spotcheckAlertGracePeriod;
    }

    public File getScrapedStagingDir() {
        return scrapedStagingDir;
    }

    public String getDefaultAdminName() {
        return defaultAdminName;
    }

    public boolean isBillScrapeQueueEnabled() {
        return billScrapeQueueEnabled;
    }

    public void setBillScrapeQueueEnabled(boolean billScrapeQueueEnabled) {
        this.billScrapeQueueEnabled = billScrapeQueueEnabled;
    }

    public LocalDateTime getDeployedDateTime() {
        return deployedDateTime;
    }

    // TODO: why is this never used?
    public boolean getLegDataProcessEnabled() {
        return legDataProcessEnabled;
    }

    public String getOpenlegRefApiKey() {
        return openlegRefApiKey;
    }

    public String getOpenlegRefUrl() {
        return openlegRefUrl;
    }

    public String getSenSiteUrl() {
        return senSiteUrl;
    }

    public boolean isCheckmailEnabled() {
        return checkmailEnabled;
    }

    public void setCheckmailEnabled(boolean checkmailEnabled) {
        this.checkmailEnabled = checkmailEnabled;
    }

    public int getSensiteBillRefQueueSize() {
        return sensiteBillRefQueueSize;
    }

    public int getSensiteBillDataQueueSize() {
        return sensiteBillDataQueueSize;
    }

    public String getEmailFromAddress() {
        return emailFromAddress;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }
}