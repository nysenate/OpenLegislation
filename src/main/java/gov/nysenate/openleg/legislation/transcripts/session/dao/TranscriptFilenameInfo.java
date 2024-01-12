package gov.nysenate.openleg.legislation.transcripts.session.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class TranscriptFilenameInfo {
    // TODO: "Senate" for session days, "SenateLD" for legislative days, "Corrected" for newer fixes
    private static final Logger logger = LoggerFactory.getLogger(TranscriptFilenameInfo.class);
    private static final Pattern filenamePattern = Pattern.compile("(?i).*(?<date>\\d{6}).*?(?<version>v\\d+)?(\\.txt)?(?<fixed>\\.fixed)?");
    private final boolean dateMatches;
    private int version = 0;

    // TODO: if the filename does not match the date, there may be a problem
    // TODO: See SenateLD080723.txt and SenateLD080923.txt
    private TranscriptFilenameInfo(String filename, LocalDate date) {
        var matcher = filenamePattern.matcher(filename);
        if (!matcher.matches()) {
            dateMatches = false;
        }
        else {
            String versionStr = matcher.group("version");
            if (versionStr != null) {
                version = Integer.parseInt(versionStr.substring(1));
            }
            // Fixed transcripts take priority
            if (matcher.group("fixed") != null) {
                version = Short.MAX_VALUE;
            }
            String dateStr = matcher.group("date");
            String fullDateStr = date.getYear() / 100 + dateStr.substring(4, 6) + dateStr.substring(0, 4);
            dateMatches = LocalDate.parse(fullDateStr, DateTimeFormatter.BASIC_ISO_DATE).equals(date);
        }
    }
    // TODO: what about fixed files? Fixed stuff should be in one place
    // TODO: Maybe warnings if it doesn't match the text in the first place?
    public static boolean isUpdate(String oldFilename, String newFilename, LocalDate date) {
        var oldInfo = new TranscriptFilenameInfo(oldFilename, date);
        var newInfo = new TranscriptFilenameInfo(newFilename, date);
        if (oldInfo.dateMatches != newInfo.dateMatches) {
            return newInfo.dateMatches;
        } else if (oldInfo.version != newInfo.version) {
            return newInfo.version > oldInfo.version;
        } else {
            logger.warn("Assuming the greater of (%s, %s) is the updated transcript.".formatted(oldFilename, newFilename));
            return newFilename.compareTo(oldFilename) > 0;
        }
    }
}
