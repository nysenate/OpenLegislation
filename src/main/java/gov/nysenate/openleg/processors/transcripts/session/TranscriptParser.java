package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.processors.ParseError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TranscriptParser {
    private TranscriptParser() {}

    protected static Transcript getTranscriptFromFile(TranscriptFile transcriptFile) throws IOException {
        List<String> lines = Files.readAllLines(transcriptFile.getFile().toPath(), StandardCharsets.ISO_8859_1);
        LocalDate date = null;
        LocalTime time = null;
        String sessionType = null, location = null;

        int index = 0;
        while (index < lines.size() && (date == null || time == null || location == null || sessionType == null)) {
            TranscriptLine line = new TranscriptLine(lines.get(index++));
            date = line.getDate().orElse(date);
            time = line.getTime().orElse(time);
            location = line.getLocation().orElse(location);
            sessionType = line.getSession().orElse(sessionType);
        }

        if (date == null || time == null)
            throw new ParseError("Date or time could not be parsed from TranscriptFile " + transcriptFile.getOriginalFilename());
        TranscriptId transcriptId = new TranscriptId(LocalDateTime.of(date, time));
        String transcriptText = String.join("\n", lines) + "\n";
        return new Transcript(transcriptId, transcriptFile.getFileName(), sessionType, location, transcriptText);
    }
}
