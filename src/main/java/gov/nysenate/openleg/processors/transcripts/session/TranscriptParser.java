package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.*;
import gov.nysenate.openleg.processors.ParseError;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class TranscriptParser {
    private static final Charset CP_850 = Charset.forName("CP850"),
            CP_1252 = Charset.forName("CP1252");
    // The maximum number of lines of relevant data.
    private static final int MAX_DATA_LENGTH = 4;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:][ ]mm a");
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("MMMM d[ ][,][ ]yyyy").toFormatter();

    private TranscriptParser() {}

    public static Transcript parse(TranscriptFile transcriptFile) throws IOException {
        var scanner = new Scanner(transcriptFile.getFile(), CP_850);
        List<String> data = new ArrayList<>(MAX_DATA_LENGTH);
        while (scanner.hasNextLine() && data.size() < MAX_DATA_LENGTH) {
            String line = scanner.nextLine().replaceAll("\\s+", " ")
                    // Remove the line number, if it exists
                    .replaceFirst("^ *\\d+ ", "").trim();
            // Skips lines before the location, and lines without data.
            if (line.matches("(?i)ALBANY[ ,]*NEW YORK") ||
                    (!data.isEmpty() && line.matches(".*[A-Za-z].*"))) {
                data.add(line);
            }
        }
        scanner.close();
        try {
            String dateStr = data.get(1).replaceAll(" +", " ").replaceFirst("\\.$", "");
            String timeStr = data.get(2).replace(".", "").replace("Noon", "pm").toUpperCase();
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            Charset encoding = date.isBefore(Stenographer.KIRKLAND.getStartDate()) ? CP_850 : CP_1252;
            String transcriptText = Files.readString(transcriptFile.getFile().toPath(), encoding);
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(timeStr, TIME_FORMATTER));
            DayType dayType = DayType.from(transcriptText);
            TranscriptId transcriptId = new TranscriptId(dateTime, new SessionType(data.get(3)));
            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), transcriptText);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
