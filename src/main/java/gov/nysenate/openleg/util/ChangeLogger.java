package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.util.Storage.Status;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ChangeLogger
{
    private static final Logger logger = Logger.getLogger(ChangeLogger.class);
    private static HashMap<String, Change> changeLog = new HashMap<String, Change>();
    private static File sourceFile;
    private static Date datetime;

    public void clearLog()
    {
        ChangeLogger.changeLog.clear();
    }

    public static HashMap<String, Storage.Status> parseChanges(Iterable<String> lines)
    {
        Pattern changePattern = Pattern.compile("\\s*(.*?)\\s+(NEW|DELETED|MODIFIED)");
        HashMap<String, Storage.Status> changes = new HashMap<String, Storage.Status>();
        for (String line : lines) {
            if (line.isEmpty() || line.matches("\\s*#")) {
                continue;
            }
            Matcher changeLine = changePattern.matcher(line);
            if (changeLine.find()) {
                changes.put(changeLine.group(1), Storage.Status.valueOf(changeLine.group(2).toUpperCase()));
            } else {
                logger.fatal("Malformed change line: "+line);
                System.exit(0);
            }
        }
        return changes;
    }

    public static void record(String key, Storage storage)
    {
        Change change = changeLog.get(key);
        if (change == null) {
            // If change is not in changeLog but json exists it is not new.
            if (storage.storageFile(key).exists()) {
                changeLog.put(key, new Change(Status.MODIFIED));
            } else {
                changeLog.put(key, new Change(Status.NEW));
            }
        } else if (change.getStatus() != Status.NEW) {
            changeLog.put(key, new Change(Status.MODIFIED));
        }
    }

    public static void delete(String key, Storage storage)
    {
        Change change = changeLog.get(key);
        if (change != null) {
            // Already a change to this key waiting to be pushed to services.
            if (change.getStatus() == Status.NEW) {
                // If new, just remove it.
                changeLog.remove(key);
            } else if (change.getStatus() == Status.MODIFIED){
                // Can't process a Modification since its file has been deleted.
                change.setStatus(Status.DELETED);
            }
        } else {
            changeLog.put(key, new Change(Status.DELETED));
        }
    }

    public static void setContext(File sourceFile, Date datetime)
    {
        ChangeLogger.sourceFile = sourceFile;
        ChangeLogger.datetime = datetime;
    }

    public static HashMap<String, Change> getChangeLog()
    {
        return changeLog;
    }
}
