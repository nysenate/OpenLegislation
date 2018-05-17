package gov.nysenate.openleg.service.scraping.bill;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.DateUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BillScrapeFile {

    private static final Pattern scrapedBillFilePattern = Pattern.compile("^(\\d{4})-([A-z]\\d+)-(\\d{8}T\\d{6}).html$");
    private File file;
    private final String fileName;
    private String filePath;
    private LocalDateTime stagedDateTime;
    private boolean isArchived;
    private boolean isPendingProcessing;

    public BillScrapeFile(String name, String path) {
        this(name, path, null, false, true);
    }

    public BillScrapeFile(String name, String path, LocalDateTime stagedDateTime,
                          boolean isArchived, boolean isPendingProcessing) {
        this.fileName = name;
        this.filePath = path;
        this.file = new File(path + name);
        this.stagedDateTime = stagedDateTime;
        this.isArchived = isArchived;
        this.isPendingProcessing = isPendingProcessing;
    }

    public BaseBillId getBaseBillId() {
        Matcher filenameMatcher = scrapedBillFilePattern.matcher(file.getName());
        if (filenameMatcher.matches()) {
            // Parse metadata from the file name
            return new BaseBillId(filenameMatcher.group(2), Integer.parseInt(filenameMatcher.group(1)));
        }
        throw new ParseError("Could not parse BaseBillId from scraped bill filename: " + file.getName());
    }

    public LocalDateTime getReferenceDateTime() {
        Matcher filenameMatcher = scrapedBillFilePattern.matcher(file.getName());
        if (filenameMatcher.matches()) {
            return LocalDateTime.parse(filenameMatcher.group(3), DateUtils.BASIC_ISO_DATE_TIME);
        }
        throw new ParseError("Could not parse ref date from  scraped bill filename: " + file.getName());
    }

    public File getFile() {
        return file;
    }

    public static Pattern getScrapedBillFilePattern() {
        return scrapedBillFilePattern;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDateTime getStagedDateTime() {
        return stagedDateTime;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public boolean isPendingProcessing() {
        return isPendingProcessing;
    }

    public void setFilePath(String filePath) {
        if (!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }
        this.filePath = filePath;
        this.file = new File(filePath, getFileName());
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public void setPendingProcessing(boolean pendingProcessing) {
        isPendingProcessing = pendingProcessing;
    }
}
