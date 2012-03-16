package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.ingest.Ingest.IngestType;
import gov.nysenate.openleg.ingest.parser.AgendaParser;
import gov.nysenate.openleg.ingest.parser.BillParser;
import gov.nysenate.openleg.ingest.parser.CalendarParser;
import gov.nysenate.openleg.ingest.parser.SenateParser;
import gov.nysenate.openleg.ingest.parser.TranscriptParser;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.TextFormatter;
import gov.nysenate.openleg.util.Timer;
import gov.nysenate.openleg.util.serialize.XmlHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * writes JSON from SOBI files
 * 
 * Any time JSON is written is should be assumed that the index
 * must also be updated.  The current mechanism to track files that
 * need to be updated is to log the absolute path.  When IngestIndexWriter.indexBulk()
 * is executed it will read the contents, load and index the objects.
 * 
 */

public class IngestJsonWriter {
    private final long THE_TIME = new Date().getTime();

    private final Logger logger = Logger.getLogger(IngestJsonWriter.class);

    AgendaParser agendaParser;
    BillParser billParser;
    CalendarParser calendarParser;
    TranscriptParser transcriptParser;

    JsonDao ingestJson;
    SearchEngine searchEngine;

    Timer timer;

    ArrayList<SenateObject> senateObjects;

    public IngestJsonWriter(JsonDao ingestJson, SearchEngine searchEngine) {
        this.ingestJson = ingestJson;
        this.searchEngine = searchEngine;

        agendaParser = new AgendaParser(ingestJson, searchEngine);
        billParser = new BillParser();
        calendarParser = new CalendarParser(ingestJson, searchEngine);
        transcriptParser = new TranscriptParser();

        timer = new gov.nysenate.openleg.util.Timer();

        senateObjects = new ArrayList<SenateObject>();
    }

    public void writeJsonFromDirectory(String sobiDirectory) {
        writeJsonFromDirectory(new File(sobiDirectory));
    }

    private void writeJsonFromDirectory(File sobiDirectory) {
        if(sobiDirectory.exists()) {
            if(sobiDirectory.isDirectory()) {
                File[] files = sortFilesByName(sobiDirectory.listFiles());

                Arrays.sort(files);

                for(File file:files) {
                    writeJsonFromDirectory(file);
                }
            }
            else {
                writeJson(sobiDirectory);
            }
        }
    }

    private void writeJson(File sobiFile) {
        if(sobiFile.getName().contains("-calendar-")) {
            XmlHelper.fixCalendar(sobiFile);
        }

        IngestType ingestType = null;
        if(sobiFile.getName().endsWith(".TXT")) {
            ingestType = IngestType.BILL;
        } else if(sobiFile.getName().contains("-calendar-")) {
            ingestType = IngestType.CALENDAR;
        } else if(sobiFile.getName().contains("-agenda-")){
            ingestType = IngestType.AGENDA;
        } else {
            return;
        }

        writeJson(sobiFile, ingestType);
    }

    public void writeJson(File sobiFile, IngestType ingestType) {
        logger.info("Reading file: " + sobiFile.getName());

        SenateParser<? extends SenateObject> senateParser = null;

        timer.start();

        switch(ingestType) {
        case BILL: senateParser = billParser; break;
        case CALENDAR: senateParser = calendarParser; break;
        case AGENDA: senateParser = agendaParser; break;
        case TRANSCRIPT: senateParser = transcriptParser; break;
        }
        if(senateParser == null) return;

        senateParser.parse(sobiFile);
        senateObjects.addAll(senateParser.getNewSenateObjects());

        logger.info(timer.stop() + " - Processed Objects");

        if(senateObjects != null) {
            timer.start();

            SenateObject sObj;
            for(int i = 0; i < senateObjects.size(); i++) {
                sObj = ingestJson.mergeSenateObject(
                        senateObjects.get(i), senateObjects.get(i).getClass());

                sObj.addSobiReference(sobiFile.getName());
                sObj.setModified(getDateFromFileName(sobiFile.getName()));

                ingestJson.write(sObj);
            }

            logger.info(TextFormatter.append(timer.stop()," - Wrote ",senateObjects.size()," Objects"));
        }

        for(SenateObject sObj:senateParser.getDeletedSenateObjects()) {
            ingestJson.delete(sObj);
        }

        senateParser.clear();
        senateObjects.clear();
    }

    private long getDateFromFileName(String fileName) {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();

            fileName = fileName.replaceAll("(SOBI\\.D|\\.TXT.*$)", "");

            if(fileName.length() == 14) {
                cal.set(Integer.parseInt(fileName.substring(0,2)) + 2000,
                        Integer.parseInt(fileName.substring(2,4))-1,
                        Integer.parseInt(fileName.substring(4,6)),
                        Integer.parseInt(fileName.substring(8,10)),
                        Integer.parseInt(fileName.substring(10,12)),
                        Integer.parseInt(fileName.substring(12,14)));
            }

            return cal.getTimeInMillis();
        }
        catch (Exception e) {
            logger.error(e);
        }
        return THE_TIME;
    }

    public void writeTranscriptsFromDirectory(String transcriptDirectory) {
        writeTranscriptsFromDirectory(new File(transcriptDirectory));
    }

    private void writeTranscriptsFromDirectory(File transcriptDirectory) {
        if(transcriptDirectory.exists()) {
            if(transcriptDirectory.isDirectory()) {
                File[] files = sortFilesByName(transcriptDirectory.listFiles());

                Arrays.sort(files);

                for(File file:files) {
                    writeTranscriptsFromDirectory(file);
                }
            }
            else {
                if(transcriptDirectory.getName().matches("(?i)^.+\\.txt$")) {
                    writeJson(transcriptDirectory, IngestType.TRANSCRIPT);
                }
            }
        }
    }

    private File[] sortFilesByName(File[] fList) {
        Arrays.sort(fList, new Comparator<File>() {
            @Override
            public int compare(File one, File two) {
                return one.getName().compareTo(two.getName());
            }
        });

        return fList;
    }
}
