package gov.nysenate.openleg.legislation.transcripts.session;

import java.util.Set;
import java.util.stream.Collectors;

public enum DayType {
    LEGISLATIVE, SESSION;

    public static DayType from(String transcriptText) {
        Set<String> speakers = transcriptText.lines()
                .map(line -> line.replaceFirst("\\d{1,2}:\\d{1,2}|The order of business:", ""))
                // Get lines with a speaker
                .filter(line -> line.contains(":"))
                // Isolate speaker names
                .map(line -> line.replaceAll("^ *\\d* *|:.*", "").toUpperCase())
                .collect(Collectors.toSet());
        // Legislative days only have the Secretary and President speaking.
        return switch (speakers.size()) {
            case 0, 1 -> null;
            case 2 -> LEGISLATIVE;
            default -> SESSION;
        };
    }
}
