package gov.nysenate.openleg.spotchecks.alert.calendar;

import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.processors.BaseSourceData;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalendarAlertFile extends BaseSourceData {

    private static final Pattern filenameRegex =
            Pattern.compile("^(floor_cal|active_list)_alert-(?<calYear>\\d{4})" +
                    "-(?<calNo>\\d+)(?<floorSup>[A-Z])?-?(?<activeListSup>\\d+)?" +
                    "-(?<pubDateTime>\\d{8}T\\d{6}).html$");

    private final Matcher filenameMatcher;

    /** Reference to the actual file. */
    private final File file;
    private boolean archived;

    public CalendarAlertFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            this.file = file;
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        filenameMatcher = filenameRegex.matcher(file.getName());
        if (!filenameMatcher.matches()) {
            throw new IllegalArgumentException("Calendar Alert File name does not match expected regex.");
        }
        // By default, a file is unprocessed.
        this.setPendingProcessing(true);
    }

    public CalendarId getCalendarId() {
        int year = Integer.parseInt(filenameMatcher.group("calYear"));
        int calNo = Integer.parseInt(filenameMatcher.group("calNo"));
        return new CalendarId(calNo, year);
    }

    public Version getFloorSupplementalVersion() {
        return Version.of(filenameMatcher.group("floorSup"));
    }

    /**
     * Get the active list sequence number.
     * Assumes sequence num = 0 if not included in filename.
     * @return
     */
    public int getActiveListSeqNum() {
        String seqNum = filenameMatcher.group("activeListSup");
        return StringUtils.hasLength(seqNum) ? Integer.parseInt(seqNum) : 0;
    }

    public LocalDateTime getPublishedDateTime() {
        String date = filenameMatcher.group("pubDateTime");
        return LocalDateTime.parse(date, DateUtils.BASIC_ISO_DATE_TIME);
    }

    public boolean isFloorSupplemental() {
        return getFile().getName().contains("floor_cal");
    }

    public boolean isActiveList() {
        return getFile().getName().contains("active_list");
    }

    public File getFile() {
        return file;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
