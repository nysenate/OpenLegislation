package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.processors.ParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TranscriptParser {
    private final static String BAD_CHARACTER = Character.toString(160);

    @Autowired
    private TranscriptDataService transcriptDataService;

    public void process(TranscriptFile transcriptFile) throws IOException {
        Transcript processed = getTranscriptFromFile(transcriptFile);
        transcriptFile.setTranscript(processed);
        transcriptDataService.saveTranscript(processed, true);
    }

    protected static Transcript getTranscriptFromFile(TranscriptFile transcriptFile) throws IOException {
        List<String> lines = Files.readAllLines(transcriptFile.getFile().toPath());
        LocalDate date = null;
        LocalTime time = null;
        String sessionType = null, location = null;

        // Some transcripts start with 3 incorrect lines that should be skipped.
        int index = new TranscriptLine(lines.get(0)).getSession().isPresent() ? 3 : 0;
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
        String transcriptText = getTranscriptText(lines, date.getYear());
        return new Transcript(transcriptId, transcriptFile.getFileName(), sessionType, location, transcriptText);
    }

    private static String getTranscriptText(List<String> lines, int year) {
        boolean needsTextCorrection = year >= 2003 && year <= 2005;
        var sb = new StringBuilder();
        for (String line : lines) {
            if (needsTextCorrection)
                line = line.replaceAll(BAD_CHARACTER, "á");
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
