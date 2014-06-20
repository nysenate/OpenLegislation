package gov.nysenate.openleg.util;

import gov.nysenate.openleg.model.Change;
import gov.nysenate.openleg.util.Storage.Status;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ChangeLogger
{
    private static final Logger logger = Logger.getLogger(ChangeLogger.class);
    private static HashMap<String, Change> changeLog = new HashMap<String, Change>();

    public static Pattern keyPattern = Pattern.compile("([0-9]+)/([^/]+)/(.*)");

    public static SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static File sourceFile;
    private static Date datetime;

    public static void clearLog()
    {
        ChangeLogger.changeLog.clear();
    }

    public static List<Entry<String, Change>> getEntries()
    {
        // Use alphabetical ordering for consistency
        List<Entry<String, Change>> entries = new ArrayList<Entry<String, Change>>(changeLog.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, Change>>() {
            public int compare(Entry<String, Change> a, Entry<String, Change> b)
            {
                if (a.getKey() == null) {
                    return -1;
                }
                else {
                    return a.getKey().compareTo(b.getKey());
                }
            }
        });
        return entries;
    }

    public static void writeToFile(File outFile) throws IOException
    {
        StringBuffer out = new StringBuffer();
        for (Entry<String, Change> entry : ChangeLogger.getEntries()) {
            Date date = entry.getValue().getTime();
            out.append(entry.getKey()+"\t"+entry.getValue().getStatus()+"\t"+dateFormat.format(date).toString()+"\n");
        }
        FileUtils.write(outFile,out.toString());
    }

    public static void readFromFile(File inFile) throws IOException
    {
        ChangeLogger.readFromLines(FileUtils.readLines(inFile));
    }

    public static void readFromLines(Iterable<String> lines)
    {
        ChangeLogger.clearLog();
        Pattern changePattern = Pattern.compile("\\s*(.*?)\\s+("+StringUtils.join(Storage.Status.values(), "|")+")\\s+(.*)");
        changeLog = new HashMap<String, Change>();
        for (String line : lines) {
            if (!line.isEmpty() && !line.matches("\\s*#")) {
                Matcher changeLine = changePattern.matcher(line);
                if (changeLine.find()) {
                    try {
                        Date date = dateFormat.parse(changeLine.group(3));
                        Matcher keyMatcher = keyPattern.matcher(changeLine.group(1));
                        if (keyMatcher.find()) {
                            String otype = keyMatcher.group(2);
                            String oid = keyMatcher.group(3);
                            changeLog.put(changeLine.group(1), new Change(oid, otype, Storage.Status.valueOf(changeLine.group(2).toUpperCase()), date));
                        }
                        else {
                            logger.error("Invalid key format for changelog line: "+line);
                        }
                    }
                    catch (ParseException e) {
                        logger.error("Invalid date format for changeLog line:"+line,e);
                    }
                }
                else {
                    logger.fatal("Malformed change line: "+line);
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Appends change information to the changeLog
     *
     * @param key
     * @param storage
     * @param block
     */
    public static void record(String key, Storage storage)
    {
        Matcher keyMatcher = keyPattern.matcher(key);
        if (!keyMatcher.find()) {
            logger.error("Invalid changelog key: "+key);
            return;
        }

        String otype = keyMatcher.group(2);
        String oid = keyMatcher.group(3);

        Change change = changeLog.get(key);
        if (change == null) {
            changeLog.put(key, new Change(oid, otype, storage.status(key), ChangeLogger.datetime));
        }
        else if (change.getStatus() == Status.DELETED) {
            // If it was previously deleted, make it new
            changeLog.put(key, new Change(oid, otype, Status.NEW, ChangeLogger.datetime));
        }
        else if (change.getStatus() != Status.NEW) {
            // Don't change a status marked as NEW
            changeLog.put(key, new Change(oid, otype, Status.MODIFIED, ChangeLogger.datetime));
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
            // Otherwise make sure to leave a trace of the object
            Matcher keyMatcher = keyPattern.matcher(key);
            if (keyMatcher.find()) {
                String otype = keyMatcher.group(2);
                String oid = keyMatcher.group(3);
                changeLog.put(key, new Change(oid, otype, Status.DELETED, ChangeLogger.datetime));
            }
            else {
                logger.error("Invalid changelog key: "+key);
            }
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
