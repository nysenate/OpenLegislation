package gov.nysenate.openleg.ingest.parser;

import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.EasyReader;
import gov.nysenate.openleg.util.OpenLegConstants;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

public class TranscriptParser extends SenateParser<Transcript> implements OpenLegConstants {
    public TranscriptParser() {
        super(TranscriptParser.class);
    }

    @Override
    public void parse(File file) {
        EasyReader er = new EasyReader(file).open();

        Transcript transcript = parseFile(er);

        if(transcript == null) {
            logger.warn("There was an issue parsing the transcript from file " + file.getAbsolutePath());
        }
        else {
            this.addNewSenateObject(transcript);
        }

        er.close();
    }

    private Transcript parseFile (EasyReader reader) {
        Transcript transcript = new Transcript();
        StringBuffer fullText = new StringBuffer();
        StringBuffer fullTextProcessed = new StringBuffer();

        String pLine = null;
        int locationLineIdx = 9;
        boolean checkedLineFour = false;

        String line = null;
        while ((line = reader.readLine()) != null) {
            pLine = line.trim();

            if (pLine.startsWith("4") && (!checkedLineFour)) {
                if (pLine.indexOf("STENOGRAPHIC RECORD")==-1)
                    locationLineIdx = 10;

                checkedLineFour = true;
            }
            else if (transcript.getLocation() == null && pLine.startsWith(locationLineIdx+" "))	{
                pLine = pLine.trim();

                if (pLine.length() < 3)
                    locationLineIdx++; //location must be on the next line
                else {
                    //9                   ALBANY, NEW YORK
                    pLine = pLine.substring(2).trim();

                    transcript.setLocation(pLine);
                    logger.info("got location: " + transcript.getLocation());
                }
            }
            else if (transcript.getTimeStamp() == null && pLine.startsWith((locationLineIdx+1)+" ")) {
                // 11                    August 7, 2009
                //  12                      10:00 a.m.
                pLine = pLine.substring(2).trim();

                logger.info("got day: " + pLine);

                String nextLine = reader.readLine();
                nextLine = reader.readLine().trim();
                nextLine = nextLine.substring(2).trim();

                logger.info("got time: " + nextLine);

                pLine += ' ' + nextLine;
                pLine = pLine.replace(".", "");

                try {
                    Date tTime = TRANSCRIPT_DATE_PARSER.parse(pLine);
                    transcript.setTimeStamp(tTime);
                } catch (ParseException e) {
                    logger.warn("unable to parse transcript datetime" + pLine,e);
                }
            }
            else if (transcript.getType() == null && pLine.startsWith((locationLineIdx+5)+" "))	{
                // 15                    REGULAR SESSION
                pLine = pLine.substring(2);
                pLine = pLine.trim();

                transcript.setType(pLine);
            }

            fullText.append(line);
            fullText.append('\n');

            line = line.trim();

            if (line.length() > 2) {
                line = line.substring(2);
                fullTextProcessed.append(line);
                fullTextProcessed.append('\n');
            }
        }
        transcript.setTranscriptText(fullText.toString());
        transcript.setTranscriptTextProcessed(fullTextProcessed.toString());

        return transcript;
    }
}
