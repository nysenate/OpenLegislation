package gov.nysenate.openleg.util;

import gov.nysenate.openleg.util.Storage.Status;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ChangeLogger
{
    private final Logger logger = Logger.getLogger(ChangeLogger.class);
    private static HashMap<String, Storage.Status> changeLog = new HashMap<String, Storage.Status>();

    private static File sourceFile;
    private static Date datetime;

    public void clearLog()
    {
        ChangeLogger.changeLog.clear();
    }

    public HashMap<String, Storage.Status> parseChanges(Iterable<String> lines)
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
        Status keyStatus = changeLog.get(key);
        if (keyStatus == null) {
            if (storage.storageFile(key).exists()) {
                changeLog.put(key, Status.MODIFIED);
            } else {
                changeLog.put(key, Status.NEW);
            }
        } else if (keyStatus != Status.NEW) {
            changeLog.put(key, Status.MODIFIED);
        }
    }

    public static void delete(String key, Storage storage)
    {
        Status keyStatus = changeLog.get(key);
        if (keyStatus == Status.NEW) {
            changeLog.remove(key);
        } else {
            changeLog.put(key, Status.DELETED);
        }
    }

    public static void setContext(File sourceFile, Date datetime)
    {
        ChangeLogger.sourceFile = sourceFile;
        ChangeLogger.datetime = datetime;
    }

    public static HashMap<String, Storage.Status> getChangeLog()
    {
        return changeLog;
    }
}
