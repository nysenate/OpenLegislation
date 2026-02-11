package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
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
import java.util.*;
import java.util.regex.Pattern;

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

            // We don't have bill data before 2009
            if (dateTime.getYear() < 2009) {
                return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), transcriptText);
            }

            // include parsed bills in transcript data
            String input = transcriptText.replaceAll("\\d*\\s{2,}", " ");
            SessionYear sessionYear = new SessionYear(dateTime.getYear());
            LinkedHashSet<BaseBillId> billIds = new LinkedHashSet<>();

            // Parse for Senate Bills
            Pattern.compile("Senate (?:Print )?(?:Bill )?(?:Number )?(\\d+)\\w?", Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                    .results()
                    .map(m -> "S" + m.group(1))
                    .forEach(m -> billIds.add(new BaseBillId(m, sessionYear)));
            // Parse for Assembly Bills
            Pattern.compile("Assembly (?:Print )?(?:Bill )?(?:Number )?(\\d+)\\w?", Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                    .results()
                    .map(m -> "A" + m.group(1))
                    .forEach(m -> billIds.add(new BaseBillId(m, sessionYear)));
            // Parse for Senate Resolutions
            Pattern.compile("Resolution (?:Number )?(\\d+)", Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                    .results()
                    .map(m -> "J" + m.group(1))
                    .forEach(m -> billIds.add(new BaseBillId(m, sessionYear)));

            // Parse for Senate Concurrent Resolutions
            Pattern.compile("Senate Concurrent Resolution (?:Number )?(\\d+)", Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                    .results()
                    .map(m -> "B" + m.group(1))
                    .forEach(m -> billIds.add(new BaseBillId(m, sessionYear)));
            // Parse for Assembly Concurrent Resolutions
            Pattern.compile("Assembly Concurrent Resolution (?:Number )?(\\d+)", Pattern.CASE_INSENSITIVE)
                    .matcher(input)
                    .results()
                    .map(m -> "C" + m.group(1))
                    .forEach(m -> billIds.add(new BaseBillId(m, sessionYear)));

            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), transcriptText, billIds);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
