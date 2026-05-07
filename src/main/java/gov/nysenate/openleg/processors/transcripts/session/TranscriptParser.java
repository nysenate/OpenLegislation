package gov.nysenate.openleg.processors.transcripts.session;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeMap;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillId;
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

@SuppressWarnings("UnstableApiUsage")
public final class TranscriptParser {
    private static final Charset CP_850 = Charset.forName("CP850"),
            CP_1252 = Charset.forName("CP1252");
    // The maximum number of lines of relevant data.
    private static final int MAX_DATA_LENGTH = 4;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:][ ]mm a");
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive().appendPattern("MMMM d[ ][,][ ]yyyy").toFormatter();

    private static final String WORD_SEP = "(?: +|(?: *\\v* *\\d+\\s+)*)";
    private static final Pattern BILL_PATTERNS = Pattern.compile((
            "\\bSenate (?:Print|Bill) (?:Number )?(\\d+[A-Z]?)" +
                    "|\\bAssembly (?:Print|Bill) (?:Number )?(\\d+[A-Z]?)" +
                    "|\\bSenate Resolution (?:Number )?(\\d+)" +
                    "|\\bResolution (?:Number )?(\\d+)" +
                    "|\\bSenate Concurrent Resolution (?:Number )?(\\d+)" +
                    "|\\bAssembly Concurrent Resolution (?:Number )?(\\d+)").replace(" ", WORD_SEP),
            Pattern.CASE_INSENSITIVE);
    private static final Pattern LINE_PAGE_BREAK_SEP = Pattern.compile(
            " *(?:\\v *(?:\\d+\\s+)*)+");

    private TranscriptParser() {}

    private static String getBillIdStr(MatchResult m) {
        // Senate and Assembly Bills may need to append amendment
        if (m.group(1) != null) return "S" + m.group(1);
        if (m.group(2) != null) return "A" + m.group(2);
        if (m.group(3) != null) return "R" + m.group(3);
        if (m.group(4) != null) return "J" + m.group(4);
        if (m.group(5) != null) return "B" + m.group(5);
        return "C" + m.group(6); // implicit m.group(6) != null
    }

    /**
     * Builds a map from each line's character range to its Position (i.e. page and line number).
     */
    private static TreeRangeMap<Integer, Position> buildPositionIndex(String text) {
        var entries = TranscriptLine.prepareLinesWithOffsets(text);
        TreeRangeMap<Integer, Position> positionIndex = TreeRangeMap.create();

        if (entries.isEmpty()) return positionIndex;

        int pageNum = entries.get(0).getValue().getStartingInt();
        Position lastPosition = null;
        for (int i = 0; i < entries.size(); i++) {
            var curr = entries.get(i);
            var next = i + 1 < entries.size() ? entries.get(i + 1) : null;
            TranscriptLine nextLine = next != null ? next.getValue() : null;
            int rangeEnd = next != null ? next.getKey() : text.length();
            if (TranscriptLine.isNextPageNumber(curr.getValue(), nextLine, pageNum, true)) {
                pageNum = curr.getValue().getStartingInt();
            }
            else {
                if (curr.getValue().getStartingInt() != null) {
                    lastPosition = new Position(pageNum, curr.getValue().getStartingInt());
                }
                if (lastPosition != null) {
                    positionIndex.put(Range.closedOpen(curr.getKey(), rangeEnd), lastPosition);
                }
            }
        }
        return positionIndex;
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

            // parse for bill/resolution data and insert links into text
            int sessionYear = (new SessionYear(dateTime.getYear())).year();
            TreeRangeMap<Integer, Position> positionIndex = buildPositionIndex(transcriptText);
            List<BillMention> billMentions = new ArrayList<>();
            // TODO: Can be done better in Java 20 with named groups in MatchResult.
            //  Just name the number groups in the (\\d) group! Can remove lots of ?: too
            String textWithLinks = BILL_PATTERNS.matcher(transcriptText).replaceAll(match -> {
                BillId billId = new BillId(getBillIdStr(match), sessionYear);
                BillMention billMention = new BillMention(billId, positionIndex.get(match.start()));
                billMentions.add(billMention);
                String tagStart = billMention.getTagStart();
                // Line and page breaks should not have overlaying links.
                return tagStart + match.group(0).replaceAll(LINE_PAGE_BREAK_SEP.pattern(), "</a>$0" + tagStart) + "</a>";
            });

            return new Transcript(transcriptId, dayType, transcriptFile.getFileName(), data.get(0), textWithLinks, billMentions);
        }
        catch (RuntimeException ex) {
            throw new ParseError("Problem parsing " + transcriptFile.getFileName(), ex);
        }
    }
}
