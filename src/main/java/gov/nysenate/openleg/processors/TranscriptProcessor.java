package gov.nysenate.openleg.processors;

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

    public SimpleDateFormat TRANSCRIPT_DATE_PARSER = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa");

    public TranscriptProcessor() {
        this.logger = Logger.getLogger(this.getClass());
    }

    public void process(File file, Storage storage) throws IOException {
        Transcript transcript = new Transcript();
        StringBuffer fullText = new StringBuffer();
        StringBuffer fullTextProcessed = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "latin1"));
        String line = null;
        String date = null;
        String time = null;
        Integer contentLineNumber = 0;
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() > 4) {
                contentLineNumber += 1;
                String content = line.trim().substring(2).trim();

                switch (contentLineNumber) {
                case 3: transcript.setLocation(content); break;
                case 4: date = content; break;
                case 5: time = content.replace(".", ""); break;
                case 6: transcript.setType(content); break;
                case 1: // NEW YORK STATE SENATE
                case 2: // THE STENOGRAPHIC RECORD, sometimes split on 2 lines
                    if (content.equals("THE")) contentLineNumber--;
                default: break;
                }
                fullTextProcessed.append(line.substring(2).trim()).append("\n");
            }
            fullText.append(line).append("\n");
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
