package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.util.Pair;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranscriptFilenameInfo {
    private static final Pattern date = Pattern.compile("\\d{6}"), dayType = Pattern.compile("SENATE(LD)?"),
            corrected = Pattern.compile("CORRECTED"), fixed = Pattern.compile("FIXED$"),
            versionPattern = Pattern.compile("V\\d+"), sessionType = Pattern.compile("E");
    private String mismatches = "";
    private Integer version = null, priority = null;

    public TranscriptFilenameInfo(Transcript transcript) {
        Pair<String> data = parseTextWithPattern(transcript.getFilename().toUpperCase().replaceAll("\\.(TXT)?", ""), date);
        LocalDate transcriptDate = transcript.getDateTime().toLocalDate();
        String dateStr = String.format("%02d%02d%02d",
                transcriptDate.getMonthValue(), transcriptDate.getDayOfMonth(), transcriptDate.getYear()%100);
        if (!dateStr.equals(data.v1())) {
            addMismatch("date", dateStr, data.v1());
        }
        data = parseTextWithPattern(data.v2(), dayType);
        DayType filenameDayType;
        if (data.v1() == null) {
            filenameDayType = null;
        }
        else if (data.v1().endsWith("LD")) {
            filenameDayType = DayType.LEGISLATIVE;
        }
        else {
            filenameDayType = DayType.SESSION;
        }
        if (filenameDayType != null && filenameDayType != transcript.getDayType()) {
            addMismatch("Day type", transcript.getDayType().toString(), filenameDayType.toString());
        }
        data = parseTextWithPattern(data.v2(), corrected);
        if (data.v1() != null) {
            this.priority = 1 << 7;
        }
        data = parseTextWithPattern(data.v2(), fixed);
        if (data.v1() != null) {
            this.priority = 1 << 8;
        }
        data = parseTextWithPattern(data.v2(), versionPattern);
        if (data.v1() != null) {
            this.version = Integer.parseInt(data.v1().replace("V", ""));
            this.priority = version;
        }
        data = parseTextWithPattern(data.v2(), sessionType);
        if (data.v1() != null && !transcript.getSessionType().startsWith("E")) {
            addMismatch("Session type", transcript.getSessionType(), "Extraordinary Session");
        }
    }

    public String getMismatches() {
        return mismatches;
    }

    private void addMismatch(String type, String fromTranscript, String fromFilename) {
        this.mismatches += "\n" + String.join("\t", type, fromTranscript, fromFilename);
    }

    /**
     * Removes a matched pattern from the input.
     * @return (matched text, remaining text)
     */
    private static Pair<String> parseTextWithPattern(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            return new Pair<>(null, text);
        }
        String result = matcher.group();
        return new Pair<>(result, text.replace(result, ""));
    }
}
