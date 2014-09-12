package gov.nysenate.openleg.processor.transcript;

import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.service.transcript.TranscriptDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class TranscriptParser
{
    private static final Logger logger = LoggerFactory.getLogger(TranscriptParser.class);

    @Autowired
    private TranscriptDataService transcriptDataService;

    public void process(TranscriptFile transcriptFile) throws IOException {
        String sessionType = null;
        String location = null;
        String date = null;
        String time = null;
        int numSkipped = 0;


        StringBuffer transcriptText = new StringBuffer();

        boolean firstPageParsed = false;
        boolean firstLineParsed = false;
        boolean skipFirstThreeLines = false;

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(transcriptFile.getFile()), "latin1"));
        String lineText;

        while ((lineText = reader.readLine()) != null) {
            TranscriptLine line = new TranscriptLine(lineText);

            if (!firstPageParsed) {
                // Handle transcripts with 3 incorrect lines at start of transcript.
                if (!firstLineParsed) {
                    if (lineText.contains("SESSION")) {
                        skipFirstThreeLines = true;
                        numSkipped = 1;
                        continue;
                    }
                }
                if (skipFirstThreeLines == true && numSkipped <= 3) {
                    numSkipped++;
                    continue;
                }

                if (line.isLocation())
                    location = line.getLocation();

                if (line.isDate())
                    date = line.getDateString();

                if (line.isTime())
                    time = line.getTimeString();

                if (line.isSession())
                    sessionType = line.removeLineNumber().trim();

                firstPageParsed = areWeDoneWithFirstPage(sessionType, location, date, time);
            }

            firstLineParsed = true;
            transcriptText.append(line.fullText()).append("\n");
        }

        reader.close();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d yyyy hmma");
        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, dtf);

        TranscriptId transcriptId = new TranscriptId(sessionType, dateTime);
        Transcript transcript = new Transcript(transcriptId);
        transcript.setLocation(location);
        transcript.setTranscriptText(transcriptText.toString());

        transcriptDataService.saveTranscript(transcript, transcriptFile);
    }

    private boolean areWeDoneWithFirstPage(String sessionType, String location, String date, String time) {
        return sessionType != null && location != null && date != null && time !=null;
    }

}
