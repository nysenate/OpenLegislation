package gov.nysenate.openleg.util;

import gov.nysenate.openleg.util.Storage.Status;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    
    /**
     * Creates a hash map of changes from a change log file.
     * Stores more detailed information than the parseChanges method.
     * Currently adds date information, more to come.
     * <p>
     * This is done separate from the parseChanges method since the main services, varnish and lucene, don't 
     * need this extra data and would require changes to their interfaces.
     * @param lines
     * @return
     */
    public static HashMap<String, Change> parseChangesDetailed(Iterable<String> lines)
    {
        // TODO: errors when no date information in change log.
        Pattern changePattern = Pattern.compile("\\s*(.*?)\\s+(NEW|DELETED|MODIFIED)\\s+(.*)");
        HashMap<String, Change> changes = new HashMap<String, Change>();
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String line : lines) {
            if (line.isEmpty() || line.matches("\\s*#")) {
                continue;
            }
            Matcher changeLine = changePattern.matcher(line);
            if (changeLine.find()) {
                Date date = null;
                try {
                    date = sdf.parse(changeLine.group(3));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                changes.put(changeLine.group(1), new Change(Storage.Status.valueOf(changeLine.group(2).toUpperCase()), date));
            } else {
                logger.fatal("Malformed change line: "+line);
                System.exit(0);
            }
        }
        return changes;
    }

    public static void record(String key, Storage storage)
    {
        record(key, storage, null);
    }

    /**
     * Appends change information to the changeLog
     * 
     * @param key
     * @param storage
     * @param date
     * @param block
     */
    public static void record(String key, Storage storage, Date date)
    {
        Change change = changeLog.get(key);
        if (change == null) {
            if (storage.storageFile(key).exists()) {
                // A json for this key already exists, it's not new.
                changeLog.put(key, new Change(Status.MODIFIED, date));
            } else {
                changeLog.put(key, new Change(Status.NEW, date));
            }
        } else if (change.getStatus() != Status.NEW) {
            changeLog.put(key, new Change(Status.MODIFIED, date));
        }
    }
    
    public static void delete(String key, Storage storage)
    {
        delete(key, storage, null);
    }

    public static void delete(String key, Storage storage, Date date)
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
            changeLog.put(key, new Change(Status.DELETED, date));
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
