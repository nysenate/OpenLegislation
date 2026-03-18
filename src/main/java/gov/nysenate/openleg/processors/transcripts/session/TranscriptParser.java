package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.transcripts.session.*;
import gov.nysenate.openleg.processors.ParseError;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class TranscriptParser {
    private static final Charset CP_850 = Charset.forName("CP850"),
            CP_1252 = Charset.forName("CP1252");
    // The maximum number of lines of relevant data.
    private static final int MAX_DATA_LENGTH = 4;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:][ ]mm a");
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("MMMM d[ ][,][ ]yyyy").toFormatter();

    private static final String WORD_SEP = "(?: +| *\\v *(?:\\d+\\s+)*)+";
    private static final Pattern BILL_PATTERNS = Pattern.compile((
            "\\bSenate (?:Print|Bill) (?:Number )?(\\d+)" +
                    "|\\bAssembly (?:Print|Bill) (?:Number )?(\\d+)" +
                    "|\\bResolution (?:Number )?(\\d+)" +
                    "|\\bSenate Concurrent Resolution (?:Number )?(\\d+)" +
                    "|\\bAssembly Concurrent Resolution (?:Number )?(\\d+)").replace(" ", WORD_SEP),
            Pattern.CASE_INSENSITIVE);
    private static final Pattern LINE_PAGE_BREAK_SEP = Pattern.compile(
            " *(?:\\v *(?:\\d+\\s+)*)+");

    private TranscriptParser() {}

    private static String toBillId(MatchResult m) {
        if (m.group(1) != null) return "S" + m.group(1);
        if (m.group(2) != null) return "A" + m.group(2);
        if (m.group(3) != null) return "J" + m.group(3);
        if (m.group(4) != null) return "B" + m.group(4);
        return "C" + m.group(5); // implicit m.group(5) != null
    }

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

            // parse for bill, resolution data and insert links into text
            SessionYear sessionYear = new SessionYear(dateTime.getYear());
            LinkedHashSet<BaseBillId> billIds = new LinkedHashSet<>();
            // TODO: Can be done better in Java 20 with named groups in MatchResult.
            //  Just name the number groups in the (\\d) group! Can remove lots of ?: too
            String textWithLinks = BILL_PATTERNS.matcher(transcriptText).replaceAll(match -> {
                String billId = toBillId(match);
                billIds.add(new BaseBillId(billId, sessionYear));
                String tagStart = "<a href=\"/%d/%s\">".formatted(sessionYear.year(), billId);
                // Line and page breaks should not be linked.
                return tagStart + match.group(0).replaceAll(LINE_PAGE_BREAK_SEP.pattern(), "</a>$0" + tagStart) + "</a>";
            });

            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), textWithLinks, billIds);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
