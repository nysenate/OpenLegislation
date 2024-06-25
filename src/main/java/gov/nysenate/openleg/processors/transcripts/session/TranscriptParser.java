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

final class TranscriptParser {
    private static final Charset CP_1252 = Charsets.toCharset("CP1252");
    // The maximum number of lines of relevant data.
    private static final int MAX_DATA_LENGTH = 4;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:][ ]mm a");
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("MMMM d[ ][,][ ]yyyy").toFormatter();

    private TranscriptParser() {}

    static Transcript parse(TranscriptFile transcriptFile) throws IOException {
        var scanner = new Scanner(transcriptFile.getFile(), CP_1252);
        List<String> data = new ArrayList<>(MAX_DATA_LENGTH);
        String location = null;
        while (scanner.hasNextLine() && data.size() < MAX_DATA_LENGTH) {
            String line = new TranscriptLine(scanner.nextLine()).removeLineNumber().trim();
            // Skips lines before the location, and lines without data.
            if (location != null && line.matches("(?i).*[a-z]+.*")) {
                data.add(line);
            }
            String temp = line.replaceAll("\\s+", " ");
            // Relevant data appears after the location label.
            if (temp.matches(("(?i)ALBANY[ ,]*NEW YORK"))) {
                location = temp;
            }
        }
        scanner.close();
        try {
            String tempDate = data.get(0).replaceAll(" +", " ").replaceFirst("\\.$", "");
            String tempTime = data.get(1).replace(".", "").replace("Noon", "pm").toUpperCase();
            LocalDate date = LocalDate.parse(tempDate, DATE_FORMATTER);
            LocalTime time = LocalTime.parse(tempTime, TIME_FORMATTER);
            String transcriptText = Files.readString(transcriptFile.getFile().toPath(), CP_1252);
            DayType dayType = DayType.from(transcriptText);
            TranscriptId transcriptId = new TranscriptId(LocalDateTime.of(date, time), new SessionType(data.get(2)));
            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), location, transcriptText);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
