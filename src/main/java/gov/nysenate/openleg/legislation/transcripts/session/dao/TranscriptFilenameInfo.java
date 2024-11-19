package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.util.Pair;
import gov.nysenate.openleg.legislation.transcripts.session.DayType;
import gov.nysenate.openleg.legislation.transcripts.session.SessionType;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranscriptFilenameInfo {
    private static final int NUM_FIELDS = 6;
    private static final Pattern datePattern = Pattern.compile("\\d{6}"), dayTypePattern = Pattern.compile("SENATE(LD)?"),
            correctedPattern = Pattern.compile("CORRECTED"), fixedPattern = Pattern.compile("FIXED$"),
            versionPattern = Pattern.compile("V\\d+"), sessionTypePattern = Pattern.compile("E");
    private final Map<Class<?>, Pair<String>> mismatches = new HashMap<>();
    // Used to determine which transcripts are preferred.
    private final int[] priority = new int[NUM_FIELDS];

    public TranscriptFilenameInfo(Transcript transcript) {
        this(transcript.getFilename(), transcript.getDateTime().toLocalDate(),
                transcript.getDayType(), transcript.getSessionType());
    }

    public TranscriptFilenameInfo(String filename, LocalDate date, DayType dayType, @Nonnull String sessionType) {
        final String cleanFilename = filename.toUpperCase().replaceAll("\\.(TXT)?", "");
        Pair<String> data = parseTextWithPattern(cleanFilename, versionPattern);
        if (data.v1() != null) {
            priority[0] = Integer.parseInt(data.v1().replace("V", ""));
        }
        data = parseTextWithPattern(data.v2(), correctedPattern);
        if (data.v1() != null) {
            priority[1] = 1;
        }
        data = parseTextWithPattern(data.v2(), fixedPattern);
        if (data.v1() != null) {
            priority[2] = 1;
        }
        data = parseTextWithPattern(data.v2(), datePattern);
        String dateStr = String.format("%02d%02d%02d",
                date.getMonthValue(), date.getDayOfMonth(), date.getYear()%100);
        if (!dateStr.equals(data.v1())) {
            addMismatch(LocalDate.class, data.v1(), dateStr);
            priority[3] = -1;
        }
        data = parseTextWithPattern(data.v2(), dayTypePattern);
        // Many filenames lack DayType information, which is fine.
        if (data.v1() != null) {
            DayType filenameDayType = data.v1().endsWith("LD") ? DayType.LEGISLATIVE : DayType.SESSION;
            if (filenameDayType != dayType) {
                addMismatch(DayType.class, filenameDayType.toString(), String.valueOf(dayType));
                priority[4] = -1;
            }
        }
        data = parseTextWithPattern(data.v2(), sessionTypePattern);
        if (data.v1() != null && !sessionType.startsWith("E")) {
            addMismatch(SessionType.class, "An Extraordinary Session", sessionType);
            priority[5] = -1;
        }
    }

    public boolean isLessAccurateThan(@Nonnull TranscriptFilenameInfo oldInfo) {
        for (int i = 0; i < NUM_FIELDS; i++) {
            if (priority[i] != oldInfo.priority[i]) {
                return priority[i] < oldInfo.priority[i];
            }
        }
        return false;
    }

    public Optional<String> getMismatches() {
        if (mismatches.isEmpty()) {
            return Optional.empty();
        }
        var result = new StringBuilder();
        result.append("\nType\t\tExpected\t\tActual\n");
        for (var entry : mismatches.entrySet()) {
            result.append("%s\t\t%s\t\t%s%n".formatted(entry.getKey().getSimpleName(), entry.getValue().v1(), entry.getValue().v2()));
        }
        return Optional.of(result.toString());
    }

    private void addMismatch(Class<?> type, String expected, String actual) {
        mismatches.put(type, new Pair<>(expected, actual));
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
