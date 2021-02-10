package gov.nysenate.openleg.processors.transcripts.session;

import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.dao.TranscriptDataService;
import gov.nysenate.openleg.processors.ParseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TranscriptParser {
    private static final Charset TRANSCRIPT_CHARSET = Charset.forName("latin1");

    @Autowired
    private TranscriptDataService transcriptDataService;

    public void process(TranscriptFile transcriptFile) throws IOException {
        Transcript processed = getTranscriptFromFile(transcriptFile);
        transcriptFile.setTranscript(processed);
        transcriptDataService.saveTranscript(processed, true);
    }

    protected static Transcript getTranscriptFromFile(TranscriptFile transcriptFile) throws IOException {
        StringBuilder transcriptText = new StringBuilder();
        List<String> lines = Files.readAllLines(transcriptFile.getFile().toPath(), TRANSCRIPT_CHARSET);

        LocalDate date = null;
        LocalTime time = null;
        String sessionType = null, location = null;

        for (int i = 0; i < lines.size(); i++) {
            TranscriptLine line = new TranscriptLine(lines.get(i));
            // Some transcripts start with 3 incorrect lines that should be skipped.
            if (transcriptText.length() == 0 && line.isSession()) {
                i += 2;
                continue;
            }

            if (!doneWithFirstPage(sessionType, location, date, time)) {
                date = line.getDate().orElse(date);
                time = line.getTime().orElse(time);
                location = line.getLocation().orElse(location);
                if (line.isSession())
                    sessionType = line.removeLineNumber().trim();
            }
            transcriptText.append(line.getText()).append("\n");
        }

        if (date == null || time == null)
            throw new ParseError("Date or time could not be parsed from TranscriptFile " + transcriptFile.getOriginalFilename());
        TranscriptId transcriptId = new TranscriptId(LocalDateTime.of(date, time));
        return new Transcript(transcriptId, transcriptFile.getFileName(), sessionType, location, transcriptText.toString());
    }

    private static boolean doneWithFirstPage(String sessionType, String location, LocalDate date, LocalTime time) {
        return sessionType != null && location != null && date != null && time != null;
    }
}
