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
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TranscriptParser {
    private static final Charset CP_850 = Charset.forName("CP850"),
            CP_1252 = Charset.forName("CP1252");
    // The maximum number of lines of relevant data.
    private static final int MAX_DATA_LENGTH = 4;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:][ ]mm a");
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("MMMM d[ ][,][ ]yyyy").toFormatter();

    private static final String WORD_SEP = "(?:\\h+|\\h*\\v\\h*(?:\\d+\\s+)*)+";
    private static final Pattern BILL_PATTERNS = Pattern.compile(
            "\\bSenate" + WORD_SEP + "(?:Print|Bill)" + WORD_SEP + "(?:Number" + WORD_SEP + ")?(\\d+)" +
                    "|\\bAssembly" + WORD_SEP + "(?:Print|Bill)" + WORD_SEP + "(?:Number" + WORD_SEP + ")?(\\d+)" +
                    "|\\bResolution" + WORD_SEP + "(?:Number" + WORD_SEP + ")?(\\d+)" +
                    "|\\bSenate" + WORD_SEP + "Concurrent" + WORD_SEP + "Resolution" + WORD_SEP + "(?:Number" + WORD_SEP + ")?(\\d+)" +
                    "|\\bAssembly" + WORD_SEP + "Concurrent" + WORD_SEP + "Resolution" + WORD_SEP + "(?:Number" + WORD_SEP + ")?(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LINE_PAGE_BREAK_SEP = Pattern.compile(
            "\\h*(?:\\v\\h*(?:\\d+\\s+)*)+");

    private TranscriptParser() {}

    private static String toBillId(MatchResult m) {
        if (m.group(1) != null) return "S" + m.group(1);
        if (m.group(2) != null) return "A" + m.group(2);
        if (m.group(3) != null) return "J" + m.group(3);
        if (m.group(4) != null) return "B" + m.group(4);
        if (m.group(5) != null) return "C" + m.group(5);
        return null;
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
            String textWithLinks = BILL_PATTERNS.matcher(transcriptText).replaceAll(match -> {
                String billId = toBillId(match);
                if (billId == null) return Matcher.quoteReplacement(match.group(0));
                billIds.add(new BaseBillId(billId, sessionYear));

                String href = "/bills/" + sessionYear.year() + "/" + billId;
                String fullMatch = match.group(0);

                String[] segments = LINE_PAGE_BREAK_SEP.split(fullMatch);
                String[] separators = LINE_PAGE_BREAK_SEP.matcher(fullMatch).results()
                        .map(r -> r.group(0))
                        .toArray(String[]::new);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < segments.length; i++) {
                    if (!segments[i].isBlank()) {
                        stringBuilder.append("<a href=\"").append(href).append("\">")
                                .append(segments[i].trim())
                                .append("</a>");
                    }
                    else {
                        stringBuilder.append(segments[i]);
                    }

                    if (i < separators.length) {
                        stringBuilder.append(separators[i]);
                    }
                }
                return Matcher.quoteReplacement(stringBuilder.toString());
            });

            System.out.println(textWithLinks);
            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), textWithLinks, billIds);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
