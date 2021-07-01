package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.processors.ParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TranscriptParser {
    @Autowired
    private TranscriptDataService transcriptDataService;

    public void process(TranscriptFile transcriptFile) throws IOException {
        String fullText = Files.readString(transcriptFile.getFile().toPath(), StandardCharsets.ISO_8859_1);
        Files.writeString(transcriptFile.getFile().toPath(), fullText, StandardOpenOption.TRUNCATE_EXISTING);
        Transcript processed = getTranscriptFromFile(transcriptFile);
        transcriptFile.setTranscript(processed);
        transcriptDataService.saveTranscript(processed, true);
    }

    protected static Transcript getTranscriptFromFile(TranscriptFile transcriptFile) throws IOException {
        List<String> lines = Files.readAllLines(transcriptFile.getFile().toPath());
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
