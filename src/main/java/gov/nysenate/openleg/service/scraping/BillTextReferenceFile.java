package gov.nysenate.openleg.service.scraping;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.DateUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BillTextReferenceFile {

    private static final Pattern scrapedBillFilePattern = Pattern.compile("^(\\d{4})-([A-z]\\d+)-(\\d{8}T\\d{6}).html$");
    private final File file;

    public BillTextReferenceFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
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
}
