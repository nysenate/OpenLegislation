package gov.nysenate.openleg.processors;

import gov.nysenate.openleg.util.TranscriptLine;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.ChangeLogger;
import gov.nysenate.openleg.util.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class TranscriptProcessor {
    private final Logger logger;

    public SimpleDateFormat TRANSCRIPT_DATE_PARSER = new SimpleDateFormat("MMM dd yyyy hhmmaa");

    public TranscriptProcessor() {
        this.logger = Logger.getLogger(this.getClass());
    }

    public void process(File file, Storage storage) throws IOException {
        Transcript transcript = new Transcript();
        StringBuffer fullText = new StringBuffer();
        StringBuffer fullTextProcessed = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "latin1"));
        String lineText;
        TranscriptLine line;
        String date = null;
        String time = null;
        boolean firstPageParsed = false;

        while ((lineText = reader.readLine()) != null) {
            line = new TranscriptLine(lineText);

            if (!firstPageParsed) {
                if (line.isLocation())
                    transcript.setLocation(line.textTrimmed());

                if (line.isDate())
                    date = line.getDateString();

                if (line.isTime())
                    time = line.getTimeString();

                if (line.isSession())
                    transcript.setType(line.textTrimmed());

                if (transcript.getLocation() != null && date != null && time != null && transcript.getType() != null)
                    firstPageParsed = true;
            }

            fullText.append(line.fullText()).append("\n");

            if (line.textTrimmed().length() > 0) {
                fullTextProcessed.append(line.textTrimmed()).append("\n");
            }
        }

        reader.close();

        try {
            transcript.setTimeStamp(TRANSCRIPT_DATE_PARSER.parse(date+" "+time));
        } catch (ParseException e) {
            logger.error(file.getName()+": unable to parse transcript datetime " + date+" "+time, e);
        }

        transcript.setTranscriptText(fullText.toString());
        transcript.setTranscriptTextProcessed(fullTextProcessed.toString());
        String oid = transcript.getType().replaceAll(" ",  "-")+"-"+new SimpleDateFormat("MM-dd-yyyy_HH:mm").format(transcript.getTimeStamp());
        transcript.setId(oid);
        transcript.setModifiedDate(transcript.getTimeStamp());
        transcript.setPublishDate(transcript.getTimeStamp());
        transcript.addDataSource(file.getName());

        // Save the transcript
        String key = transcript.getYear()+"/transcript/"+transcript.getId();
        storage.set(transcript);

        // Make an entry in the change log
        ChangeLogger.setContext(file, transcript.getTimeStamp());
        ChangeLogger.record(key, storage);
    }
}
