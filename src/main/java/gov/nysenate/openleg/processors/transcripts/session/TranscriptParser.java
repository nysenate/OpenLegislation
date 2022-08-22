package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.processors.ParseError;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class TranscriptParser {
    private static final Charset CP_1252 = Charsets.toCharset("CP1252");
    private static final int MAX_PAGE_LENGTH = 10;
    private TranscriptParser() {}

    static Transcript process(TranscriptFile transcriptFile) throws IOException {
        var scanner = new Scanner(transcriptFile.getFile(), CP_1252);
        List<TranscriptLine> firstPage = new ArrayList<>(MAX_PAGE_LENGTH);
        while (scanner.hasNextLine() && firstPage.size() < MAX_PAGE_LENGTH) {
            var line = new TranscriptLine(scanner.nextLine());
            if (!line.text().replaceFirst("\\d+", "").isBlank()) {
                firstPage.add(line);
            }
        }
        scanner.close();
        firstPage = firstPage.stream().dropWhile(line -> line.getLocation().isEmpty()).toList();
        try {
            String location = firstPage.get(0).getLocation().get();
            LocalDate date = firstPage.get(1).getDate().get();
            LocalTime time = firstPage.get(2).getTime().get();
            String sessionType = firstPage.get(3).getSession().get();
            TranscriptId transcriptId = new TranscriptId(LocalDateTime.of(date, time), sessionType);
            String transcriptText = Files.readString(transcriptFile.getFile().toPath(), CP_1252);
            return new Transcript(transcriptId, transcriptFile.getFileName(), location, transcriptText);
        }
        catch (ArrayIndexOutOfBoundsException | NoSuchElementException e) {
            throw new ParseError("Trouble parsing " + transcriptFile.getFileName());
        }
    }
}
